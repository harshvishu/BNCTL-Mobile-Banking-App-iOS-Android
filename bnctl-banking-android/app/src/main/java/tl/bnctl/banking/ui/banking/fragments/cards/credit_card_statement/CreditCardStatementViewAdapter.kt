package tl.bnctl.banking.ui.banking.fragments.cards.credit_card_statement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.cards.model.CreditCardStatement
import tl.bnctl.banking.ui.utils.DateUtils

class CreditCardStatementViewAdapter(
    private val statementResult: LiveData<Result<List<CreditCardStatement>>>,
    private val onClickHandler: (CreditCardStatement) -> Unit
) : RecyclerView.Adapter<CreditCardStatementViewAdapter.CreditCardStatementViewHolder>() {

    class CreditCardStatementViewHolder(
        view: View,
        private val onClickHandler: (CreditCardStatement) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val statementNumber: TextView = view.findViewById(R.id.credit_card_statement_number)
        private val statementDate: TextView = view.findViewById(R.id.credit_card_statement_date)
        private val downloadButton: Button =
            view.findViewById(R.id.btn_download_credit_card_statement)

        fun bind(
            statement: CreditCardStatement
        ) {
            statementNumber.text = statement.statementId
            statementDate.text = DateUtils.formatDateAndTimeToUserPreference(
                itemView.context,
                statement.date!!
            )

            downloadButton.setOnClickListener {
                onClickHandler(statement)
            }
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): CreditCardStatementViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.list_item_credit_card_statement, viewGroup, false)
        return CreditCardStatementViewHolder(view, onClickHandler)
    }

    override fun onBindViewHolder(viewHolder: CreditCardStatementViewHolder, position: Int) {
        if (statementResult.value is Result.Success) {
            val statement: CreditCardStatement =
                (statementResult.value!! as Result.Success).data[position]
            viewHolder.bind(statement)
        }
    }

    override fun getItemCount(): Int {
        return if (statementResult.value is Result.Success) (statementResult.value!! as Result.Success).data.size else 0
    }

}
