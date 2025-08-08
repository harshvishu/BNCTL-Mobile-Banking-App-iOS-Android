package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.RadioGroup
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.FragmentSectionTransferDetailsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.custom.amount.field.AmountTextWatcher
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.details.adapter.CurrencyDropDownAdapter
import tl.bnctl.banking.ui.utils.NumberUtils
import java.math.BigDecimal


class TransferDetailsFragment : BaseFragment() {

    private var _binding: FragmentSectionTransferDetailsBinding? = null
    private val binding get() = _binding!!

    private val transferDetailsViewModel: TransferDetailsViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSectionTransferDetailsBinding.inflate(inflater, container, false)
        setUpAmountField()
        setUpReasonField()
        setUpCurrencyDropDownField()
        if (transferDetailsViewModel.enableType.value == false) {
            binding.transferDetailsTypeRadioGroup.visibility = View.GONE
            binding.transferDetailsTypeTransferLabel.visibility = View.GONE
        } else {
            setUpTypeField()
        }

        return binding.root
    }

    private fun setUpTypeField() {
        val type: RadioGroup = binding.transferDetailsTypeRadioGroup
        when (transferDetailsViewModel.type.value) {
            TransferDetailsType.STANDARD -> binding.transferDetailsTypeRadioStandard.isChecked =
                true
            TransferDetailsType.RINGS -> binding.transferDetailsTypeRadioRings.isChecked = true
            else -> {}
        }
        type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.transfer_details_type_radio_standard -> transferDetailsViewModel.changeType(
                    TransferDetailsType.STANDARD
                )
                R.id.transfer_details_type_radio_rings -> transferDetailsViewModel.changeType(
                    TransferDetailsType.RINGS
                )
            }
        }
    }

    private fun setUpAmountField() {
        val amountEditTextLayout: TextInputLayout = binding.transferDetailsAmountLayout
        val amountEditText: TextInputEditText = binding.transferDetailsAmount
        transferDetailsViewModel.amountError.observe(viewLifecycleOwner) {
            if (it != null) {
                amountEditTextLayout.error = resources.getString(it)
            } else {
                amountEditTextLayout.error = null
                amountEditTextLayout.isErrorEnabled = false
            }
        }

        val amount: Double? = transferDetailsViewModel.amount.value
        if (amount != null) {
            amountEditText.setText(NumberUtils.formatAmount(amount))
        }
        amountEditText.addTextChangedListener(AmountTextWatcher(amountEditText, requireContext()))
        amountEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newInput = (v as TextInputEditText)
                val textInput = newInput.text
                var amountText = textInput.toString()
                val decimalPlaces = BuildConfig.NUMBER_DECIMAL_PLACES.toInt()
                if (textInput!!.isEmpty()) {
                    amountText = "0"
                }
                val decimal: BigDecimal = BigDecimal(amountText.replace(',', '.')).setScale(
                    decimalPlaces,
                    BigDecimal.ROUND_FLOOR
                )
                transferDetailsViewModel.setAmount(decimal.toDouble())
                transferDetailsViewModel.validateAmount()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpCurrencyDropDownField() {
        val currencyDropDown: AutoCompleteTextView = binding.transferDetailsCurrency
        val transferDetailsCurrencyLayout = binding.transferDetailsCurrencyLayout

        currencyDropDown.isEnabled = transferDetailsViewModel.enableCurrencies.value == true
        currencyDropDown.isClickable = transferDetailsViewModel.enableCurrencies.value == true

        val currencies =
            resources.getStringArray(transferDetailsViewModel.availableCurrenciesResourceId.value!!)
        val adapter = CurrencyDropDownAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            currencies
        )
        currencyDropDown.setAdapter(adapter)

        if (currencyDropDown.isEnabled && currencyDropDown.isClickable) {
            currencyDropDown.onItemClickListener =
                AdapterView.OnItemClickListener { parent, _, position, _ ->
                    if (parent != null) {
                        val currency = parent.getItemAtPosition(position) as String
                        transferDetailsViewModel.selectCurrency(currency)
                    }
                }
            currencyDropDown.setOnClickListener {
                currencyDropDown.showDropDown()
            }
            currencyDropDown.setOnTouchListener { _, _ ->
                currencyDropDown.showDropDown()
                true
            }
            transferDetailsCurrencyLayout.setEndIconOnClickListener {
                currencyDropDown.showDropDown()
            }
            if (transferDetailsViewModel.selectedCurrency.value == null) {
                transferDetailsViewModel.selectCurrency(currencies[0])
            }
            transferDetailsViewModel.selectedCurrency.observe(viewLifecycleOwner) {
                currencyDropDown.setText(currencies[currencies.indexOf(it)], false)
            }
        } else {
            transferDetailsCurrencyLayout.isClickable = false
            transferDetailsCurrencyLayout.endIconMode = END_ICON_NONE
            transferDetailsViewModel.selectedCurrency.observe(viewLifecycleOwner) {
                currencyDropDown.setText(currencies[currencies.indexOf(it)], false)
            }
        }

    }

    private fun setUpReasonField() {
        val reasonEditTextLayout: TextInputLayout = binding.transferReasonLayout
        val reasonEditText: TextInputEditText = binding.transferReason

        // Set reason length according to the app config
        // Setting to MAX+1, so you actually get an error
        reasonEditText.filters += arrayOf<InputFilter>(LengthFilter(BuildConfig.TRANSFER_REASON_MAX_LENGTH + 1))
        transferDetailsViewModel.reasonError.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it == R.string.error_transfer_validation_required_reason) {
                    reasonEditTextLayout.error =
                        resources.getString(it, BuildConfig.TRANSFER_REASON_MAX_LENGTH)
                } else {
                    reasonEditTextLayout.error = resources.getString(it)
                }
            } else {
                reasonEditTextLayout.error = null
                reasonEditTextLayout.isErrorEnabled = false
            }
        }
        reasonEditText.setText(transferDetailsViewModel.reason.value)
        reasonEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newInput = (v as TextInputEditText)
                transferDetailsViewModel.setReason(newInput.text.toString())
                transferDetailsViewModel.validateReason()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
