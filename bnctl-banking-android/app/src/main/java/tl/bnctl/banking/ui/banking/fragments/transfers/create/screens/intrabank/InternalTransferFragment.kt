package tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.intrabank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.accounts.model.Beneficiary
import tl.bnctl.banking.data.payee.model.Payee
import tl.bnctl.banking.data.templates.TemplatesDataSource
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.templates.model.TemplateRequest
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.data.transfers.model.Transfer
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.data.transfers.model.TransferSummary
import tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.account.AccountTransferFragment

class InternalTransferFragment : AccountTransferFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding.transferTypeTitle.text =
            resources.getString(R.string.transfer_type_intrabank)
        sourceAccountViewModel.filterRequirement = { true }
        detailsViewModel.setAvailableCurrencies(R.array.internal_transfer_currencies)
        detailsViewModel.disableTypeOption()
        detailsViewModel.disableCurrenciesOption()
        destinationAccountViewModel.validateIbanIfItIsForCurrentBank = true
        // Set transfer type
        destinationAccountViewModel.setTransferType(TransferType.INTRABANK)
        detailsViewModel.disableCurrenciesOption() // No currencies other than USD in BNCTL.

        return view
    }

    override fun onValidationFailure(transferEnd: TransferEnd) {
        requireView().findNavController()
            .navigate(
                InternalTransferFragmentDirections.actionNavFragmentInternalTransferToNavFragmentTransferEnd(
                    transferEnd
                )
            )
    }

    override fun onValidationSuccess(summary: TransferSummary) {
        requireView().findNavController()
            .navigate(
                InternalTransferFragmentDirections.actionNavFragmentInternalTransferToNavFragmentTransferSummary(
                    summary
                )
            )
    }

    override fun onPendingTransferCreateResult(transferEnd: TransferEnd, subMessage: String) {
        requireView().findNavController()
            .navigate(
                InternalTransferFragmentDirections.actionNavFragmentInternalTransferToNavFragmentTransferEnd(
                    transferEnd,
                    null,
                    transferEnd.transferSuccess,
                    subMessage
                )
            )
    }

    override fun populateViewFromTemplate(template: Template) {
        super.populateViewFromTemplate(template)
        destinationAccountViewModel.hideAddToPayeeListCheckbox();
    }

    override fun generateTransfer(): Transfer {
        val transfer = super.generateTransfer()
        transfer.transferType = TransferType.INTRABANK.type
        transfer.additionalDetails = mapOf()
        if (
            destinationAccountViewModel.addToPayeeList.value != null &&
            destinationAccountViewModel.addToPayeeList.value!!
        ) {
            transfer.newPayee = generateNewPayee(transfer)
        }
        return transfer
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
        summary.transferType = TransferType.INTRABANK
        if (destinationCustomerName != null) {
            destinationAccountViewModel.setBeneficiary(destinationCustomerName)
        }
        val beneficiary = Beneficiary(destinationAccountViewModel.beneficiaryName.value!!)
        summary.to =
            Payee(
                beneficiary,
                destinationAccountViewModel.destinationAccount.value!!
            )

        return summary
    }

    override fun generateNewPayee(transfer: Transfer): TemplateRequest? {
        val payee = super.generateNewPayee(transfer)
        payee?.accountTypeId =
            TemplatesDataSource.TRANSFER_TYPE_TO_TEMPLATE_TYPE_ID[TransferType.INTRABANK.toString()
                .lowercase()]
        return payee
    }

    override fun generateNewPayee(summary: TransferSummary): TemplateRequest {
        val payee = super.generateNewPayee(summary)
        payee.accountTypeId =
            TemplatesDataSource.TRANSFER_TYPE_TO_TEMPLATE_TYPE_ID[TransferType.INTRABANK.toString()
                .lowercase()]
        return payee
    }
}