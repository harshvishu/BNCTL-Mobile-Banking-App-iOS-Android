package tl.bnctl.banking.ui.banking.fragments.transfers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.AccountPermissions
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.accounts.model.Beneficiary
import tl.bnctl.banking.data.payee.model.Payee
import tl.bnctl.banking.data.templates.model.Bank
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.templates.model.TemplateRequest
import tl.bnctl.banking.data.transfers.enums.TransferExecution
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.data.transfers.model.Transfer
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.data.transfers.model.TransferSummary
import tl.bnctl.banking.data.transfers.model.TransferValidateResult
import tl.bnctl.banking.databinding.FragmentTransferBaseBinding
import tl.bnctl.banking.services.PermissionService
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source.SelectAccountViewModelFactory
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source.SelectSourceAccountViewModel
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.details.TransferDetailsType
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.details.TransferDetailsViewModel
import tl.bnctl.banking.ui.banking.fragments.transfers.create.validation.TransferValidateViewModel
import tl.bnctl.banking.ui.banking.fragments.transfers.create.validation.TransferValidateViewModelFactory
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.util.camelToSnakeCase

abstract class BaseTransferFragment : BaseFragment() {

    private var _binding: FragmentTransferBaseBinding? = null
    protected val binding get() = _binding!!

    // ViewModels used by this fragment's children
    protected val sourceAccountViewModel: SelectSourceAccountViewModel
            by viewModels { SelectAccountViewModelFactory() }
    protected val detailsViewModel: TransferDetailsViewModel
            by viewModels()

    // ViewModels important for the Transfer flow
    private val transferValidateViewModel: TransferValidateViewModel
            by viewModels { TransferValidateViewModelFactory() }

