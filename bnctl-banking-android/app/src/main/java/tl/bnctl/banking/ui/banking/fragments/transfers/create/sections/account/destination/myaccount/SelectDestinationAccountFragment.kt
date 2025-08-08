package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.destination.myaccount

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.databinding.FragmentSectionDestinationAccountSelectBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.list.SelectAccountListFragmentDirections
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source.SelectSourceAccountViewModel
import tl.bnctl.banking.ui.utils.NumberUtils

class SelectDestinationAccountFragment : BaseFragment() {

    private var _binding: FragmentSectionDestinationAccountSelectBinding? = null
    private val binding get() = _binding!!

    private val selectSourceAccountViewModel: SelectSourceAccountViewModel by viewModels({ requireParentFragment() })
    private val selectDestinationAccountViewModel: SelectDestinationAccountViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            FragmentSectionDestinationAccountSelectBinding.inflate(inflater, container, false)
        populateView()
        // Send the available accounts to SelectAccountListFragment.
        binding.destinationAccountSelectButtonEdit.setOnClickListener {
            startAccountSelection()
        }
        binding.destinationAccountSelectAccountMessage.setOnClickListener {
            startAccountSelection()
        }

        // Use fragment result api to listen for the result form SelectAccountListFragment
        // Note: the listener need to be set on the same fragment manager as the SelectAccountListFragment's
        requireParentFragment().parentFragmentManager.setFragmentResultListener(
            SelectDestinationAccountFragment::class.simpleName.toString(),
            viewLifecycleOwner, FragmentResultListener { _, result ->
                result.getParcelable<Account>("selectedAccount")?.let {
                    selectDestinationAccountViewModel.selectDestinationAccount(it)
                }
            })


        selectDestinationAccountViewModel.selectAccountError.observe(viewLifecycleOwner) {
            val focusable: Boolean
            if (it != null) {
                focusable = true
                binding.destinationAccountSelectAccountLabel.error = resources.getString(it)
            } else {
                focusable = false
                binding.destinationAccountSelectAccountLabel.error = null
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.destinationAccountSelectAccountLabel.focusable =
                    if (focusable) View.FOCUSABLE else View.NOT_FOCUSABLE
            }
            binding.destinationAccountSelectAccountLabel.isFocusableInTouchMode = focusable
        }
        return binding.root
    }

    private fun startAccountSelection() {
        val selectedAccount = selectSourceAccountViewModel.selectedAccount.value
        val accountResult = selectDestinationAccountViewModel.accountsFullList.value
        if (accountResult != null && accountResult is Result.Success) {
            val accounts =
                accountResult.data.filter { account ->
                    account.accountId != selectedAccount?.accountId
                }
            requireView().findNavController()
                .navigate(
                    SelectAccountListFragmentDirections.actionGlobalNavFragmentSelectAccountList(
                        accounts.toTypedArray(),
                        SelectDestinationAccountFragment::class.simpleName.toString(),
                        R.string.transfer_form_label_to
                    )
                )
        }
    }

    // Populate the destination account dropdown after the source account changes so the same account won't be selected
    private fun populateView() {
        selectDestinationAccountViewModel.selectedDestinationAccount.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.destinationAccountSelectAccountMessage.visibility = View.VISIBLE
                binding.destinationAccountDataWrapper.visibility = View.GONE
            } else {
                binding.destinationAccountSelectAccountMessage.visibility = View.GONE
                binding.destinationAccountDataWrapper.visibility = View.VISIBLE

                binding.destinationAccountSelectName.text = it.accountTypeDescription
                binding.destinationAccountSelectNumber.text = it.accountNumber
                binding.destinationAccountSelectBalance.text = requireContext().resources
                    .getString(R.string.transfer_select_account_balance_label).format(
                        NumberUtils.formatAmount(it.balance.available), it.currencyName)
            }
        }
        selectSourceAccountViewModel.accountsFullList.observe(viewLifecycleOwner) {
            if (it != null && it is Result.Success) {
                preselectDestinationAccount(it, selectSourceAccountViewModel.selectedAccount.value)
            }
        }
        selectSourceAccountViewModel.selectedAccount.observe(viewLifecycleOwner) {
            val accountResult = selectSourceAccountViewModel.accountsFullList.value
            preselectDestinationAccount(accountResult, it)
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
    }

    private fun preselectDestinationAccount(
        accountResult: Result<List<Account>>?,
        sourceAccount: Account?
    ) {
        if (accountResult != null && accountResult is Result.Success) {
            val accounts =
                accountResult.data.filter { account ->
                    account.accountId != sourceAccount?.accountId
                }
            val selectedDestinationAccount =
                selectDestinationAccountViewModel.selectedDestinationAccount.value
            if ((selectedDestinationAccount == null
                        || selectedDestinationAccount.accountId == sourceAccount?.accountId)
                && accounts.isNotEmpty()
            ) {
                val preselectAccountNumber =
                    selectDestinationAccountViewModel.preselectedDestinationAccount.value
                var newlySelectedAccount: Account? = null
                if (!preselectAccountNumber.isNullOrBlank()) {
                    val preselectedAccount =
                        accounts.find { account -> account.iban == preselectAccountNumber }
                    preselectedAccount?.let { acc -> newlySelectedAccount = acc }
                    selectDestinationAccountViewModel.clearPreselectedDestinationAccount()
                }
                selectDestinationAccountViewModel.selectDestinationAccount(newlySelectedAccount)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}