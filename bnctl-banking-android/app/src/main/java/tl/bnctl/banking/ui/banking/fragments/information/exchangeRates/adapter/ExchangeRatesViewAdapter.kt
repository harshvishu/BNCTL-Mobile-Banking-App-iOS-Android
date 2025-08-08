package tl.bnctl.banking.ui.banking.fragments.information.exchangeRates.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.exchange_rates.model.ExchangeRate

class ExchangeRatesViewAdapter(
    private val exchangeRatesResult: LiveData<Result<List<ExchangeRate>>?>,
    private val context: Context
) : RecyclerView.Adapter<ExchangeRatesViewAdapter.ExchangeRatesViewHolder>() {

    class ExchangeRatesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val flagIcon: ImageView = view.findViewById(R.id.flag_icon)
        private val currencyLabel: TextView = view.findViewById(R.id.currency_label)
        private val currencyUnits: TextView = view.findViewById(R.id.currency_units)
        private val fixedRateAmount: TextView = view.findViewById(R.id.fixed_rate_amount)
        private val cashBuyAmount: TextView = view.findViewById(R.id.cash_buy_amount)
        private val cashSellAmount: TextView = view.findViewById(R.id.cash_sell_amount)
        private val cashlessBuyAmount: TextView = view.findViewById(R.id.cashless_buy_amount)
        private val cashlessSellAmount: TextView = view.findViewById(R.id.cashless_sell_amount)

        fun bind(
            exchangeRate: ExchangeRate,
            context: Context
        ) {
            val resId = itemView.resources.getIdentifier(
                "flag_${exchangeRate.currencyCode.lowercase()}",
                "drawable",
                context.packageName
            )
            flagIcon.setImageResource(resId)
            currencyLabel.text = exchangeRate.currencyCode
            currencyUnits.text = exchangeRate.currencyUnits.toString()
            fixedRateAmount.text = exchangeRate.fixedRate
            cashBuyAmount.text = exchangeRate.cashBuyRate
            cashSellAmount.text = exchangeRate.cashSellRate
            cashlessBuyAmount.text = exchangeRate.cashlessBuyRate
            cashlessSellAmount.text = exchangeRate.cashlessSellRate
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ExchangeRatesViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.list_item_exchange_rate, viewGroup, false)
        return ExchangeRatesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExchangeRatesViewHolder, position: Int) {
        if (exchangeRatesResult.value is Result.Success) {
            val exchangeRate: ExchangeRate =
                (exchangeRatesResult.value as Result.Success).data[position]
            holder.bind(exchangeRate, context)
        }
    }

    override fun getItemCount(): Int {
        if (exchangeRatesResult.value is Result.Success) {
            return (exchangeRatesResult.value as Result.Success).data.size
        }
        return 0;
    }

}
