package tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.betweenacc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
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
import tl.bnctl.banking.ui.banking.fragments.transfers.BaseTransferFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.destination.myaccount.SelectDestinationAccountFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.destination.myaccount.SelectDestinationAccountViewModel
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.destination.myaccount.SelectDestinationAccountViewModelFactory
import tl.bnctl.banking.ui.utils.DialogFactory

open class TransferBetweenOwnAccountsFragment : BaseTransferFragment() {

    protected val destinationAccountViewModel: SelectDestinationAccountViewModel by viewModels() { SelectDestinationAccountViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding.transferTypeTitle.text = resources.getString(R.string.transfer_type_betweenacc)
        detailsViewModel.setAvailableCurrencies(R.array.between_account_currencies)
        destinationAccountViewModel.accountFetch(this.javaClass.toString())
        detailsViewModel.disableTypeOption()
        detailsViewModel.disableCurrenciesOption()
        childFragmentManager.beginTransaction()
            .replace(R.id.transfer_base_destination_account, SelectDestinationAccountFragment())
            .commitNow()
        sourceAccountViewModel.selectedAccount.observe(viewLifecycleOwner) {
            if (it != null) {
                detailsViewModel.selectCurrency(it.currencyName)
            }
        }
        return view
    }

    override fun populateViewFromTemplate(template: Template) {
        super.populateViewFromTemplate(template)
        destinationAccountViewModel.preselectDestinationAccount(template.accountNumber)
    }

    override fun onValidationFailure(transferEnd: TransferEnd) {
        requireView().findNavController()
            .navigate(
                TransferBetweenOwnAccountsFragmentDirections.actionNavFragmentTransferBetweenOwnAccountsToNavFragmentTransferEnd(
                    transferEnd
                )
            )
    }

    override fun onValidationSuccess(summary: TransferSummary) {
        requireView().findNavController()
            .navigate(
                TransferBetweenOwnAccountsFragmentDirections.actionNavFragmentTransferBetweenOwnAccountsToNavFragmentTransferSummary(
                    summary
                )
            )
    }

    override fun onPendingTransferCreateResult(transferEnd: TransferEnd, subMessage: String) {
        requireView().findNavController()
            .navigate(
                TransferBetweenOwnAccountsFragmentDirections.actionNavFragmentTransferBetweenOwnAccountsToNavFragmentTransferEnd(
                    transferEnd,
                    null,
                    transferEnd.transferSuccess,
                    subMessage
                )
            )
    }

    override fun isInputValid(): Boolean {
        val isSourceAccountSelected = sourceAccountViewModel.selectedAccount.value != null
        val isDestinationAccountSelected =
            destinationAccountViewModel.selectedDestinationAccount.value != null

        if (!isSourceAccountSelected) {
            sourceAccountViewModel.raiseSelectAccountError(R.string.transfer_select_account_select_select_account)
        } else {
            sourceAccountViewModel.clearSelectAccountError()
        }
        if (!isDestinationAccountSelected) {
            destinationAccountViewModel.raiseSelectAccountError(R.string.transfer_select_account_select_select_account)
        } else {
            destinationAccountViewModel.clearSelectAccountError()
        }
        var isSameCurrency = false
        // Check if the accounts are the same currency only if accounts are actually selected
        if (isSourceAccountSelected && isDestinationAccountSelected) {
            isSameCurrency =
                sourceAccountViewModel.selectedAccount.value!!.currencyName == destinationAccountViewModel.selectedDestinationAccount.value!!.currencyName
            if (!isSameCurrency) {
                DialogFactory.createCancellableDialog(
                    requireActivity(),
                    R.string.error_transfer_validation_betweenacc_same_currency_account
                ).show()
            }
        }

        return ((super.isInputValid()
                && destinationAccountViewModel.isSelectedAccountValid())
                && isSameCurrency)
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
        val destinationAccount = destinationAccountViewModel.selectedDestinationAccount.value!!

        summary.to = Payee(
            Beneficiary(destinationAccount.accountTypeDescription),
            destinationAccount.accountNumber,
            destinationAccount.balance.available,
            destinationAccount.currencyName
        )
        return summary
    }

    override fun generateTransfer(): Transfer {
        return Transfer(
            detailsViewModel.amount.value!!.toString(),
            detailsViewModel.reason.value!!.toString(),
            detailsViewModel.selectedCurrency.value!!.toString(),
            sourceAccountViewModel.selectedAccount.value!!.accountNumber,
            sourceAccountViewModel.selectedAccount.value!!.accountId,
            sourceAccountViewModel.selectedAccount.value!!.currencyName,
            destinationAccountViewModel.selectedDestinationAccount.value!!.accountNumber,
            destinationAccountViewModel.selectedDestinationAccount.value!!.beneficiary.name,
            TransferType.BETWEENACC.type,
            TransferExecution.NOW.time,
            mapOf(),
            null
        )
    }

    override fun generateNewPayee(transfer: Transfer): TemplateRequest? {
        return null
    }

    override fun onBanksLoaded(banksResult: Result<List<Bank>>) {
    }
}