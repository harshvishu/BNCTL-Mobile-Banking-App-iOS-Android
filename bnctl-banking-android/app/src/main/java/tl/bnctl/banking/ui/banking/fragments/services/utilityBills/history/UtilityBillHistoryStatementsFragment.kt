package tl.bnctl.banking.ui.banking.fragments.services.utilityBills.history

import androidx.fragment.app.viewModels
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsFragment
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsViewModel

class UtilityBillHistoryStatementsFragment: StatementsFragment() {

    override fun initStatementsViewModel() {
        val scopedStatementsViewModel: StatementsViewModel by viewModels({ requireParentFragment() })
        statementsViewModel = scopedStatementsViewModel
    }

    override fun onNextPageRequested() {
        // whistle
    }
}