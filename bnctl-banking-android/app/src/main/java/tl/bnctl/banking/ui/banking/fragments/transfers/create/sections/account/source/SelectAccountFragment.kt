package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.databinding.FragmentSectionAccountSelectBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.list.SelectAccountListFragmentDirections
import tl.bnctl.banking.ui.utils.NumberUtils

class SelectAccountFragment : BaseFragment() {

    private var _binding: FragmentSectionAccountSelectBinding? = null
    private val binding get() = _binding!!

    private val selectSourceAccountViewModel: SelectSourceAccountViewModel by viewModels({ requireParentFragment() })

    companion object {
        val allowedSourceAccountProductTypes = arrayOf(
            "operational",
            "savings"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSectionAccountSelectBinding.inflate(inflater, container, false)
        populateView()
        binding.sourceAccountSelectButtonEdit.setOnClickListener {
            startSourceAccountSelection()
        }
        binding.sourceAccountSelectAccountMessage.setOnClickListener {
            startSourceAccountSelection()
        }
        // Use fragment result api to listen for the result form SelectAccountListFragment
        // Note: the listener need to be set on the same fragment manager as the SelectAccountListFragment's
        requireParentFragment().parentFragmentManager.setFragmentResultListener(
            SelectAccountFragment::class.simpleName.toString(),
            viewLifecycleOwner
        ) { _, result ->
            result.getParcelable<Account>("selectedAccount")?.let {
                selectSourceAccountViewModel.selectAccount(it)
            }
        }

        selectSourceAccountViewModel.selectAccountError.observe(viewLifecycleOwner) {
            val focusable: Boolean
            if (it != null) {
                focusable = true
                binding.sourceAccountSelectAccountLabel.error = resources.getString(it)
            } else {
                focusable = false
                binding.sourceAccountSelectAccountLabel.error = null
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.sourceAccountSelectAccountLabel.focusable =
                    if (focusable) View.FOCUSABLE else View.NOT_FOCUSABLE
            }
            binding.sourceAccountSelectAccountLabel.isFocusableInTouchMode = focusable
        }

        if (BuildConfig.SHOW_LOADER_FOR_ACCOUNTS) {
            selectSourceAccountViewModel.loadingAccounts.observe(viewLifecycleOwner) {
                binding.progressBar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        } else {
            binding.progressBar.visibility = View.GONE
        }
        return binding.root
    }

    private fun startSourceAccountSelection() {
        val accountsResult = selectSourceAccountViewModel.accountsWithPermission.value
        if (accountsResult != null && accountsResult is Result.Success) {
            val accounts =
                accountsResult.data.filter { account ->
                    allowedSourceAccountProductTypes.contains(account.product.type) && selectSourceAccountViewModel.filterRequirement(
                        account
                    )
                }
            requireView().findNavController()
                .navigate(
                    SelectAccountListFragmentDirections.actionGlobalNavFragmentSelectAccountList(
                        accounts.toTypedArray(),
                        SelectAccountFragment::class.simpleName.toString(),
                        R.string.transfer_form_label_from
                    )
                )
        }
    }

    private fun populateView() {
        selectSourceAccountViewModel.selectedAccount.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.sourceAccountSelectAccountMessage.visibility = View.VISIBLE
                binding.sourceAccountDataWrapper.visibility = View.GONE
            } else {
                binding.sourceAccountSelectAccountMessage.visibility = View.GONE
                binding.sourceAccountDataWrapper.visibility = View.VISIBLE

                binding.sourceAccountSelectName.text = it.accountTypeDescription
                binding.sourceAccountSelectNumber.text = it.accountNumber
                binding.sourceAccountSelectBalance.text = requireContext().resources
                    .getString(R.string.transfer_select_account_balance_label).format(
                        NumberUtils.formatAmount(it.balance.available), it.currencyName)
            }
        }
        selectSourceAccountViewModel.accountsWithPermission.observe(viewLifecycleOwner) {
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                handleResultError(error, R.string.error_loading_accounts)
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
                    var newlySelectedAccount = if (accounts.size == 1) accounts[0] else null
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