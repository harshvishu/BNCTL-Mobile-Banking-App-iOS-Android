package tl.bnctl.banking.ui.banking.fragments.statements.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.R
import tl.bnctl.banking.data.accounts.model.StatementsDataHolder
import tl.bnctl.banking.ui.banking.fragments.statements.ViewStatementProperty
import tl.bnctl.banking.ui.banking.fragments.statements.details.StatementsDetailsFragmentDirections
import tl.bnctl.banking.ui.utils.DateUtils
import tl.bnctl.banking.ui.utils.NumberUtils
import java.math.BigDecimal

class StatementsViewAdapter(
    private val enableDetailedView: Boolean,
    private val detailsTitleResourceId: Int,
    private val viewStatementProperty: ViewStatementProperty,
    private val onNextPageRequested: (() -> Unit)? = null
) : ListAdapter<StatementsDataHolder, StatementsViewAdapter.AccountStatementViewHolder>(
    StatementDiffCallback
) {
    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }


    class AccountStatementViewHolder(view: View, private val viewType: Int) :
        RecyclerView.ViewHolder(view) {
        private lateinit var details: TextView
        private lateinit var dateOfExecution: TextView
        private lateinit var amount: TextView
        private lateinit var currency: TextView

        init {
            if (viewType == VIEW_TYPE_ITEM) {
                details = view.findViewById(R.id.account_statement_details)
                dateOfExecution = view.findViewById(R.id.account_statement_date)
                amount = view.findViewById(R.id.account_statement_amount)
                currency = view.findViewById(R.id.account_statement_currency)
            }
        }

        /**
         * Bind account-related data
         */
        fun bind(
            accountStatement: StatementsDataHolder,
            enableDetailedView: Boolean,
            detailsTitleResourceId: Int,
            viewStatementProperty: ViewStatementProperty
        ) {
            // We don't care if this is a loading view
            if (viewType == VIEW_TYPE_LOADING) {
                return
            }
            // debit is negative
            // credit is positive
            val isCredit: Boolean =
                BigDecimal(accountStatement.amount).compareTo(BigDecimal.ZERO) == 1

            val amountColor = if (isCredit) R.color.green else R.color.red

            amount.setTextColor(ContextCompat.getColor(itemView.context, amountColor))
            currency.setTextColor(ContextCompat.getColor(itemView.context, amountColor))
            amount.text = NumberUtils.formatAmount(accountStatement.amount)

            when (viewStatementProperty) {
                ViewStatementProperty.REASON -> {
                    if (accountStatement.reason.isBlank()) {
                        details.text = accountStatement.transactionType
                    } else {
                        details.text = accountStatement.reason
                    }
                }
                ViewStatementProperty.BENEFICIARY -> {
                    if (accountStatement.beneficiary?.isBlank() == true) {
                        details.text = accountStatement.transactionType
                    } else {
                        details.text = accountStatement.beneficiary
                    }
                }
                ViewStatementProperty.TRANSACTION_TYPE -> {
                    if (accountStatement.transactionType.isBlank()) {
                        if (accountStatement.beneficiary?.isBlank() == true) {
                            details.text = accountStatement.reason
                        }
                        details.text = accountStatement.beneficiary
                    } else {
                        details.text = accountStatement.transactionType
                    }
                }
            }

            if (accountStatement.dateOfExecution != null) {
                dateOfExecution.text = DateUtils.formatDateAndTimeToUserPreference(
                    itemView.context,
                    accountStatement.dateOfExecution!!
                )
            }
            currency.text = accountStatement.currency

            if (enableDetailedView) {
                itemView.setOnClickListener {
                    it.findNavController().navigate(
                        StatementsDetailsFragmentDirections.actionGlobalNavFragmentStatementsDetails(
                            accountStatement,
                            detailsTitleResourceId
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccountStatementViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        if (viewType == VIEW_TYPE_LOADING) {
            onNextPageRequested?.let { it() }
            view = inflater.inflate(R.layout.list_item_statement_empty_item, parent, false)
        } else {
            view = inflater.inflate(R.layout.list_item_statement, parent, false)
        }
        return AccountStatementViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: AccountStatementViewHolder, position: Int) {
        val accountStatement: StatementsDataHolder? = getItem(position)
        if (accountStatement != null) {
            holder.bind(
                accountStatement,
                enableDetailedView,
                detailsTitleResourceId,
                viewStatementProperty
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    object StatementDiffCallback : DiffUtil.ItemCallback<StatementsDataHolder>() {
        override fun areItemsTheSame(
            oldItem: StatementsDataHolder,
            newItem: StatementsDataHolder
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: StatementsDataHolder,
            newItem: StatementsDataHolder
        ): Boolean {
            return oldItem.amount == newItem.amount && oldItem.beneficiary == newItem.beneficiary && oldItem.reason == newItem.reason
                    && oldItem.status == newItem.status && oldItem.additionalInfo == newItem.additionalInfo && oldItem.currency == newItem.currency &&
                    oldItem.dateOfExecution == newItem.dateOfExecution

        }
    }
}
