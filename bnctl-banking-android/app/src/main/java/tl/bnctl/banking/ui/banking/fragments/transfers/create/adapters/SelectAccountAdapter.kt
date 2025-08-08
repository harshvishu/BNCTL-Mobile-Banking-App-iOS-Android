package tl.bnctl.banking.ui.banking.fragments.transfers.create.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.R
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.ui.banking.fragments.dashboard.adapter.AccountsViewAdapter

class SelectAccountAdapter(
    private val accounts: List<Account>,
    private val onClickHandler: (Account) -> Unit
) : RecyclerView.Adapter<AccountsViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): AccountsViewAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_fragment_dashboard_account, viewGroup, false)
        return AccountsViewAdapter.ViewHolder(view, onClickHandler)
    }

    override fun onBindViewHolder(viewHolder: AccountsViewAdapter.ViewHolder, position: Int) {
        val account = accounts[position] as Account
        viewHolder.setIsRecyclable(false)
        viewHolder.bind(account)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return accounts.size
    }
}