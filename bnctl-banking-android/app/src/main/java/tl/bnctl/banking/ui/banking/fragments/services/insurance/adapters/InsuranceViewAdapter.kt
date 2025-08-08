package tl.bnctl.banking.ui.banking.fragments.services.insurance.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.insurance.model.Insurance
import tl.bnctl.banking.ui.banking.fragments.services.insurance.InsuranceFragmentDirections
import tl.bnctl.banking.ui.banking.fragments.services.insurance.InsuranceViewModel
import tl.bnctl.banking.ui.utils.DateUtils

class InsuranceViewAdapter(
    private val viewModel: InsuranceViewModel
) : RecyclerView.Adapter<InsuranceViewAdapter.InsuranceViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): InsuranceViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.list_item_insurance, viewGroup, false)
        return InsuranceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InsuranceViewHolder, position: Int) {
        if (viewModel.insurances.value is Result.Success) {
            val insurance: Insurance =
                (viewModel.insurances.value as Result.Success).data[position]
            holder.bind(insurance, viewModel)
        }
    }

    override fun getItemCount(): Int {
        if (viewModel.insurances.value is Result.Success) {
            return (viewModel.insurances.value as Result.Success).data.size
        }
        return 0
    }

    class InsuranceViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val amountBGN: TextView = view.findViewById(R.id.insurance_value_amount_bgn)
        private val amountEUR: TextView = view.findViewById(R.id.insurance_value_amount_eur)
        private val insuranceType: TextView = view.findViewById(R.id.insurance_type)
        private val date: TextView = view.findViewById(R.id.insurance_value_date)
        private val policyNumber: TextView = view.findViewById(R.id.insurance_value_policy_number)
        private val insurerName: TextView = view.findViewById(R.id.insurance_value_insurer_name)
        private val accountBGN: TextView = view.findViewById(R.id.insurance_value_to_account_bgn)
        private val accountEUR: TextView = view.findViewById(R.id.insurance_value_to_account_other)
        private val billNumber: TextView =
            view.findViewById(R.id.insurance_value_installment_number)
        private val payButton: Button = view.findViewById(R.id.insurance_pay_button)

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(insurance: Insurance, viewModel: InsuranceViewModel) {
            payButton.setOnClickListener {
                viewModel.selectInsurance(insurance)
                view.findNavController()
                    .navigate(InsuranceFragmentDirections.actionNavFragmentInsuranceToNavFragmentConfirm())
            }

            if (insurance.iban == null) {
                val viewOtherAccount = view.findViewById<LinearLayout>(R.id.row_to_account_other)
                viewOtherAccount.visibility = View.GONE
                val viewOtherAmount = view.findViewById<LinearLayout>(R.id.row_amount_other)
                viewOtherAmount.visibility = View.GONE
            }
            if (insurance.billNumber == null) {
                val viewBillNumber = view.findViewById<LinearLayout>(R.id.row_installment_number)
                viewBillNumber.visibility = View.GONE
            }

            amountBGN.text = "${insurance.amountBgn} BGN"
            amountEUR.text = "${insurance.amount} ${insurance.currency}"
            insuranceType.text = insurance.insurer
            date.text = DateUtils.formatDate(view.context, insurance.dueDate, "dd/MM/yyyy")
            policyNumber.text = insurance.policy
            insurerName.text = insurance.insuranceAgencyName
            accountBGN.text = insurance.ibanBgn
            accountEUR.text = insurance.iban
            billNumber.text = insurance.billNumber

            val labelCurrency = view.findViewById<TextView>(R.id.insurance_label_amount_currency)
            labelCurrency.text = view.resources.getString(
                R.string.insurance_list_item_label_amount_currency,
                insurance.currency
            )
            val labelCurrencyAccount =
                view.findViewById<TextView>(R.id.insurance_label_to_account_currency)
            labelCurrencyAccount.text = view.resources.getString(
                R.string.insurance_list_item_label_account_currency,
                insurance.currency
            )
        }
    }
}