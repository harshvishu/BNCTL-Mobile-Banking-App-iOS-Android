package tl.bnctl.banking.ui.banking.fragments.accounts.details.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.navigation.navGraphViewModels
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.databinding.FragmentAccountBalancesDialogBinding
import tl.bnctl.banking.ui.banking.fragments.accounts.details.AccountDetailsViewModel
import tl.bnctl.banking.ui.banking.fragments.accounts.details.AccountDetailsViewModelFactory
import tl.bnctl.banking.ui.utils.NumberUtils

class AccountBalancesDialogFragment : DialogFragment() {

    private var _binding: FragmentAccountBalancesDialogBinding? = null
    private val binding get() = _binding!!

    private val accountDetailsViewModel: AccountDetailsViewModel by navGraphViewModels(
        R.id.nav_fragment_account_details
    ) { AccountDetailsViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBalancesDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(
            R.drawable.dialog_background
        )
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (accountDetailsViewModel.accountDetails.value !is Result.Success) {
            // If there is no Account info.. do nothing.
            return
        }
        displayBalances()
        setupCloseButton()
    }

    private fun displayBalances() {
        val context = requireContext()
        val account: Account =
            (accountDetailsViewModel.accountDetails.value as Result.Success<Account>).data
        // Opening Balance
        val supportedAccountBalances = BuildConfig.SUPPORTED_ACCOUNT_BALANCES
        if (supportedAccountBalances.contains(OPENING_BALANCE)) {
            binding.balancesDialogOpeningBalance.visibility = View.VISIBLE
            binding.balancesDialogOpeningBalanceAmount.text =
                NumberUtils.formatAmountWithCurrency(
                    context,
                    account.balance.opening,
                    account.currencyName
                )
        } else {
            binding.balancesDialogOpeningBalance.visibility = View.GONE
        }

        // Current Balance
        if (supportedAccountBalances.contains(CURRENT_BALANCE)) {
            binding.balancesDialogCurrentBalance.visibility = View.VISIBLE
            binding.balancesDialogCurrentBalanceAmount.text =
                NumberUtils.formatAmountWithCurrency(
                    context,
                    account.balance.current,
                    account.currencyName
                )
        } else {
            binding.balancesDialogCurrentBalance.visibility = View.GONE
        }

        // Available Balance
        if (supportedAccountBalances.contains(AVAILABLE_BALANCE)) {
            binding.balancesDialogAvailableBalance.visibility = View.VISIBLE
            binding.balancesDialogAvailableBalanceAmount.text =
                NumberUtils.formatAmountWithCurrency(
                    context,
                    account.balance.available,
                    account.currencyName
                )
        } else {
            binding.balancesDialogAvailableBalance.visibility = View.GONE
        }
        // Overdraft Balance
        if (supportedAccountBalances.contains(OVERDRAFT_BALANCE)) {
            binding.balancesDialogOverdraftBalance.visibility = View.VISIBLE
            binding.balancesDialogOverdraftBalanceAmount.text =
                NumberUtils.formatAmountWithCurrency(
                    context,
                    account.balance.overdraft,
                    account.currencyName
                )
        } else {
            binding.balancesDialogOverdraftBalance.visibility = View.GONE
        }
        // Blocked Amount
        if (supportedAccountBalances.contains(BLOCKED_BALANCE)) {
            binding.balancesDialogBlockedAmount.visibility = View.VISIBLE
            binding.balancesDialogBlockedAmountAmount.text =
                NumberUtils.formatAmountWithCurrency(
                    context,
                    account.balance.blocked,
                    account.currencyName
                )
        } else {
            binding.balancesDialogBlockedAmount.visibility = View.GONE
        }
    }

    private fun setupCloseButton() {
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            dismiss()
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // Constants used for showing different types of balances according to the app configuration
        const val CURRENT_BALANCE = "current"
        const val AVAILABLE_BALANCE = "available"
        const val BLOCKED_BALANCE = "blocked"
        const val OPENING_BALANCE = "opening"
        const val OVERDRAFT_BALANCE = "overdraft"
    }
}