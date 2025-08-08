package tl.bnctl.banking.ui.banking.fragments.accounts.details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.databinding.FragmentAccountDetailsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.accounts.details.dialogs.AccountBalancesDialogFragment
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsViewModel
import tl.bnctl.banking.ui.banking.fragments.statements.ViewStatementProperty
import tl.bnctl.banking.ui.utils.DateUtils
import tl.bnctl.banking.ui.utils.NumberUtils

class AccountDetailsFragment : BaseFragment() {

    private var _binding: FragmentAccountDetailsBinding? = null
    private val binding get() = _binding!!

    private val accountDetailsViewModel: AccountDetailsViewModel
        by navGraphViewModels(R.id.nav_fragment_account_details) { AccountDetailsViewModelFactory() }
    private val statementsViewModel: StatementsViewModel
        by navGraphViewModels(R.id.nav_fragment_account_details) { defaultViewModelProviderFactory }

    private var clipboard: ClipboardManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAccountStatement()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDetailsBinding.inflate(inflater, container, false)

        setupBackButton() // Back Navigation
        setupAccountNumberCopyButton() // Account number copy
        setupMenuButtonInfo() // Top Bar menu
        setupDisplayAllAccountStatementsButton()
        setupAccountBalancesDialog() // Balances

        setupLoadingIndicator()

        displayAccount() // Load account info
        displayAccountStatement() // Load statements

        return binding.root
    }

    // region Buttons

    private fun setupAccountBalancesDialog() {
        binding.buttonBalances.setOnClickListener {
            val balancesDialog = AccountBalancesDialogFragment()
            balancesDialog.show(childFragmentManager, "balances")
        }
    }

    private fun setupAccountNumberCopyButton() {
        clipboard = requireActivity()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding.copyAccountNumberButton.setOnClickListener {
            val clip: ClipData = ClipData.newPlainText("copiedData", binding.accountNumber.text)
            clipboard!!.setPrimaryClip(clip)
            Toast.makeText(requireContext(), R.string.toast_message_copied_to_clipboard, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBackButton() {
        with(binding) {
            toolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupMenuButtonInfo() {
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            if (accountDetailsViewModel.accountDetails.value is Result.Success<Account>) {
                val accountInfo: Account =
                    (accountDetailsViewModel.accountDetails.value as Result.Success<Account>).data
                findNavController()
                    .navigate(
                        AccountDetailsFragmentDirections.actionNavFragmentAccountDetailsToNavFragmentAccountAdditionalDetails(
                            accountInfo.beneficiary.name,
                            accountInfo.accountNumber,
                            accountInfo.swift,
                            accountInfo.accountTypeDescription,
                            accountInfo.currencyName
                        )
                    )
            }
            true
        }
    }

    private fun setupDisplayAllAccountStatementsButton() {
        binding.buttonDisplayAllAccountStatements.setOnClickListener {
            findNavController()
                .navigate(
                    AccountDetailsFragmentDirections.actionNavFragmentAccountDetailsToNavFragmentAccountStatement()
                )
        }
    }

    private fun setupLoadingIndicator() {
        val doneLoadingMediatorData = MediatorLiveData<Boolean>()
        doneLoadingMediatorData.value = false
        // Observe is loading Account Details
        doneLoadingMediatorData.addSource(accountDetailsViewModel.isLoadingAccountDetails) {
            doneLoadingMediatorData.postValue(
                !accountDetailsViewModel.isLoadingAccountStatements.value!!
                        && !it
            )
        }
        // Observe is loading Account Statements
        doneLoadingMediatorData.addSource(accountDetailsViewModel.isLoadingAccountStatements) {
            doneLoadingMediatorData.postValue(
                !accountDetailsViewModel.isLoadingAccountDetails.value!!
                        && !it
            )
        }
        // Hide loader when everything is done loading
        doneLoadingMediatorData.observe(viewLifecycleOwner) { doneLoading ->
            if (doneLoading) {
                binding.loadingIndicator.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.VISIBLE
            }
        }
    }

    // endregion

    // region Display Account

    private fun displayAccount() {
        val accountId = requireArguments().getString("accountId")!!
        accountDetailsViewModel.accountId = accountId
        accountDetailsViewModel.getAccountDetails(accountId)
        accountDetailsViewModel.accountDetails.observe(viewLifecycleOwner) {
            if (it is Result.Error) {
                handleResultError(it, R.string.error_loading_accounts)
            }
            if (it is Result.Success) {
                if (it.data.product.code == BuildConfig.SPECIAL_ACCOUNT_CODE) {
                    binding.accountBalances.visibility = View.GONE
                }
                binding.accountName.text = it.data.product.name
                binding.accountNumber.text = it.data.accountNumber
                binding.accountCurrency.text = it.data.currencyName
                binding.accountBalance.text = NumberUtils.formatAmount(it.data.balance.available)
                binding.balancesDialogBlockedAmountAmount.text = NumberUtils.formatAmount(it.data.balance.blocked)
                binding.blockedAmountCurrency.text = it.data.currencyName
            }
        }
    }

    private fun setupAccountStatement() {
        statementsViewModel.raiseShouldFetchStatementsFlag()
        statementsViewModel.showProperty(ViewStatementProperty.REASON)
    }

    private fun displayAccountStatement() {
        // Attach observer for accountStatement in viewModel
        accountDetailsViewModel.accountStatement.observe(viewLifecycleOwner) {
            if (it == null || it is Result.Error) {
                handleResultError(it as Result.Error, R.string.error_loading_accounts_statements)
                return@observe
            }
            if (it is Result.Success) {
                if (it.data.size > 10) {
                    statementsViewModel.setAccountStatements(it.data.subList(0, 10))
                    binding.displayAllAccountStatements.visibility = View.VISIBLE
                } else {
                    statementsViewModel.setAccountStatements(it.data)
                }
            }
        }
        statementsViewModel.shouldFetchStatements.observe(viewLifecycleOwner) {
            if (it) {
                val accountId = requireArguments().getString("accountId")!!
                val filter = statementsViewModel.statementsFilter.value!!
                accountDetailsViewModel.getAccountStatement(
                    accountId,
                    filter.startDate,
                    filter.endDate,
                    DateUtils.getContextDateFormat(requireContext())
                )
                statementsViewModel.dropShouldFetchStatementsFlag()
            }
        }
    }

    // endregion

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
