package tl.bnctl.banking.ui.banking.fragments.services.insurance.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.insurance.model.Insurance
import tl.bnctl.banking.data.transfers.model.TransferConfirmRequest
import tl.bnctl.banking.databinding.FragmentInsuranceConfirmBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.services.insurance.InsuranceViewModel
import tl.bnctl.banking.ui.banking.fragments.services.insurance.InsuranceViewModelFactory
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source.SelectAccountViewModelFactory
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source.SelectSourceAccountViewModel
import tl.bnctl.banking.ui.utils.DialogFactory

class InsuranceConfirmFragment : BaseFragment() {

    companion object {
        private const val RESULT_KEY_LIST_ACCOUNTS: String = "sourceAccountForPayingInsurance"
    }

    private var _binding: FragmentInsuranceConfirmBinding? = null
    private val binding get() = _binding!!

    private val insuranceViewModel: InsuranceViewModel by navGraphViewModels(R.id.nav_fragment_insurance) { InsuranceViewModelFactory() }
    private val selectSourceAccountViewModel: SelectSourceAccountViewModel by navGraphViewModels(R.id.nav_fragment_insurance) { SelectAccountViewModelFactory() }

    private var isLoading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsuranceConfirmBinding.inflate(inflater, container, false)
        isLoading = true
        selectSourceAccountViewModel.accountFetch(this.javaClass.toString())
        val selectedInsurance: Insurance = insuranceViewModel.selectedInsurance.value!!
        val currency = "BGN"
        if (insuranceViewModel.selectedPaymentCurrency.value == null) {
            insuranceViewModel.chooseCurrencyPayment(currency, selectedInsurance.amountBgn)
        }
        if (selectedInsurance.iban == null) {
            binding.amountWithDifferentCurrencies.visibility = View.GONE
            binding.amount.visibility = View.VISIBLE
            binding.amountForDefaultCurrency.text = selectedInsurance.amountBgn
            binding.defaultPolicyCurrency.text = currency
        } else {
            binding.amountWithDifferentCurrencies.visibility = View.VISIBLE
            binding.amount.visibility = View.GONE

            binding.textSecondCurrency.text = selectedInsurance.currency

            binding.textMainAmount.text = selectedInsurance.amountBgn
            binding.textMainCurrency.text = currency

            binding.textSecondAmount.text = selectedInsurance.amount
            binding.textSecondCurrency.text = selectedInsurance.currency

            binding.radioButtonBgnCurrencyChoice.setOnClickListener {
                insuranceViewModel.chooseCurrencyPayment(currency, selectedInsurance.amountBgn)
                binding.radioButtonSecondCurrencyChoice.isChecked = false
            }

            binding.radioButtonSecondCurrencyChoice.setOnClickListener {
                insuranceViewModel.chooseCurrencyPayment(
                    selectedInsurance.currency,
                    selectedInsurance.amount
                )
                binding.radioButtonBgnCurrencyChoice.isChecked = false
            }

            if ((insuranceViewModel.selectedPaymentCurrency.value != null &&
                        insuranceViewModel.selectedPaymentCurrency.value!!.currency == currency) ||
                insuranceViewModel.selectedPaymentCurrency.value == null
            ) {
                binding.radioGroup.check(R.id.radio_button_bgn_currency_choice)
            }
        }

        selectSourceAccountViewModel.accountsWithPermission.observe(viewLifecycleOwner) {
            isLoading = false
            setupLoadingIndicator()
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                handleResultError(error, R.string.error_loading_accounts)
                return@observe
            }
            if ((it as Result.Success).data.isEmpty()) {
                createCancellableDialog()
            }
            insuranceViewModel.setSelectedSourceAccount(it)
        }
        insuranceViewModel.selectedSourceAccount.observe(viewLifecycleOwner) {
            if (it != null) {
                val account = insuranceViewModel.selectedSourceAccount.value!!
                binding.insuranceSelectAccountItemName.text = account.accountTypeDescription
                binding.insuranceSelectAccountItemNumber.text = account.iban
                binding.insuranceSelectAccountItemBalance.text = getString(
                    R.string.transfer_select_account_balance_label,
                    account.balance.available.toString(),
                    account.currencyName
                )
                if (selectSourceAccountViewModel.accountsWithPermission.value is Result.Success &&
                    (selectSourceAccountViewModel.accountsWithPermission.value as Result.Success<List<Account>>).data.size > 1
                ) {
                    binding.sourceAccountSelectButtonEdit.setOnClickListener {
                        requireView().findNavController()
                            .navigate(
                                InsuranceConfirmFragmentDirections.actionGlobalNavFragmentSelectAccountList(
                                    (selectSourceAccountViewModel.accountsWithPermission.value as Result.Success<List<Account>>).data.toTypedArray(),
                                    RESULT_KEY_LIST_ACCOUNTS,
                                    R.string.insurance_payment_label_select_source_account
                                )
                            )
                    }
                }
            }
        }
        parentFragmentManager.setFragmentResultListener(
            RESULT_KEY_LIST_ACCOUNTS,
            viewLifecycleOwner,
            FragmentResultListener { _, result ->
                result.getParcelable<Account>("selectedAccount")?.let {
                    insuranceViewModel.selectSourceAccount(it)
                }
            })

        insuranceViewModel.transferCreateResult.observe(viewLifecycleOwner) {
            isLoading = false
            setupLoadingIndicator()
            if (it.success != null) {
                requireView().findNavController()
                    .navigate(
                        InsuranceConfirmFragmentDirections.actionNavFragmentInsuranceConfirmToNavFragmentTransferConfirm(
                            TransferConfirmRequest(
                                it.success.validationRequestId,
                                it.success.objectId
                            ),
                            insuranceViewModel.selectedSourceAccount.value!!.accountId
                        )
                    )
            }
        }

        binding.insuranceConfirmButton.setOnClickListener {
            isLoading = true
            setupLoadingIndicator()
            if (BuildConfig.MAKER_CHECKER_FLOW) {
                insuranceViewModel.startTransferCreation()
            } else {
                requireView().findNavController()
                    .navigate(
                        InsuranceConfirmFragmentDirections.actionNavFragmentInsuranceConfirmToNavFragmentTransferConfirm(
                            null,
                            insuranceViewModel.selectedSourceAccount.value!!.accountId,
                            insuranceViewModel.generateTransferSummary()
                        )
                    )
            }

        }

        binding.toolbar.setOnClickListener {
            insuranceViewModel.clearCurrencyPayment()
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun createCancellableDialog() {
        DialogFactory.createCancellableDialog(
            requireActivity(),
            R.string.error_loading_accounts
        ).show()
    }

    private fun setupLoadingIndicator() {
        if (!isLoading) {
            binding.loadingIndicator.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.VISIBLE
        }
    }
}