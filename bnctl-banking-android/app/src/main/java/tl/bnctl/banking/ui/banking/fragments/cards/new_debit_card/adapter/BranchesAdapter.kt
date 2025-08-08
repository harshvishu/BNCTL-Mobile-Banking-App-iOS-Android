package tl.bnctl.banking.ui.banking.fragments.cards.new_debit_card.adapter

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import tl.bnctl.banking.R
import tl.bnctl.banking.data.branches.model.Branch

class BranchesAdapter(
    context: Context,
    textViewResourceId: Int,
    values: List<Branch>,
    hintValue: Branch
) : ArrayAdapter<Branch>(context, textViewResourceId, values) {

    private val _values: List<Branch>

    override fun getCount(): Int {
        return _values.size
    }

    override fun getItem(position: Int): Branch {
        return _values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        // Set first view to be greyed out so we have a "hint"
        val color = if (position == 0) R.color.text_color_grey else R.color.text_color_primary
        label.setTextColor(context.getColor(color))
        label.text = _values[position].name

        return label
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        // Set first view to be greyed out so we have a "hint"
        val color = if (position == 0) R.color.text_color_grey else R.color.text_color_primary
        label.setTextColor(context.getColor(color))
        label.text = _values[position].name
        return label
    }

    override fun isEnabled(position: Int): Boolean {
        // Set first position as disabled
        return position > 0
    }

    init {
        val listOf = mutableListOf(hintValue)
        listOf.addAll(values)
        _values = listOf
    }
}
