package tl.bnctl.banking.ui.banking.fragments.transfers.pending.confirm

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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.fallback.FallbackRequestParams
import tl.bnctl.banking.data.transfers.model.PendingTransferConfirmOrRejectResult
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.data.transfers.model.TransferStatus
import tl.bnctl.banking.databinding.FragmentTransferConfirmBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.pending.PendingTransfersViewModel
import tl.bnctl.banking.ui.banking.fragments.transfers.pending.PendingTransfersViewModelFactory
import tl.bnctl.banking.ui.fallback.FallbackConfirmActivity
import tl.bnctl.banking.ui.fallback.FallbackPromptDialog
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.util.Constants

class PendingTransferConfirmFragment : BaseFragment() {

    private var _binding: FragmentTransferConfirmBinding? = null
    private val binding get() = _binding!!

    private val pendingTransfersViewModel: PendingTransfersViewModel by navGraphViewModels(R.id.nav_graph_transfers) { PendingTransfersViewModelFactory() }

    private val pendingTransferConfirmFragmentArgs: PendingTransferConfirmFragmentArgs by navArgs()

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
                    confirmOrRejectPendingTransfer(smsCode, pin)
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

        val action = requireArguments().getString("action")!!
        val labelIdentifier = resources.getIdentifier(
            "transfer_${action}_title_confirm_payment",
            "string", requireContext().packageName
        )
        val messageIdentifier = resources.getIdentifier(
            "transfer_${action}_payment_message",
            "string", requireContext().packageName
        )

        binding.transferConfirmLabel.text = resources.getString(labelIdentifier)
        binding.transferConfirmMessage.text = resources.getString(messageIdentifier)

        val observer: (Result<PendingTransferConfirmOrRejectResult>) -> Unit = {
            loadingDialog?.dismiss()
            if (it is Result.Success) {
                val msgIdentifier = resources.getIdentifier(
                    "transfer_${action}_payment_success",
                    "string", requireContext().packageName
                )
                if (it.data.status == TransferStatus.WAITING_FOR_FALLBACK_CONFIRMATION) {
                    pendingTransfersViewModel.setUsePin(it.data.fallback?.usePin == true)
                    fallbackConfirm.launch(
                        getFallbackConfirmIntent(
                            it.data.fallback?.usePin == true,
                            false
                        )
                    )
                } else {
                    requireView().findNavController()
                        .navigate(
                            PendingTransferConfirmFragmentDirections.actionNavFragmentPendingTransferConfirmToNavFragmentTransferEnd(
                                TransferEnd(
                                    true,
                                    resources.getString(msgIdentifier)
                                )
                            )
                        )
                }
            } else if (it is Result.Error) {
                val error: Result.Error = it
                loadingDialog?.hide()
                // Show fallback dialog
                when (error.target) {
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
                                pendingTransfersViewModel.usePin.value == true,
                                true
                            )
                        )
                    }
                    else -> {
                        val errorString: String = error.getErrorString()
                        val stringId =
                            resources.getIdentifier(
                                errorString,
                                "string",
                                requireActivity().packageName
                            )
                        val errorStringId =
                            if (stringId == 0) R.string.error_transfer_confirm else stringId
                        handleResultError(error, errorStringId, false)
                        requireView().findNavController()
                            .navigate(
                                PendingTransferConfirmFragmentDirections.actionNavFragmentPendingTransferConfirmToNavFragmentTransferEnd(
                                    TransferEnd(
                                        false,
                                        resources.getString(errorStringId)
                                    )
                                )
                            )
                    }
                }
            } else {
                requireView().findNavController()
                    .navigate(
                        PendingTransferConfirmFragmentDirections.actionNavFragmentPendingTransferConfirmToNavFragmentTransferEnd(
                            TransferEnd(
                                false,
                                resources.getString(R.string.error_transfer_creation)
                            )
                        )
                    )
            }

        }

        when (action) {
            "confirm" -> pendingTransfersViewModel.confirmationRequestResult.observe(
                viewLifecycleOwner,
                observer
            )
            "reject" -> pendingTransfersViewModel.rejectionRequestResult.observe(
                viewLifecycleOwner,
                observer
            )
        }

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
                            pendingTransfersViewModel.usePin.value == true,
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
        val fallbackParams = FallbackRequestParams(true, null, null)
        showLoadingDialog()
        when (pendingTransferConfirmFragmentArgs.action) {
            "confirm" -> pendingTransfersViewModel.confirmSelectedPendingTransfers(fallbackParams)
            "reject" -> pendingTransfersViewModel.rejectSelectedPendingTransfers(fallbackParams)
        }
    }

    private fun confirmOrRejectPendingTransfer(smsCode: String?, pin: String?) {
        val fallbackParams = FallbackRequestParams(true, smsCode, pin)
        when (pendingTransferConfirmFragmentArgs.action) {
            "confirm" -> pendingTransfersViewModel.confirmSelectedPendingTransfers(fallbackParams)
            "reject" -> pendingTransfersViewModel.rejectSelectedPendingTransfers(fallbackParams)
        }
    }

    private fun showLoadingDialog() {
        loadingDialog = DialogFactory.createLoadingDialog(
            requireActivity(),
            R.string.common_please_wait
        )
        loadingDialog!!.show()
    }

}