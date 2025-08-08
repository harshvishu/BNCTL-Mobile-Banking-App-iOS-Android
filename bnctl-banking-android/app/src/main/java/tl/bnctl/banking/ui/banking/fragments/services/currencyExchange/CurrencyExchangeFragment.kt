package tl.bnctl.banking.ui.banking.fragments.services.currencyExchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.currencyExchange.enums.CurrencyExchangeOperationType
import tl.bnctl.banking.data.payee.model.Payee
import tl.bnctl.banking.data.templates.model.Bank
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.templates.model.TemplateRequest
import tl.bnctl.banking.data.transfers.enums.TransferExecution
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.data.transfers.model.Transfer
import tl.bnctl.banking.data.transfers.model.TransferConfirmRequest
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.data.transfers.model.TransferSummary
import tl.bnctl.banking.ui.banking.fragments.services.currencyExchange.create.sections.details.CurrencyExchangeDetailsFragment
import tl.bnctl.banking.ui.banking.fragments.services.currencyExchange.create.sections.details.CurrencyExchangeDetailsViewModel
import tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.betweenacc.TransferBetweenOwnAccountsFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.summary.TransferSummaryViewModel
import tl.bnctl.banking.ui.banking.fragments.transfers.summary.TransferSummaryViewModelFactory
import tl.bnctl.banking.ui.utils.DialogFactory

class CurrencyExchangeFragment : TransferBetweenOwnAccountsFragment() {

    private val currencyExchangeDetailsViewModel: CurrencyExchangeDetailsViewModel by viewModels()

    // User the transfer creation from TransferSummaryViewModel so you won't have to navigate to the fragment
    private val transferSummaryViewModel: TransferSummaryViewModel
            by viewModels { TransferSummaryViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        setUpTransferCreationFlow()
        binding.transferTypeTitle.text =
            resources.getString(R.string.transfer_type_currency_exchange)
        childFragmentManager.beginTransaction()
            .replace(R.id.transfer_base_details, CurrencyExchangeDetailsFragment())
            .commitNow()
        setUpCurrencySelection()
        return view
    }

    override fun populateViewFromTemplate(template: Template) {
        super.populateViewFromTemplate(template)
        //currencyExchangeDetailsViewModel.setAmount(template.amount)
        if (template.additionalDetails != null) {
            currencyExchangeDetailsViewModel.setPreferentialRatesPin(
                template.additionalDetails!!.get(
                    "preferentialRatesPin"
                ).toString()
            )
            when (template.additionalDetails!!.get("type").toString()) {
                "buy" -> currencyExchangeDetailsViewModel.changeType(CurrencyExchangeOperationType.BUY)
                "sell" -> currencyExchangeDetailsViewModel.changeType(CurrencyExchangeOperationType.SELL)
            }
        }
    }

    private fun setUpCurrencySelection() {
        sourceAccountViewModel.selectedAccount.observe(viewLifecycleOwner) {
            if (currencyExchangeDetailsViewModel.type.value == CurrencyExchangeOperationType.SELL && it != null) {
                currencyExchangeDetailsViewModel.selectCurrency(it.currencyName)
            } else {
                destinationAccountViewModel.selectedDestinationAccount.value?.currencyName?.let { currency ->
                    currencyExchangeDetailsViewModel.selectCurrency(
                        currency
                    )
                }
            }
        }
        destinationAccountViewModel.selectedDestinationAccount.observe(viewLifecycleOwner) {
            if (currencyExchangeDetailsViewModel.type.value == CurrencyExchangeOperationType.SELL) {
                sourceAccountViewModel.selectedAccount.value?.currencyName?.let { currency ->
                    currencyExchangeDetailsViewModel.selectCurrency(
                        currency
                    )
                }
            } else {
                currencyExchangeDetailsViewModel.selectCurrency(it!!.currencyName)
            }
        }
        currencyExchangeDetailsViewModel.type.observe(viewLifecycleOwner) {
            if (it == CurrencyExchangeOperationType.SELL) {
                sourceAccountViewModel.selectedAccount.value?.currencyName?.let { sourceAccountCurrency ->
                    currencyExchangeDetailsViewModel.selectCurrency(
                        sourceAccountCurrency
                    )
                }
            } else {
                destinationAccountViewModel.selectedDestinationAccount.value?.currencyName?.let { destinationAccountCurrency ->
                    currencyExchangeDetailsViewModel.selectCurrency(
                        destinationAccountCurrency
                    )
                }
            }
        }
    }

