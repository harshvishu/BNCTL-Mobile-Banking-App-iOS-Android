package tl.bnctl.banking.ui.banking.fragments.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.ui.utils.NumberUtils

class AccountsViewAdapter(
    private val accountResult: LiveData<Result<List<Account>>>,
    private val onClickHandler: (Account) -> Unit
) : RecyclerView.Adapter<AccountsViewAdapter.ViewHolder>() {

    class ViewHolder(view: View, val onClickHandler: (Account) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private var selectedAccount: Account? = null

        private val accountName: TextView = view.findViewById(R.id.account_name)
        private val balance: TextView = view.findViewById(R.id.account_balance)
        private val labelBalance: TextView = view.findViewById(R.id.label_balance)
        private val balanceCurrency: TextView = view.findViewById(R.id.account_currency)
        private val currency: TextView = view.findViewById(R.id.account_currency)
        private val accountNumber: TextView = view.findViewById(R.id.account_account_number)

        init {
            itemView.setOnClickListener {
                selectedAccount?.let {
                    onClickHandler(it)
                }
            }
        }

        fun bind(account: Account) {
            selectedAccount = account
            accountName.text = account.accountTypeDescription
            accountNumber.text = account.accountNumber
            balance.text = NumberUtils.formatAmount(account.balance.available)
            currency.text = account.currencyName
            if (account.product.code == BuildConfig.SPECIAL_ACCOUNT_CODE) {
                labelBalance.visibility = View.GONE
                balance.visibility = View.GONE
                balanceCurrency.visibility = View.GONE
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_fragment_dashboard_account, viewGroup, false)
        return ViewHolder(view, onClickHandler)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (accountResult.value is Result.Success) {
            val account: Account =
                (accountResult.value as Result.Success).data[position]
            viewHolder.setIsRecyclable(false)
            viewHolder.bind(account)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (accountResult.value is Result.Success) {
            return (accountResult.value as Result.Success).data.size
        }
        return 0
    }
}
