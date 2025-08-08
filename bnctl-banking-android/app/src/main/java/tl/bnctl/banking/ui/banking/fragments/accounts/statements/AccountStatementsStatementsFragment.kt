package tl.bnctl.banking.ui.banking.fragments.accounts.statements

import androidx.navigation.navGraphViewModels
import tl.bnctl.banking.R
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsFragment
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsViewModel

/**
 * Concrete implementation of the StatementsFragment.
 * For use in AccountStatements Fragment.
 */
class AccountStatementsStatementsFragment: StatementsFragment() {

    override fun initStatementsViewModel() {
        val scopedStatementsViewModel: StatementsViewModel
            by navGraphViewModels(R.id.nav_fragment_account_details) { defaultViewModelProviderFactory }
        statementsViewModel = scopedStatementsViewModel
    }

    override fun onNextPageRequested() {
        // Not implemented here
    }
}