    private fun setUpTransferCreationFlow() {
        transferSummaryViewModel.creationResult.observe(viewLifecycleOwner) {
            if (it.success != null) {
                requireView().findNavController()
                    .navigate(
                        CurrencyExchangeFragmentDirections.actionNavFragmentCurrencyExchangeToNavFragmentTransferConfirm(
                            TransferConfirmRequest(
                                it.success.validationRequestId,
                                it.success.objectId
                            )
                        )
                    )
            }
            if (it.error != null) {
                val errorString = it.error.getErrorString()
                val stringId =
                    resources.getIdentifier(errorString, "string", requireActivity().packageName)
                val errorStringId =
                    if (stringId == 0) R.string.error_transfer_creation else stringId
                requireView().findNavController()
                    .navigate(
                        CurrencyExchangeFragmentDirections.actionNavFragmentCurrencyExchangeToNavFragmentTransferEnd(
                            TransferEnd(
                                false,
                                resources.getString(errorStringId)
                            )
                        )
                    )
            }
        }
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

        var isSameCurrency = true
        // Check if the accounts are the same currency only if accounts are actually selected
        if (isSourceAccountSelected && isDestinationAccountSelected) {
            isSameCurrency =
                sourceAccountViewModel.selectedAccount.value?.currencyName == destinationAccountViewModel.selectedDestinationAccount.value?.currencyName
            if (isSameCurrency) {
                DialogFactory.createCancellableDialog(
                    requireActivity(),
                    R.string.error_currency_exchange_validation_same_currency_account
                ).show()
            }
        }
        currencyExchangeDetailsViewModel.validateAmount()
        currencyExchangeDetailsViewModel.validatePreferentialRatesPin()
        return sourceAccountViewModel.isSelectedAccountValid()
                && currencyExchangeDetailsViewModel.amountError.value == null
                && currencyExchangeDetailsViewModel.preferentialRatesPinError.value == null
                && currencyExchangeDetailsViewModel.isCurrencyValid()
                && !isSameCurrency
    }

    override fun generateSummary(
        feeChange: String,
        feeCurrency: String,
        destinationCustomerName: String?
    ): TransferSummary {
        return TransferSummary(
            currencyExchangeDetailsViewModel.amount.value!!,
            destinationAccountViewModel.selectedDestinationAccount.value!!.currencyName,
            feeChange.toDouble(),
            feeCurrency,
            sourceAccountViewModel.selectedAccount.value!!,
            Payee(
                destinationAccountViewModel.selectedDestinationAccount.value!!.beneficiary,
                destinationAccountViewModel.selectedDestinationAccount.value!!.iban
            ),
            "",
            TransferType.BETWEENACC,
            TransferExecution.NOW,
            generateAdditionalDetails()
        )
    }

    override fun onValidationFailure(transferEnd: TransferEnd) {
        requireView().findNavController().navigate(
            CurrencyExchangeFragmentDirections.actionNavFragmentCurrencyExchangeToNavFragmentTransferEnd(
                transferEnd
            )
        )
    }

    override fun onValidationSuccess(summary: TransferSummary) {
        if (BuildConfig.MAKER_CHECKER_FLOW) {
            populateSummaryViewModel(summary)
            transferSummaryViewModel.startTransferCreation()
        } else {
            requireView().findNavController()
                .navigate(
                    CurrencyExchangeFragmentDirections.actionNavFragmentCurrencyExchangeToNavFragmentTransferConfirm(
                        null,
                        summary.from.accountId,
                        summary
                    )
                )
        }
    }

    override fun onPendingTransferCreateResult(transferEnd: TransferEnd, subMessage: String) {
        // Not needed here
    }

    override fun generateTransfer(): Transfer {
        return Transfer(
            currencyExchangeDetailsViewModel.amount.value!!.toString(),
            "",
            destinationAccountViewModel.selectedDestinationAccount.value!!.currencyName,
            sourceAccountViewModel.selectedAccount.value!!.iban,
            sourceAccountViewModel.selectedAccount.value!!.accountId,
            sourceAccountViewModel.selectedAccount.value!!.currencyName,
            destinationAccountViewModel.selectedDestinationAccount.value!!.iban,
            destinationAccountViewModel.selectedDestinationAccount.value!!.beneficiary.name,
            TransferType.BETWEENACC.type,
            TransferExecution.NOW.time,
            generateAdditionalDetails(),
            null
        )
    }

    override fun generateNewPayee(transfer: Transfer): TemplateRequest? {
        return null
    }

    override fun onBanksLoaded(banksResult: Result<List<Bank>>) {
        // Not needed here
    }

    private fun populateSummaryViewModel(summary: TransferSummary) {
        transferSummaryViewModel.setAmount(summary.amount)
        transferSummaryViewModel.setCurrency(summary.currency)
        transferSummaryViewModel.setFeeAmount(summary.feeAmount)
        transferSummaryViewModel.setFeeCurrency(summary.feeCurrency)
        transferSummaryViewModel.setFrom(summary.from)
        transferSummaryViewModel.setTo(summary.to)
        transferSummaryViewModel.setReason(summary.reason)
        transferSummaryViewModel.setTransferType(summary.transferType)
        transferSummaryViewModel.setExecutionType(summary.executionType)
        transferSummaryViewModel.setAdditionalDetails(summary.additionalDetails)
    }

    private fun generateAdditionalDetails(): Map<String, Any> {
        val type = currencyExchangeDetailsViewModel.type.value!!.type
        val preferentialRatePin: String? =
            currencyExchangeDetailsViewModel.preferentialRatePin.value
        val additionalDetails: Map<String, Any> = if (preferentialRatePin.isNullOrBlank()) {
            mapOf(
                "orderType" to type,
                "rateType" to "standard",
                "transactionType" to "currency.exchange"
            )
        } else {
            mapOf(
                "orderType" to type,
                "rateType" to "custom",
                "offerId" to preferentialRatePin,
                "transactionType" to "currency.exchange"
            )
        }
        return additionalDetails
    }
}