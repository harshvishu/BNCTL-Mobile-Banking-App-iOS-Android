package tl.bnctl.banking.ui.banking.fragments.transfers.templates.adapter

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.annotation.RequiresApi
import tl.bnctl.banking.data.templates.model.TemplateTransferType

class TransferTypeDropDownAdapter(
    context: Context,
    resource: Int,
    var items: List<TemplateTransferType>
) : ArrayAdapter<TemplateTransferType>(context, resource, items) {

    override fun getItem(position: Int): TemplateTransferType {
        return items[position]
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        // Set first view to be greyed out so we have a "hint"
        // val color = if (position == 0) R.color.text_color_grey else R.color.text_color_primary
        // label.setTextColor(context.getColor(color))
        label.text = items[position].name
        if (position == 0) {
            label.setPadding(0, 0, 0, 0)
        }
        return label
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        // Set first view to be greyed out so we have a "hint"
        // val color = if (position == 0) R.color.text_color_grey else R.color.text_color_primary
        // label.setTextColor(context.getColor(color))
        label.text = items[position].name
        return label
    }

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