    private var isNextButtonClicked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTransferBaseBinding.inflate(inflater, container, false)
        sourceAccountViewModel.accountFetch(this.javaClass.toString())
        transferValidateViewModel.validationResult.observe(viewLifecycleOwner) {
            validateResult(it)
        }
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            findNavController().popBackStack()
        }

        transferValidateViewModel.fetchBanks()
        transferValidateViewModel.banksResult.observe(viewLifecycleOwner) {
            onBanksLoaded(it)
        }
        sourceAccountViewModel.selectedAccount.observe(viewLifecycleOwner) {
            onSourceAccountChanged(it)
        }
        setupNextButton()
        setupInitiatePendingTransferButton()
        checkForTemplate()
        return binding.root
    }

    private fun setupNextButton() {
        val nextButton: Button = binding.transferBaseNext
        nextButton.isEnabled = true
        nextButton.setOnClickListener {
            isNextButtonClicked = true
            transferValidateViewModel.checkEoD()
        }
        if (!transferValidateViewModel.eodResult.hasActiveObservers()) {
            transferValidateViewModel.eodResult.observe(viewLifecycleOwner) {
                if (isNextButtonClicked) {
                    isNextButtonClicked = false
                    if (it.error?.target.equals("sessionExpired")) {
                        DialogFactory.createSessionExpiredDialog(requireActivity()).show()
                    } else if (transferValidateViewModel.eodResult.value != null) {
                        if (transferValidateViewModel.eodResult.value!!.eodStarted) {
                            DialogFactory.createConfirmDialog(
                                requireContext(),
                                R.string.transfer_form_eod_started,
                                R.string.common_ok,
                                R.string.common_cancel
                            ) {
                                submitForm()
                            }.show()
                        } else {
                            submitForm()
                        }
                    }
                }
            }
        }
    }

    private fun setupInitiatePendingTransferButton() {
        val button: Button = binding.transferBaseInitiatePending
        button.isEnabled = true
        button.setOnClickListener {
            isNextButtonClicked = true
            submitPendingTransferForm()
        }
        transferValidateViewModel.pendingTransferResult.observe(viewLifecycleOwner) {
            val success: Boolean
            val message: String
            val subMessage: String

            if (it is Result.Success) {
                success = true
                message = getString(R.string.common_label_success)
                subMessage =
                    getString(R.string.transfer_end_success_initiated, it.data.transferId)
            } else {
                success = false
                subMessage = ""
                val errorString = (it as Result.Error).getErrorString()
                val stringId =
                    resources.getIdentifier(errorString, "string", requireActivity().packageName)
                val errorStringId =
                    if (stringId == 0) R.string.error_transfer_creation else stringId
                message = getString(errorStringId)
            }
            onPendingTransferCreateResult(
                TransferEnd(
                    success,
                    message
                ),
                subMessage
            )
        }
    }

    private fun checkForTemplate() {
        val template = requireArguments().get("template")
        if (template != null && template is Template) {
            populateViewFromTemplate(template)
        }
    }

    protected open fun populateViewFromTemplate(template: Template) {
        if (template.sourceAccount != null) {
            sourceAccountViewModel.preselectAccount(template.sourceAccount!!)
        }
        /*if (detailsViewModel.amount.value == null) {
            detailsViewModel.setAmount(template.amount)
        }*/

        if (detailsViewModel.reason.value == null && template.description != null) {
            detailsViewModel.setReason(template.description!!)
        }
        if (detailsViewModel.selectedCurrency.value == null) {
            detailsViewModel.selectCurrency(template.currency)
        }
        when (template.transactionType?.lowercase()) {
            TransferDetailsType.STANDARD.type -> detailsViewModel.changeType(TransferDetailsType.STANDARD)
            TransferDetailsType.RINGS.type -> detailsViewModel.changeType(TransferDetailsType.RINGS)
        }
    }

    private fun submitForm() {
        clearFocusAndHideKeyboard()
        if (isInputValid()) {
            binding.transferBaseNext.isEnabled = false
            transferValidateViewModel.startTransferValidation(generateTransfer())
        }
    }

    private fun submitPendingTransferForm() {
        clearFocusAndHideKeyboard()
        if (isInputValid()) {
            binding.transferBaseInitiatePending.isEnabled = false
            transferValidateViewModel.createPendingTransfer(generateTransfer())
        }
    }

    private fun clearFocusAndHideKeyboard() {
        val transferBase = binding.transferBase
        transferBase.requestFocus()
        transferBase.clearFocus()
        // TODO: check for other approach on focus clear with keyboard close
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(
                transferBase.windowToken,
                0
            )
    }

    private fun validateResult(validationResult: TransferValidateResult) {
        if (validationResult.success != null) {
            if (validationResult.success.canCreateTransfer) {
                onValidationSuccess(
                    generateSummary(
                        validationResult.success.chargeAmount.toString(),
                        validationResult.success.chargeCurrency,
                        validationResult.success.destinationCustomerName
                    )
                )
            } else {
                val stringId =
                    resources.getIdentifier(
                        validationResult.success.status.camelToSnakeCase(),
                        "string",
                        requireActivity().packageName
                    )
                val errorStringId =
                    if (stringId == 0) R.string.error_generic else stringId
                onValidationFailure(
                    TransferEnd(
                        false,
                        getString(errorStringId)
                    )
                )
            }
        }
        if (validationResult.error != null) run {
            val errorString = validationResult.error.getErrorString()
            val stringId =
                resources.getIdentifier(errorString, "string", requireActivity().packageName)
            val errorStringId =
                if (stringId == 0) R.string.error_transfer_validation else stringId
            handleResultError(validationResult.error, errorStringId, false)
            onValidationFailure(
                TransferEnd(
                    false,
                    resources.getString(errorStringId)
                )
            )
        }
    }

    protected abstract fun onValidationFailure(transferEnd: TransferEnd)

    protected abstract fun onValidationSuccess(summary: TransferSummary)

    protected abstract fun onPendingTransferCreateResult(
        transferEnd: TransferEnd,
        subMessage: String
    )

    protected open fun isInputValid(): Boolean {
        val isSourceAccountSelected = sourceAccountViewModel.selectedAccount.value != null
        if (!isSourceAccountSelected) {
            sourceAccountViewModel.raiseSelectAccountError(R.string.transfer_select_account_select_select_account)
        } else {
            sourceAccountViewModel.clearSelectAccountError()
        }

        detailsViewModel.validateAmount()
        detailsViewModel.validateReason()

        // Check balance
        if (sourceAccountViewModel.selectAccountError.value == null) {
            val availableBalance = sourceAccountViewModel.selectedAccount.value!!.balance.available
            if (
                detailsViewModel.amountError.value == null &&
                (availableBalance <= 0 || detailsViewModel.amount.value!! > availableBalance)
            ) {
                detailsViewModel.raiseAmountError(R.string.error_transfer_validation_insufficient_funds)
            } else {
                detailsViewModel.clearAmountError()
            }
        }

        return sourceAccountViewModel.isSelectedAccountValid()
                && detailsViewModel.amountError.value == null
                && detailsViewModel.reasonError.value == null
                && detailsViewModel.isCurrencyValid()
    }

    protected open fun generateSummary(
        feeChange: String,
        feeCurrency: String,
        destinationCustomerName: String?
    ): TransferSummary {
        var additionalDetails = mapOf<String, Any>()
        if (detailsViewModel.enableType.value == true) {
            additionalDetails = mapOf(
                "transactionType" to detailsViewModel.type.value.toString()
            )
        }
        val beneficiaryName = destinationCustomerName ?: ""
        return TransferSummary(
            detailsViewModel.amount.value!!,
            detailsViewModel.selectedCurrency.value!!,
            feeChange.toDouble(),
            feeCurrency,
            sourceAccountViewModel.selectedAccount.value!!,
            Payee(Beneficiary(beneficiaryName), ""),
            detailsViewModel.reason.value!!,
            TransferType.BETWEENACC,
            TransferExecution.NOW,
            additionalDetails
        )
    }

    protected abstract fun generateTransfer(): Transfer

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        transferValidateViewModel.clearValidationResult()
    }

    abstract fun generateNewPayee(transfer: Transfer): TemplateRequest?

    protected abstract fun onBanksLoaded(banksResult: Result<List<Bank>>)

    /**
     * Similar to the internet banking, we need to show different buttons according to the user's
     * permissions for the selected account
     */
    protected open fun onSourceAccountChanged(account: Account?) {
        if (account == null) {
            return
        }
        val hasPermissionToExecute = (PermissionService.getInstance().accountHasPermission(
            listOf(AccountPermissions.APPROVE.permission, AccountPermissions.INITIATE.permission),
            account
        ))
        val hasPermissionToInitiate = (PermissionService.getInstance().accountHasPermission(
            listOf(AccountPermissions.INITIATE.permission),
            account
        ))
        if (!hasPermissionToExecute && hasPermissionToInitiate) {
            binding.transferBaseNext.visibility = View.GONE
            binding.transferBaseInitiatePending.visibility = View.VISIBLE
        } else if (hasPermissionToExecute) {
            binding.transferBaseNext.visibility = View.VISIBLE
            binding.transferBaseInitiatePending.visibility = View.GONE
        }
    }

}
