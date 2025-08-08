package tl.bnctl.banking.ui.banking.fragments.accounts.statements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.databinding.FragmentAccountStatementsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.accounts.details.AccountDetailsViewModel
import tl.bnctl.banking.ui.banking.fragments.accounts.details.AccountDetailsViewModelFactory
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsViewModel
import tl.bnctl.banking.ui.utils.DateUtils

class AccountStatementsFragment: BaseFragment() {

    private var _binding: FragmentAccountStatementsBinding? = null
    private val binding get() = _binding!!

    private val accountDetailsViewModel: AccountDetailsViewModel
        by navGraphViewModels(R.id.nav_fragment_account_details) { AccountDetailsViewModelFactory() }
    private val statementsViewModel: StatementsViewModel
        by navGraphViewModels(R.id.nav_fragment_account_details) { defaultViewModelProviderFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountStatementsBinding.inflate(layoutInflater)

        setupLoadingIndicator()
        setupBackButton()
        displayAccountStatement()

        return binding.root
    }

    private fun setupLoadingIndicator() {
        val doneLoadingMediatorData = MediatorLiveData<Boolean>()
        doneLoadingMediatorData.value = false
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

    private fun displayAccountStatement() {
        statementsViewModel.shouldFetchStatements.observe(viewLifecycleOwner) {
            if (it) {
                val accountId = (accountDetailsViewModel.accountDetails.value as Result.Success).data.accountId
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
        accountDetailsViewModel.accountStatement.observe(viewLifecycleOwner) {
            if (it == null || it is Result.Error) {
                handleResultError(it as Result.Error, R.string.error_loading_accounts_statements)
                return@observe
            }
            if (it is Result.Success) {
                statementsViewModel.setAccountStatements(it.data)
            }
        }
    }

    private fun setupBackButton() {
        with(binding) {
            toolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}