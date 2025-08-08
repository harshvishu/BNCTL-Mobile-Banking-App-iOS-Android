package tl.bnctl.banking.ui.banking.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.databinding.FragmentDashboardBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.dashboard.adapter.AccountsViewAdapter

class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var accountsViewAdapter: AccountsViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        dashboardViewModel =
            ViewModelProvider(this, DashboardViewModelFactory())[DashboardViewModel::class.java]
        accountsViewAdapter =
            AccountsViewAdapter(dashboardViewModel.accounts, onAccountClickHandler)
        binding.accountsRecyclerView.adapter = accountsViewAdapter

        // fetchCards()

        dashboardViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.loadingIndicator.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        checkAndRedirect()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (
            dashboardViewModel.accounts.value == null ||
            dashboardViewModel.accounts.value is Result.Error ||
            accountsListIsEmpty()
        ) {
            fetchAccounts()
        }
    }

    override fun onPause() {
        super.onPause()
        //if (dashboardViewModel.accounts.value is Result.Error) {
        dashboardViewModel.setAccountsResult(Result.Success(listOf()))
        //}
    }

    private fun accountsListIsEmpty(): Boolean {
        if (!(dashboardViewModel.accounts.value is Result.Success)) {
            return true
        }
        return (dashboardViewModel.accounts.value as Result.Success).data.isEmpty()
    }

    private fun checkAndRedirect() {
        // Do not use navArgs because it'll keep redirecting to the account details since there is no reliable way to clear them
        val arguments = requireArguments()
        val redirectToAccountId = arguments.getString("redirectToAccountId")
        if (redirectToAccountId != null) {
            arguments.clear()
            findNavController()
                .navigate(
                    DashboardFragmentDirections
                        .actionNavFragmentDashboardToNavFragmentAccountDetails(redirectToAccountId)
                )

        }
    }

    private fun fetchAccounts() {
        dashboardViewModel.accountFetch()
        dashboardViewModel.accounts.observe(
            viewLifecycleOwner,
            handleResult<Result<List<Account>>>({
                handleResultError(it, R.string.error_loading_accounts)
            }, {
                accountsViewAdapter.notifyItemRangeChanged(0, (it as Result.Success).data.size)
            })
        )
    }

//    private fun fetchCards() {
//        dashboardViewModel.cardFetch()
//        dashboardViewModel.cards.observe(viewLifecycleOwner, {
//            if (it == null || it is Result.Error) {
//                DialogFactory.createCancellableDialog(
//                    requireContext(), R.string.error_loading_cards
//                ).show()
//            }
//        })
//    }

    private val onAccountClickHandler: (Account) -> Unit = {
        findNavController()
            .navigate(
                DashboardFragmentDirections
                    .actionNavFragmentDashboardToNavFragmentAccountDetails(it.accountId)
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}