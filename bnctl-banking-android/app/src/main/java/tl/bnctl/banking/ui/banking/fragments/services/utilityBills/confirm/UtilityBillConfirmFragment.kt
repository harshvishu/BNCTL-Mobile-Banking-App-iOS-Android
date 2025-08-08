package tl.bnctl.banking.ui.banking.fragments.services.utilityBills.confirm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.R
import tl.bnctl.banking.data.fallback.FallbackRequestParams
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.data.transfers.model.TransferStatus
import tl.bnctl.banking.databinding.FragmentTransferConfirmBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.fallback.FallbackConfirmActivity
import tl.bnctl.banking.ui.fallback.FallbackPromptDialog
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.util.Constants

class UtilityBillConfirmFragment : BaseFragment() {

    private var _binding: FragmentTransferConfirmBinding? = null
    private val binding get() = _binding!!

    private val utilityBillConfirmFragmentArgs: UtilityBillConfirmFragmentArgs by navArgs()

    private val utilityBillConfirmViewModel: UtilityBillConfirmViewModel by viewModels { UtilityBillConfirmViewModelFactory() }

    // Dialogs
    private var loadingDialog: AlertDialog? = null
    private var fallbackPromptDialog: FallbackPromptDialog? = null

    // Fallback activity
    private val fallbackConfirm =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            fallbackPromptDialog?.dismiss()
            loadingDialog?.dismiss()
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val smsCode =
                        result.data?.getStringExtra(FallbackConfirmActivity.EXTRA_SMS_CODE_KEY)
                    val pin = result.data?.getStringExtra(FallbackConfirmActivity.EXTRA_PIN_KEY)
                    showLoadingDialog()
                    startBillPaymentWithFallback(smsCode, pin)
                }
                Activity.RESULT_CANCELED -> {
                    showTransferCancelPromptDialog(reshowFallbackConfirm = true)
                }
                else -> {
                    findNavController().popBackStack()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferConfirmBinding.inflate(inflater, container, false)
        utilityBillConfirmViewModel.confirmBillPaymentResult.observe(viewLifecycleOwner) {
            loadingDialog?.dismiss()
            if (it.success != null) {
                if (it.success.status == TransferStatus.WAITING_FOR_FALLBACK_CONFIRMATION) {
                    utilityBillConfirmViewModel.setUsePin(it.success.usePin)
                    fallbackConfirm.launch(getFallbackConfirmIntent(it.success.usePin, false))
                } else {
                    requireView().findNavController()
                        .navigate(
                            UtilityBillConfirmFragmentDirections.actionNavFragmentUtilityBillsConfirmToNavFragmentTransferEnd(
                                TransferEnd(
                                    true,
                                    getString(R.string.transfer_confirm_payment_success)
                                ),
                                utilityBillConfirmFragmentArgs.redirectToAccountId
                            )
                        )
                }
            }
            if (it.error != null) run {

                loadingDialog?.hide()
                // Show fallback dialog
                when (it.error.target) {
                    // SCA is unreachable, ask user if they want to use SMS to confirm
                    Constants.ERROR_SCA_ERROR,
                    Constants.ERROR_SCA_EXPIRED,
                    Constants.ERROR_FALLBACK_AGREEMENT -> {
                        showFallbackPromptDialog()
                    }
                    // Wrong SMS code entered, show the SMS prompt again
                    Constants.ERROR_FALLBACK_INVALID_SMS -> {
                        fallbackConfirm.launch(
                            getFallbackConfirmIntent(
                                utilityBillConfirmViewModel.usePin.value == true,
                                true
                            )
                        )
                    }
                    else -> {
                        ////
                        val errorString = it.error.getErrorString()
                        val stringId =
                            resources.getIdentifier(
                                errorString,
                                "string",
                                requireActivity().packageName
                            )
                        val errorStringId =
                            if (stringId == 0) R.string.error_transfer_confirm else stringId
                        requireView().findNavController()
                            .navigate(
                                UtilityBillConfirmFragmentDirections.actionNavFragmentUtilityBillsConfirmToNavFragmentTransferEnd(
                                    TransferEnd(
                                        false,
                                        resources.getString(errorStringId)
                                    )
                                )
                            )
                    }
                }
            }
        }
        utilityBillConfirmViewModel.startBillPaymentConfirmation(
            utilityBillConfirmFragmentArgs.transferConfirm,
            null
        )
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            DialogFactory.createConfirmDialog(
                requireContext(),
                R.string.common_dialog_message_cancel_transaction
            ) {
                findNavController().popBackStack()
            }.show()
            true
        }
        return binding.root
    }

    private fun showFallbackPromptDialog() {
        fallbackPromptDialog = DialogFactory.createFallBackPromptDialog(
            requireContext(),
            onUseFallbackClickHandler = onUseFallBack,
            onCancelHandler = onFallbackPromptCancel
        )
        fallbackPromptDialog!!.show()
    }

    private fun getFallbackConfirmIntent(usePin: Boolean, wrongSms: Boolean): Intent {
        val intent = Intent(requireContext(), FallbackConfirmActivity::class.java)

        if (usePin) {
            intent.putExtra(
                FallbackConfirmActivity.EXTRA_TITLE_TEXT_KEY,
                R.string.fallback_confirm_transfer_via_sms_and_pin
            )
        } else {
            intent.putExtra(
                FallbackConfirmActivity.EXTRA_TITLE_TEXT_KEY,
                R.string.fallback_confirm_transfer_via_sms
            )
        }
        intent.putExtra(FallbackConfirmActivity.EXTRA_WRONG_SMS_CODE_KEY, wrongSms)
        intent.putExtra(FallbackConfirmActivity.EXTRA_USE_PIN_KEY, usePin)

        return intent
    }

    private val onFallbackPromptCancel: View.OnClickListener = View.OnClickListener {
        showTransferCancelPromptDialog(
            reshowFallbackConfirm = false,
            reshowFallbackPrompt = true
        )
        fallbackPromptDialog!!.dismiss()

    }

    private val onUseFallBack: View.OnClickListener = View.OnClickListener {
        fallbackPromptDialog?.dismiss()
        showLoadingDialog()
        startBillPaymentWithFallback(null, null)
    }

    private fun startBillPaymentWithFallback(smsCode: String?, pin: String?) {
        val fallbackParams = FallbackRequestParams(true, smsCode, pin)

        utilityBillConfirmViewModel.startBillPaymentConfirmation(
            utilityBillConfirmFragmentArgs.transferConfirm,
            fallbackParams
        )
    }


    private fun showLoadingDialog() {
        loadingDialog = DialogFactory.createLoadingDialog(
            requireActivity(),
            R.string.common_please_wait
        )
        loadingDialog!!.show()
    }

    /**
     * You can get here from multiple locations.
     * One of those locations is when cancelling in the fallback screen, the other is the "use fallback" prompt.
     * Depending on the parameters, you can re-show the fallback prompt dialog or re-launch the fallback confirm activity
     */
    private fun showTransferCancelPromptDialog(
        reshowFallbackConfirm: Boolean = false,
        reshowFallbackPrompt: Boolean = false
    ) {
        DialogFactory.createConfirmDialog(
            requireContext(),
            R.string.common_dialog_message_cancel_transaction,
            doOnConfirmation = {
                findNavController().popBackStack()
            },
            doOnCancel = {
                if (reshowFallbackConfirm) {
                    fallbackConfirm.launch(
                        getFallbackConfirmIntent(
                            utilityBillConfirmViewModel.usePin.value == true,
                            false
                        )
                    )
                }
                if (reshowFallbackPrompt) {
                    showFallbackPromptDialog()
                }
            }
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}