package tl.bnctl.banking.ui.banking.fragments.transfers.templates.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.templates.model.Template

class TemplatesViewAdapter(
    private val templatesResult: LiveData<Result<List<Template>>>,
    private val context: Context,
    private val onClickHandler: (Template) -> Unit
) : RecyclerView.Adapter<TemplatesViewAdapter.TemplateViewHolder>() {

    class TemplateViewHolder(view: View, onClickHandler: (Template) -> Unit) :
        RecyclerView.ViewHolder(view) {

        private var selectedTemplate: Template? = null

        private val templateName: TextView = view.findViewById(R.id.template_name)
        private val templateDestinationAccount: TextView =
            view.findViewById(R.id.template_destination_account)

        init {
            itemView.setOnClickListener {
                selectedTemplate?.let {
                    onClickHandler(it)
                }
            }
        }

        fun bind(
            template: Template,
            context: Context
        ) {
            selectedTemplate = template
            templateName.text = template.name
            templateDestinationAccount.text = template.accountNumber
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TemplateViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.list_item_template, viewGroup, false)
        return TemplateViewHolder(view, onClickHandler)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        if (templatesResult.value is Result.Success) {
            val template: Template =
                (templatesResult.value as Result.Success).data[position]
            holder.bind(template, context)
        }
    }

    override fun getItemCount(): Int {
        if (templatesResult.value is Result.Success) {
            return (templatesResult.value as Result.Success).data.size
        }
        return 0
    }
}