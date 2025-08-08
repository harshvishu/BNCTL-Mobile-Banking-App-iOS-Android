package tl.bnctl.banking.ui.banking.fragments.services.utilityBills.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.billers.model.UtilityBill
import tl.bnctl.banking.ui.utils.NumberUtils

class UtilityBillViewAdapter(
    private val billerResult: LiveData<Result<List<UtilityBill>>>,
) : RecyclerView.Adapter<UtilityBillViewAdapter.UtilityBillViewHolder>() {

    class UtilityBillViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val customName: CheckBox = view.findViewById(R.id.utility_bill_custom_name)
        private val providerName: TextView = view.findViewById(R.id.utility_bill_provider_name)
        private val subscriptionNumber: TextView =
            view.findViewById(R.id.utility_bill_subscription_number)
        private val amount: TextView = view.findViewById(R.id.utility_bill_amount)
        private val currency: TextView = view.findViewById(R.id.utility_bill_currency)

        fun bind(
            utilityBill: UtilityBill
        ) {
            customName.text = utilityBill.name
            customName.isChecked = utilityBill.isSelected
            customName.setOnCheckedChangeListener { _, isChecked ->
                utilityBill.isSelected = isChecked
            }
            providerName.text = utilityBill.biller.name
            subscriptionNumber.text = utilityBill.clientReference
            val billAmount = utilityBill.billAmount.toDoubleOrNull()
            if (billAmount != null && billAmount > 0) {
                customName.isEnabled = true
                amount.text = NumberUtils.formatAmount(billAmount)
                currency.text = utilityBill.currencyName
            } else {
                customName.isEnabled = false
                customName.isChecked = false
                var message = R.string.utility_bills_paid
                var messageColor = R.color.green
                if (utilityBill.status == BuildConfig.UTILITY_BILL_ERROR_STATUS) {
                    message = R.string.error_utility_bills_checking_amount_due
                    messageColor = R.color.red
                }
                amount.text = itemView.resources.getText(message)
                amount.setTextColor(ContextCompat.getColor(itemView.context, messageColor))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UtilityBillViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_utility_bill_card, parent, false)
        return UtilityBillViewHolder(view)
    }

    override fun onBindViewHolder(holder: UtilityBillViewHolder, position: Int) {
        if (billerResult.value is Result.Success) {
            val biller: UtilityBill =
                (billerResult.value as Result.Success).data[position]
            holder.setIsRecyclable(false)
            holder.bind(biller)
        }
    }

    override fun getItemCount(): Int {
        if (billerResult.value is Result.Success) {
            return (billerResult.value as Result.Success).data.size
        }
        return 0
    }
}