package tl.bnctl.banking.ui.banking.fragments.services.cashWithdrawal

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.branches.model.Branch
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.databinding.FragmentCashWithdrawalBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.custom.amount.field.AmountTextWatcher
import tl.bnctl.banking.ui.banking.fragments.services.cashWithdrawal.adapter.BranchInfoAdapter
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.details.adapter.CurrencyDropDownAdapter
import tl.bnctl.banking.ui.utils.DateUtils
import tl.bnctl.banking.ui.utils.NumberUtils
import java.math.BigDecimal
import java.util.*

class CashWithdrawalFragment : BaseFragment() {

    private var _binding: FragmentCashWithdrawalBinding? = null
    private val binding get() = _binding!!

    private val cashWithdrawalViewModel: CashWithdrawalViewModel by viewModels { CashWithdrawalViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCashWithdrawalBinding.inflate(inflater, container, false)
        setUpAmountField()
        setUpCurrencyDropDownField()
        setUpLocationDropDownField()
        setUpDateField()
        setUpDescriptionField()
        setUpToolbar()
        setUpNextButton()
        cashWithdrawalViewModel.cashWithdrawal.observe(viewLifecycleOwner) {
            binding.cashWithdrawalFormButtonNext.isEnabled = true
            if (it is Result.Success) {
                findNavController().navigate(
                    CashWithdrawalFragmentDirections.actionNavFragmentCashWithdrawalToNavFragmentCashWithdrawalEnd(
                        TransferEnd(
                            true,
                            requireContext().resources.getString(R.string.common_label_success)
                        ), null, false
                    )
                )
            } else {
                handleResultError(it as Result.Error, R.string.error_generic)
            }
        }
        cashWithdrawalViewModel.fetchBranches()
        return binding.root
    }

    private fun setUpNextButton() {
        binding.cashWithdrawalFormButtonNext.setOnClickListener {
            binding.cashWithdrawalFormButtonNext.isEnabled = false
            val focusTarget = binding.cashWithdrawalForm
            focusTarget.requestFocus()
            focusTarget.clearFocus()
            // TODO: check for other approach on focus clear with keyboard close
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(
                    focusTarget.windowToken,
                    0
                )
            if (validateForm()) {
                cashWithdrawalViewModel.requestWithdrawal()
            } else {
                binding.cashWithdrawalFormButtonNext.isEnabled = true
            }
        }
    }

    private fun validateForm(): Boolean {
        cashWithdrawalViewModel.validateLocation()
        cashWithdrawalViewModel.validateAmount()
        cashWithdrawalViewModel.validateDate()
        return cashWithdrawalViewModel.amountError.value == null
                && cashWithdrawalViewModel.dateError.value == null
                && cashWithdrawalViewModel.locationError.value == null
    }

    private fun setUpToolbar() {
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpAmountField() {
        val amountEditField: TextInputEditText = binding.cashWithdrawalFormFieldAmount
        val amountLayout = binding.cashWithdrawalFormFieldAmountLayout
        amountEditField.addTextChangedListener(AmountTextWatcher(amountEditField, requireContext()))
        cashWithdrawalViewModel.amountError.observe(viewLifecycleOwner) {
            if (it != null) {
                amountLayout.error = resources.getString(it)
            } else {
                amountLayout.error = null
            }
        }

        val amount: BigDecimal? = cashWithdrawalViewModel.amount.value
        if (amount != null) {
            amountEditField.setText(
                NumberUtils.formatAmount(amount.toPlainString())
            )
        }
        amountEditField.setOnFocusChangeListener { v, hasFocus ->
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
                cashWithdrawalViewModel.setAmount(decimal)
                cashWithdrawalViewModel.validateAmount()
            }
        }
    }

