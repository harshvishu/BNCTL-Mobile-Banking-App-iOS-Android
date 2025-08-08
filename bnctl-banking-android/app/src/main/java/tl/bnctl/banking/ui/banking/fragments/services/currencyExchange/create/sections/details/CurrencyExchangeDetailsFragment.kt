package tl.bnctl.banking.ui.banking.fragments.services.currencyExchange.create.sections.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.currencyExchange.enums.CurrencyExchangeOperationType
import tl.bnctl.banking.databinding.FragmentSectionCurrencyExchangeDetailsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.custom.amount.field.AmountTextWatcher
import tl.bnctl.banking.ui.utils.NumberUtils
import java.math.BigDecimal

class CurrencyExchangeDetailsFragment : BaseFragment() {

    private var _binding: FragmentSectionCurrencyExchangeDetailsBinding? = null
    private val binding get() = _binding!!

    private val currencyExchangeDetailsViewModel: CurrencyExchangeDetailsViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSectionCurrencyExchangeDetailsBinding.inflate(inflater, container, false)
        populateView()
        setUpAmountField()
        setUpPreferentialRatesPinField()
        setUpTypeField()
        setUpCurrencyField()
        return binding.root
    }

    private fun populateView() {
        binding.currencyExchangeDetailsPreferentialRatesPin.setText(currencyExchangeDetailsViewModel.preferentialRatePin.value?.toString())
        when (currencyExchangeDetailsViewModel.type.value) {
            CurrencyExchangeOperationType.SELL -> binding.currencyExchangeDetailsOperationTypeRadioButtonSell.isChecked =
                true
            CurrencyExchangeOperationType.BUY -> binding.currencyExchangeDetailsOperationTypeRadioButtonBuy.isChecked =
                true
            else -> {}
        }
    }

    private fun setUpCurrencyField() {
        currencyExchangeDetailsViewModel.selectedCurrency.observe(viewLifecycleOwner) {
            binding.currencyExchangeDetailsCurrency.text = it
        }
    }

    private fun setUpAmountField() {
        val amountLayout: TextInputLayout = binding.currencyExchangeDetailsAmountLayout
        val amountEditText: TextInputEditText = binding.currencyExchangeDetailsAmount
        currencyExchangeDetailsViewModel.amountError.observe(viewLifecycleOwner) {
            if (it != null) {
                amountLayout.error = resources.getString(it)
            } else {
                amountLayout.error = null
                amountLayout.isErrorEnabled = false
            }
        }
        val amount: Double? = currencyExchangeDetailsViewModel.amount.value
        if (amount != null) {
            amountEditText.setText(
                NumberUtils.formatAmount(amount)
            )
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
                currencyExchangeDetailsViewModel.setAmount(decimal.toDouble())
                currencyExchangeDetailsViewModel.validateAmount()
            }
        }
    }

    private fun setUpPreferentialRatesPinField() {
        val preferentialRatesPinLayout: TextInputLayout =
            binding.currencyExchangeDetailsPreferentialRatesPinLayout
        val preferentialRatesPin: TextInputEditText =
            binding.currencyExchangeDetailsPreferentialRatesPin
        currencyExchangeDetailsViewModel.preferentialRatesPinError.observe(viewLifecycleOwner) {
            if (it != null) {
                preferentialRatesPinLayout.error = resources.getString(it)
            } else {
                preferentialRatesPinLayout.error = null
                preferentialRatesPinLayout.isErrorEnabled = false
            }
        }
        preferentialRatesPin.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newInput = (v as TextInputEditText)
                currencyExchangeDetailsViewModel.setPreferentialRatesPin(newInput.text.toString())
                currencyExchangeDetailsViewModel.validatePreferentialRatesPin()
            }
        }
    }

    private fun setUpTypeField() {
        val type: RadioGroup = binding.currencyExchangeDetailsOperationTypeRadioGroup
        type.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.currency_exchange_details_operation_type_radio_button_sell -> currencyExchangeDetailsViewModel.changeType(
                    CurrencyExchangeOperationType.SELL
                )
                R.id.currency_exchange_details_operation_type_radio_button_buy -> currencyExchangeDetailsViewModel.changeType(
                    CurrencyExchangeOperationType.BUY
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}