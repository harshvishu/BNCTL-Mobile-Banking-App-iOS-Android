package tl.bnctl.banking.ui.banking.fragments.services.utilityBills.summary

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.billpayments.model.BillPaymentApproveNumerousRequest
import tl.bnctl.banking.data.billpayments.model.BillPaymentValidationRequest
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.databinding.FragmentServiceUtilityBillSummaryBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source.SelectAccountViewModelFactory
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source.SelectSourceAccountViewModel
import tl.bnctl.banking.ui.utils.DateUtils
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.ui.utils.NumberUtils
import tl.bnctl.banking.util.Constants
import java.util.*

class UtilityBillSummaryFragment : BaseFragment() {

    private var _binding: FragmentServiceUtilityBillSummaryBinding? = null

    private val binding get() = _binding!!

    private val utilityBillSummaryFragmentArgs: UtilityBillSummaryFragmentArgs by navArgs()

    private val selectSourceAccountViewModel: SelectSourceAccountViewModel by viewModels { SelectAccountViewModelFactory() }
    private val utilityBillSummaryViewModel: UtilityBillSummaryViewModel by viewModels { UtilityBillSummaryViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceUtilityBillSummaryBinding.inflate(inflater, container, false)
        selectSourceAccountViewModel.accountFetch(this.javaClass.toString())
        populateView()
        utilityBillSummaryViewModel.setSelectedBills(utilityBillSummaryFragmentArgs.selectedUtilityBills.toList())
        changeSourceAccount()
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        utilityBillSummaryViewModel.validationResult.observe(viewLifecycleOwner) {
            if (it.success != null) {
                if (it.success.canCreateTransfer) {
                    requireView().findNavController().navigate(
                        UtilityBillSummaryFragmentDirections.actionNavFragmentUtilityBillsSummaryToNavFragmentUtilityBillsConfirm(
                            BillPaymentApproveNumerousRequest(
                                utilityBillSummaryViewModel.selectedBills.value!!,
                                selectSourceAccountViewModel.selectedAccount.value!!.iban,
                                selectSourceAccountViewModel.selectedAccount.value!!.accountId,
                                selectSourceAccountViewModel.selectedAccount.value!!.beneficiary.name
                            )
                        )
                    )
                } else {
                    requireView().findNavController()
                        .navigate(
                            UtilityBillSummaryFragmentDirections.actionNavFragmentUtilityBillsSummaryToNavFragmentTransferEnd(
                                TransferEnd(
                                    false,
                                    getString(R.string.error_generic)
                                )
                            )
                        )
                }
            }
            if (it.error != null) run {
                val errorString = it.error.getErrorString()
                val stringId =
                    resources.getIdentifier(errorString, "string", requireActivity().packageName)
                val errorStringId =
                    if (stringId == 0) R.string.error_transfer_validation else stringId
                handleResultError(it.error, R.string.error_transfer_validation, false)
                requireView().findNavController()
                    .navigate(
                        UtilityBillSummaryFragmentDirections.actionNavFragmentUtilityBillsSummaryToNavFragmentTransferEnd(
                            TransferEnd(
                                false,
                                getString(errorStringId)
                            )
                        )
                    )
            }
        }
        binding.buttonUtilityBillsConfirm.setOnClickListener {
            utilityBillSummaryViewModel.startBillPaymentValidation(
                BillPaymentValidationRequest(
                    selectSourceAccountViewModel.selectedAccount.value!!.accountId,
                    selectSourceAccountViewModel.selectedAccount.value!!.iban,
                    utilityBillSummaryFragmentArgs.totalAmount,
                    utilityBillSummaryFragmentArgs.totalAmountCurrency,
                    DateUtils.formatDate(requireContext(), Date()),
                    "now"
                )
            )
        }
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            DialogFactory.createConfirmDialog(
                requireContext(),
                R.string.common_dialog_message_cancel_transaction
            ) {
                findNavController().currentDestination?.parent?.let { destination ->
                    findNavController().popBackStack(
                        destination.startDestinationId, false
                    )
                }
            }.show()
            true
        }
        return binding.root
    }

    private fun changeSourceAccount() {
        val filterCurrency =
            resources.getStringArray(R.array.utility_bills_currencies)[0].toString()
        selectSourceAccountViewModel.filterRequirement =
            { account: Account -> account.currencyName == filterCurrency }
        binding.buttonEditSourceAccount.setOnClickListener {
            val accountsResult = selectSourceAccountViewModel.accountsWithPermission.value
            if (accountsResult != null && accountsResult is Result.Success) {
                val accounts =
                    accountsResult.data.filter { account ->
                        account.product.type == "operational" && selectSourceAccountViewModel.filterRequirement(
                            account
                        )
                    }
                requireView().findNavController()
                    .navigate(
                        UtilityBillSummaryFragmentDirections.actionGlobalNavFragmentSelectAccountList(
                            accounts.toTypedArray(),
                            UtilityBillSummaryFragment::class.simpleName.toString(),
                            R.string.transfer_form_label_from
                        )
                    )
            }
        }
        // Use fragment result api to listen for the result form SelectAccountListFragment
        // Note: the listener need to be set on the same fragment manager as the SelectAccountListFragment's
        parentFragmentManager.setFragmentResultListener(
            UtilityBillSummaryFragment::class.simpleName.toString(),
            viewLifecycleOwner
        ) { _, result ->
            result.getParcelable<Account>("selectedAccount")?.let {
                selectSourceAccountViewModel.selectAccount(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateView() {
        binding.utilityBillsAmount.text =
            NumberUtils.formatAmount(utilityBillSummaryFragmentArgs.totalAmount)
        binding.utilityBillsCurrency.text = utilityBillSummaryFragmentArgs.totalAmountCurrency
        selectSourceAccountViewModel.selectedAccount.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.sourceAccountBalance.text =
                    "${
                        NumberUtils.formatAmount(it.balance.available)
                    } ${it.currencyName}"
                binding.sourceAccountName.text = it.accountName
                binding.sourceAccountNumber.text = it.iban
            }
        }
        selectSourceAccountViewModel.accountsWithPermission.observe(viewLifecycleOwner) {
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                if (error.code.equals(Constants.SESSION_EXPIRED_CODE)) {
                    DialogFactory.createSessionExpiredDialog(requireContext()).show()
                } else {
                    DialogFactory.createCancellableDialog(
                        requireActivity(),
                        R.string.error_loading_accounts
                    ).show()
                }
            } else {
                val accounts =
                    (it as Result.Success<List<Account>>).data.filter { account ->
                        account.product.type == "operational" && selectSourceAccountViewModel.filterRequirement(
                            account
                        )
                    }
                val selectedAccount = selectSourceAccountViewModel.selectedAccount.value
                if (selectedAccount == null && accounts.isNotEmpty()) {
                    val preselectAccountNumber = selectSourceAccountViewModel.preselectAccount.value
                    var newlySelectedAccount = accounts[0]
                    if (!preselectAccountNumber.isNullOrBlank()) {
                        val preselectedAccount =
                            accounts.find { account -> account.iban == preselectAccountNumber }
                        preselectedAccount?.let { acc -> newlySelectedAccount = acc }
                        selectSourceAccountViewModel.clearPreselectedAccount()
                    }
                    selectSourceAccountViewModel.selectAccount(newlySelectedAccount)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}