    private fun setUpCurrencyDropDownField() {
        val currencyDropDown: AutoCompleteTextView = binding.cashWithdrawalFormFieldCurrency

        val currencies =
            resources.getStringArray(R.array.cash_withdrawal_currencies).asList()
        val arrayAdapter = CurrencyDropDownAdapter(
            requireContext(),
            R.layout.list_item_drop_down,
            currencies.toTypedArray()
        )

        currencyDropDown.setAdapter(
            arrayAdapter
        )

        binding.cashWithdrawalFormFieldCurrencyLayout.setEndIconOnClickListener {
            currencyDropDown.showDropDown()
        }

        currencyDropDown.setOnClickListener {
            currencyDropDown.showDropDown()
        }

        currencyDropDown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                if (parent != null) {
                    val currency = parent.getItemAtPosition(position) as String
                    cashWithdrawalViewModel.selectCurrency(currency)
                }
            }

        if (cashWithdrawalViewModel.currency.value == null) {
            cashWithdrawalViewModel.selectCurrency(currencies[0])
        }

        cashWithdrawalViewModel.currency.observe(viewLifecycleOwner) {
            currencyDropDown.setText(currencies[currencies.indexOf(it)], false)
        }

    }

    private fun setUpLocationDropDownField() {
        val locationDropDownField: AutoCompleteTextView = binding.cashWithdrawalFormFieldLocation
        val locationDropDownLayout = binding.cashWithdrawalFormFieldLocationLayout

        cashWithdrawalViewModel.branches.observe(viewLifecycleOwner) {
            if (it is Result.Success) {
                locationDropDownField.setAdapter(
                    BranchInfoAdapter(
                        requireContext(),
                        it.data.toTypedArray()
                    )
                )
            }
        }

        locationDropDownLayout.setEndIconOnClickListener {
            locationDropDownField.showDropDown()
        }

        locationDropDownField.setOnClickListener {
            locationDropDownField.showDropDown()
        }

        locationDropDownField.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                if (parent != null) {
                    val branch = parent.getItemAtPosition(position) as Branch
                    cashWithdrawalViewModel.setLocation(branch.id)
                }
            }

        cashWithdrawalViewModel.locationError.observe(viewLifecycleOwner) {
            if (it != null) {
                locationDropDownLayout.error = resources.getString(it)
            } else {
                locationDropDownLayout.error = null
            }
        }
    }

    private fun setUpDateField() {
        val dateSelectorField = binding.cashWithdrawalFormFieldDate
        val dateSelectorLayout = binding.cashWithdrawalFormFieldDateLayout

        dateSelectorField.setOnClickListener { showDatePickerDialog() }
        dateSelectorLayout.setEndIconOnClickListener { showDatePickerDialog() }
        cashWithdrawalViewModel.date.observe(viewLifecycleOwner) {
            if (it != null) {
                dateSelectorField.setText(
                    DateUtils.formatDate(
                        requireContext(),
                        it
                    )
                )
            }
        }

        cashWithdrawalViewModel.dateError.observe(viewLifecycleOwner) {
            if (it != null) {
                dateSelectorLayout.error = resources.getString(it)
            } else {
                dateSelectorLayout.error = null
            }
        }
    }

    private fun showDatePickerDialog() {
        binding.cashWithdrawalFormFieldDateLayout.isEnabled = false
        binding.cashWithdrawalFormFieldDate.isEnabled = false
        val date = Calendar.getInstance()
        val onDateSelectListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DATE, day)
            cashWithdrawalViewModel.setDate(selectedDate.time)
        }
        val picker = DatePickerDialog(
            requireActivity(),
            onDateSelectListener,
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DATE)
        )
        picker.setOnDismissListener {
            binding.cashWithdrawalFormFieldDateLayout.isEnabled = true
            binding.cashWithdrawalFormFieldDate.isEnabled = true
        }
        Calendar.getInstance().time.also { picker.datePicker.minDate = it.time }
        picker.show()
    }

    private fun setUpDescriptionField() {
        val descriptionEditText: TextInputEditText = binding.cashWithdrawalFormFieldDescription
        descriptionEditText.setText(cashWithdrawalViewModel.description.value)
        descriptionEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newInput = (v as TextInputEditText)
                cashWithdrawalViewModel.setDescription(newInput.text.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}