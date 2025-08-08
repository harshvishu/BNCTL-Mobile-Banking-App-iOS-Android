package tl.bnctl.banking.ui.banking.fragments.transfers.history

import androidx.fragment.app.viewModels
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsFragment
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsViewModel

class TransferHistoryStatementsFragment: StatementsFragment() {

    override fun initStatementsViewModel() {
        val scopedStatementsViewModel: StatementsViewModel by viewModels({ requireParentFragment() })
        statementsViewModel = scopedStatementsViewModel
    }

    override fun onNextPageRequested() {
        statementsViewModel.raiseShouldFetchNextStatementsPageFlag()
    }


}