package tl.bnctl.banking.ui.banking.fragments.transfers.confirm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.transfers.model.TransferConfirmRequest
import tl.bnctl.banking.data.transfers.model.TransferConfirmResult
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.data.transfers.model.TransferStatus
import tl.bnctl.banking.databinding.FragmentTransferConfirmBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.utils.DialogFactory

class TransferConfirmFragment : BaseFragment() {

    private var _binding: FragmentTransferConfirmBinding? = null
    private val binding get() = _binding!!

    private val transferConfirmFragmentArgs: TransferConfirmFragmentArgs by navArgs()

    private val transferConfirmViewModel: TransferConfirmViewModel by viewModels { TransferConfirmViewModelFactory() }

    // Dialogs
    private var loadingDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentTransferConfirmBinding.inflate(inflater, container, false)
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            showTransferCancelPromptDialog()
            true
        }
        binding.toolbar.menu[0].isVisible = false

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            showTransferCancelPromptDialog()
        }
        setUpUI()
        setUpObservers()
        return binding.root
    }

    private fun showTransferCancelPromptDialog() {
        DialogFactory.createConfirmDialog(
            requireContext(),
            R.string.common_dialog_message_cancel_transaction,
            doOnConfirmation = {
                findNavController().popBackStack()
            }
        ).show()
    }

    private fun setUpUI() {
        binding.transferConfirmConfirmButton.setOnClickListener {
            if (validateForm()) {
                val secretField = binding.transferConfirmOtpSecret
                secretField.requestFocus()
                secretField.clearFocus()
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(
                        secretField.windowToken,
                        0
                    )
                showLoadingDialog()
                transferConfirmViewModel.startTransferConfirmation(
                    TransferConfirmRequest(
                        transferConfirmFragmentArgs.transferConfirm!!.validationRequestId,
                        secretField.text.toString()
                    )
                )
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.transferConfirmOtpSecret.text.isNullOrBlank()) {
            binding.transferConfirmOtpSecret.error = getString(R.string.common_error_field_required)
            binding.transferConfirmOtpSecret.requestFocus()

            return false
        } else {
            binding.transferConfirmOtpSecret.error = null
        }
        return true
    }

    private fun setUpObservers() {
        transferConfirmViewModel.confirmTransferResult.observe(viewLifecycleOwner) {
            loadingDialog?.dismiss()
            binding.transferConfirmOtpSecretLayout.error = null

            if (it.success != null) {
                goToSuccessScreen(it)
            }
            if (it.error != null) run {
                loadingDialog?.hide()
                val errorString: String? = parseOTPConfirmError(it.error)
                if (errorString == null) {
                    val stringId =
                        resources.getIdentifier(
                            it.error.getErrorString(),
                            "string",
                            requireActivity().packageName
                        )
                    val errorStringId =
                        if (stringId == 0) R.string.error_transfer_confirm else stringId
                    handleResultError(it.error, errorStringId, false)
                    requireView().findNavController()
                        .navigate(
                            TransferConfirmFragmentDirections.actionNavFragmentTransferConfirmToNavFragmentTransferEnd(
                                TransferEnd(
                                    false,
                                    resources.getString(errorStringId)
                                )
                            )
                        )
                } else {
                    showOTPVerificationError(errorString)
                }

            }
        }
    }

    private fun showOTPVerificationError(errorString: String) {
        binding.transferConfirmOtpSecretLayout.error = errorString
    }

    /**
     * This piece of gold is copied directly from:
     * wave-ibs-front-core/src/transfer/helpers.js#checkFailOTPReason
     */
    private fun parseOTPConfirmError(result: Result.Error): String? {
        val messageSplit = result.message.split(": ")
        val remainingAttempts = if (messageSplit.size > 1) messageSplit[1] else ""

        return when (messageSplit[0]) {
            "Expired action authorization." -> resources.getString(R.string.error_authentication_otp_expired)
            "Invalid action authorization. Remaining attempts" -> resources.getString(
                R.string.error_authentication_otp_invalid,
                remainingAttempts
            )
            "The action is already confirmed." -> resources.getString(R.string.error_authentication_otp_alreadyConfirmed)
            // Not "parsing" this case so we can show the fail screen when the OTP has been incorrectly entered more than the allowed times
            //"The action is locked." -> resources.getString(R.string.error_authentication_otp_locked)
            else -> null
        }
    }

    private fun goToSuccessScreen(transferConfirmResult: TransferConfirmResult) {
        requireView().findNavController()
            .navigate(
                TransferConfirmFragmentDirections.actionNavFragmentTransferConfirmToNavFragmentTransferEnd(
                    TransferEnd(
                        true,
                        getString(R.string.transfer_confirm_payment_success)
                    ),
                    transferConfirmFragmentArgs.redirectToAccountId,
                    true,
                    getSuccessMessage(transferConfirmResult)
                )
            )
    }

    private fun getSuccessMessage(transferConfirmResult: TransferConfirmResult): String {
        val result = transferConfirmResult.success!!
        return when (result.status) {
            TransferStatus.SUCCESS ->
                getString(
                    R.string.transfer_end_success_accepted,
                    result.transferId,
                    result.transferIdIssuer
                )
            TransferStatus.PENDING, TransferStatus.STORED -> getString(
                R.string.transfer_end_success_stored,
                result.transferId
            )
            else -> ""
        }
    }

    private fun showLoadingDialog() {
        loadingDialog = DialogFactory.createLoadingDialog(
            requireActivity(),
            R.string.common_please_wait
        )
        loadingDialog!!.show()
    }

    // Depending on the data received by navigation it'll execute different flow
    // Use transferConfirm if you're doing the standard maker/checker flow and transferExecute if you are doing direct execution for the transfer
    private fun checkAndStartCorrespondingFlow() {
        val transferExecute = transferConfirmFragmentArgs.transferExecute
        if (transferExecute != null) {
            transferConfirmViewModel.startTransferExecution(transferExecute)
        } else {
            transferConfirmViewModel.startTransferConfirmation(transferConfirmFragmentArgs.transferConfirm!!)
        }
    }

}