package tl.bnctl.banking.ui.banking.fragments.services.cashWithdrawal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import tl.bnctl.banking.R
import tl.bnctl.banking.data.branches.model.Branch

class BranchInfoAdapter(context: Context, private val branches: Array<out Branch>) :
    ArrayAdapter<Branch>(context, R.layout.list_item_drop_down, branches), Filterable {

    private val listFilter = object : Filter() {

        override fun performFiltering(filterBy: CharSequence?): FilterResults? {
            return null
        }

        override fun publishResults(filterBy: CharSequence?, result: FilterResults?) {
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as Branch).name
        }

    }

    override fun getFilter(): Filter {
        return listFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return newView(convertView, parent, position)
    }

    private fun newView(
        convertView: View?,
        parent: ViewGroup,
        position: Int
    ): View {
        val view: View = convertView
            ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_drop_down, parent, false)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = branches[position].name
        return view
    }

    override fun getItem(position: Int): Branch {
        return branches[position]
    }

    override fun getCount(): Int {
        return branches.size
    }
}