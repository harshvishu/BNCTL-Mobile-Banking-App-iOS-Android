package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.details.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class CurrencyDropDownAdapter(
    context: Context,
    resource: Int,
    var items: Array<out String>
) : ArrayAdapter<String>(context, resource, items) {

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                filterResults.values = items
                filterResults.count = items.size
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }
}