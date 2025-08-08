package tl.bnctl.banking.ui.banking.fragments.transfers.pending.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.transfers.model.PendingTransfer
import tl.bnctl.banking.ui.banking.fragments.transfers.pending.PendingTransfersViewModel

class PendingTransfersViewAdapter(
    private val viewModel: PendingTransfersViewModel,
    private val pendingTransfersResult: LiveData<Result<List<PendingTransfer>>?>
) : RecyclerView.Adapter<PendingTransfersViewAdapter.PendingTransferViewHolder>() {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): PendingTransferViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.list_item_transfer_pending, viewGroup, false)
        return PendingTransferViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingTransferViewHolder, position: Int) {
        if (pendingTransfersResult.value is Result.Success) {
            val pendingTransfer: PendingTransfer =
                (pendingTransfersResult.value as Result.Success).data[position]
            holder.bind(pendingTransfer, viewModel)
        }
    }

    override fun getItemCount(): Int {
        if (pendingTransfersResult.value is Result.Success) {
            return (pendingTransfersResult.value as Result.Success).data.size
        }
        return 0;
    }

    class PendingTransferViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val checkBox: CheckBox = view.findViewById(R.id.transfer_pending_check_box)
        private val beneficiary: TextView =
            view.findViewById(R.id.transfer_pending_value_beneficiary)
        private val reason: TextView = view.findViewById(R.id.transfer_pending_value_reason)
        private val fromAccount: TextView =
            view.findViewById(R.id.transfer_pending_value_from_account)
        private val toAccount: TextView = view.findViewById(R.id.transfer_pending_value_to_account)
        private val amount: TextView = view.findViewById(R.id.transfer_pending_amount)
        private val currency: TextView = view.findViewById(R.id.transfer_pending_currency)
        private val documents: TextView = view.findViewById(R.id.transfer_pending_value_documents)

        fun bind(
            pendingTransfer: PendingTransfer,
            viewModel: PendingTransfersViewModel
        ) {
            checkBox.isChecked = pendingTransfer.isSelected
            checkBox.setOnClickListener {
                if (it is CheckBox) {
                    pendingTransfer.isSelected = it.isChecked
                    viewModel.changeNumberOfSelectedPendingTransfers(pendingTransfer.isSelected)
                }
            }
            checkBox.text = pendingTransfer.transactionType
            beneficiary.text = pendingTransfer.beneficiaryName
            if (beneficiary.text.isEmpty()) {
                view.findViewById<LinearLayout>(R.id.beneficiary).visibility = View.GONE
            }
            reason.text = pendingTransfer.description
            if (reason.text.isEmpty()) {
                view.findViewById<LinearLayout>(R.id.row_reason).visibility = View.GONE
            }
            fromAccount.text = pendingTransfer.sourceAccount
            toAccount.text = pendingTransfer.destinationAccount
            if (toAccount.text.isEmpty()) {
                view.findViewById<LinearLayout>(R.id.row_to_account).visibility = View.GONE
            }
            amount.text = pendingTransfer.amount
            currency.text = pendingTransfer.currency
            documents.text = pendingTransfer.numberOfDocuments.toString()
            if (pendingTransfer.numberOfDocuments == 0) {
                view.findViewById<LinearLayout>(R.id.row_number_of_documents).visibility = View.GONE
            }
        }
    }
}
