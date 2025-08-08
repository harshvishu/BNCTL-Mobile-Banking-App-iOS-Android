package tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.iban

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.accounts.model.Beneficiary
import tl.bnctl.banking.data.payee.model.Payee
import tl.bnctl.banking.data.templates.TemplatesDataSource
import tl.bnctl.banking.data.templates.model.Bank
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.templates.model.TemplateRequest
import tl.bnctl.banking.data.transfers.enums.TransferExecution
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.data.transfers.model.Transfer
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.data.transfers.model.TransferSummary
import tl.bnctl.banking.ui.banking.fragments.transfers.BaseTransferFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.destination.newpayee.NewPayeeViewModel
import tl.bnctl.banking.util.LocaleHelper

open class IbanTransferFragment : BaseTransferFragment() {

    protected val destinationAccountViewModel: NewPayeeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding.transferTypeTitle.text = resources.getString(R.string.transfer_type_interbank)
        val filterCurrency =
            resources.getStringArray(R.array.national_iban_currencies)[0].toString()
        sourceAccountViewModel.filterRequirement =
            { account: Account -> account.currencyName == filterCurrency }
        detailsViewModel.setAvailableCurrencies(R.array.national_iban_currencies)
        detailsViewModel.disableCurrenciesOption()
        sourceAccountViewModel.selectedAccount.observe(viewLifecycleOwner) {
            if (detailsViewModel.selectedCurrency.value == null && it != null) {
                detailsViewModel.selectCurrency(it.currencyName)
            }
        }
        return view
    }

    override fun populateViewFromTemplate(template: Template) {
        super.populateViewFromTemplate(template)
        destinationAccountViewModel.setBeneficiary(template.name)
        if (destinationAccountViewModel.destinationAccount.value == null) {
            destinationAccountViewModel.setDestinationAccount(template.accountNumber)
        }
    }

    override fun onValidationFailure(transferEnd: TransferEnd) {
        requireView().findNavController()
            .navigate(
                IbanTransferFragmentDirections.actionNavFragmentIbanTransferToNavFragmentTransferEnd(
                    transferEnd
                )
            )
    }

    override fun onValidationSuccess(summary: TransferSummary) {
        requireView().findNavController()
            .navigate(
                IbanTransferFragmentDirections.actionNavFragmentIbanTransferToNavFragmentTransferSummary(
                    summary
                )
            )
    }

    override fun isInputValid(): Boolean {
        destinationAccountViewModel.validateBeneficiary()
        destinationAccountViewModel.validateDestinationAccountNumber(R.string.error_transfer_validation_same_source_and_destination) { iban ->
            iban != sourceAccountViewModel.selectedAccount.value?.iban
        }
        // Check if there are errors after the first validation and then make the second one
        if (destinationAccountViewModel.destinationAccountError.value == null) {
            destinationAccountViewModel.validateDestinationAccountNumber(R.string.error_transfer_validation_invalid_bulgarian_iban) { iban ->
                iban.startsWith(getString(R.string.transfers_country_iban_prefix))
            }
        }
        return super.isInputValid()
                && destinationAccountViewModel.destinationAccountError.value == null
                && destinationAccountViewModel.beneficiaryNameError.value == null
    }

    override fun generateSummary(
        feeChange: String,
        feeCurrency: String,
        destinationCustomerName: String?
    ): TransferSummary {
        val summary = super.generateSummary(
            feeChange,
            feeCurrency,
            destinationCustomerName
        )

        if (destinationCustomerName != null) {
            destinationAccountViewModel.setBeneficiary(destinationCustomerName)
        }
        var beneficiary = Beneficiary(destinationAccountViewModel.beneficiaryName.value!!)

        summary.to =
            Payee(
                beneficiary,
                destinationAccountViewModel.destinationAccount.value!!
            )
        summary.transferType = TransferType.INTERBANK

        if (
            destinationAccountViewModel.addToPayeeList.value != null &&
            destinationAccountViewModel.addToPayeeList.value!!
        ) {
            summary.newPayee = generateNewPayee(summary)
        }
        return summary
    }

    override fun generateTransfer(): Transfer {
        val transfer = Transfer(
            detailsViewModel.amount.value!!.toString(),
            detailsViewModel.reason.value!!.toString(),
            detailsViewModel.selectedCurrency.value!!.toString(),
            sourceAccountViewModel.selectedAccount.value!!.iban,
            sourceAccountViewModel.selectedAccount.value!!.accountId,
            sourceAccountViewModel.selectedAccount.value!!.currencyName,
            destinationAccountViewModel.destinationAccount.value!!.toString(),
            destinationAccountViewModel.beneficiaryName.value!!.toString(),
            TransferType.INTERBANK.type,
            TransferExecution.NOW.time,
            mapOf(
                "transactionType" to detailsViewModel.type.value.toString()
            ),
            null
        )
        if (
            destinationAccountViewModel.addToPayeeList.value != null &&
            destinationAccountViewModel.addToPayeeList.value!!
        ) {
            transfer.newPayee = generateNewPayee(transfer)
        }
        return transfer
    }

    override fun generateNewPayee(transfer: Transfer): TemplateRequest? {
        val bank = destinationAccountViewModel.bank
        return TemplateRequest(
            "bank",
            transfer.destinationAccount,
            TemplatesDataSource.TRANSFER_TYPE_TO_TEMPLATE_TYPE_ID[transfer.transferType.lowercase()],
            transfer.destinationAccountHolder,
            null,
            null,
            null,
            bank.value?.name,
            bank.value?.swift,
            destinationAccountViewModel.email.value,
            transfer.destinationAccountCurrency,
            LocaleHelper.getCurrentLanguage(requireContext()),
            true
        )
    }

    private fun generateNewPayee(summary: TransferSummary): TemplateRequest {
        val bank = destinationAccountViewModel.bank
        return TemplateRequest(
            "bank",
            summary.to.accountNumber,
            TemplatesDataSource.TRANSFER_TYPE_TO_TEMPLATE_TYPE_ID[summary.transferType.toString()
                .lowercase()],
            summary.to.beneficiary.name,
            null,
            null,
            null,
            bank.value?.name,
            bank.value?.swift,
            destinationAccountViewModel.email.value,
            summary.currency,
            LocaleHelper.getCurrentLanguage(requireContext()),
            true
        )
    }

    override fun onBanksLoaded(banksResult: Result<List<Bank>>) {
        if (banksResult is Result.Success && banksResult.data.isNotEmpty()) {
            destinationAccountViewModel.setBank(banksResult.data[0])
        }
    }

    override fun onPendingTransferCreateResult(transferEnd: TransferEnd, subMessage: String) {
        IbanTransferFragmentDirections.actionNavFragmentIbanTransferToNavFragmentTransferEnd(
            transferEnd,
            null,
            transferEnd.transferSuccess,
            subMessage
        )
    }
}