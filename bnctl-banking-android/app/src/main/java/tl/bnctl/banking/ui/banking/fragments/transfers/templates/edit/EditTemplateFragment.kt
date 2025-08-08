package tl.bnctl.banking.ui.banking.fragments.transfers.templates.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.templates.TemplatesDataSource
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.templates.model.TemplateTransferType
import tl.bnctl.banking.databinding.FragmentTemplateEditBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.details.adapter.CurrencyDropDownAdapter
import tl.bnctl.banking.ui.banking.fragments.transfers.templates.CreateEditTemplateViewModel
import tl.bnctl.banking.ui.banking.fragments.transfers.templates.TemplatesViewModelFactory
import tl.bnctl.banking.ui.banking.fragments.transfers.templates.adapter.TransferTypeDropDownAdapter
import tl.bnctl.banking.ui.banking.fragments.transfers.templates.details.TemplateDetailsFragment
import tl.bnctl.banking.ui.utils.DialogFactory

class EditTemplateFragment : BaseFragment() {

    private var _binding: FragmentTemplateEditBinding? = null
    private val binding get() = _binding!!

    private val editTemplateViewModel: CreateEditTemplateViewModel by viewModels { TemplatesViewModelFactory() }

    private lateinit var loadingDialog: AlertDialog

    private lateinit var template: Template


    companion object {
        val TAG: String = EditTemplateFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplateEditBinding.inflate(inflater, container, false)

        // Hardcoding internal transfers here. The available currencies will be changed when the transfer type changes
        editTemplateViewModel.setAvailableCurrencies(R.array.internal_transfer_currencies)
        editTemplateViewModel.disableCurrenciesOption()
        editTemplateViewModel.fetchBanks()

        template = requireArguments().get("template") as Template
        setupUI()
        populateTemplateDetails(template)

        return binding.root
    }

    private fun setupUI() {
        setupToolbar()
        setupTransferTypeField()
        setupPayeeNameField()
        setupPayeeEmailField()
        setupAccountNumberField()
        setupCurrencyField()
        setupBankField()
        setupNextButton()

        loadingDialog = DialogFactory.createLoadingDialog(requireContext())

        editTemplateViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
        editTemplateViewModel.result.observe(viewLifecycleOwner) {
            val success = (it is Result.Success)

            val messageResourceId = if (success) {
                R.string.template_edit_success
            } else {
                R.string.template_edit_error
            }

            val dialog = DialogFactory.createNonCancelableInformativeDialog(
                requireContext(),
                null,
                messageResourceId,
                R.string.common_ok,
                if (success) { // If successfully added, go back to the payees screen. Otherwise, just dismiss the dialog
                    onEndDialogConfirmListener
                } else {
                    null
                }
            )
            dialog.show()
        }
    }

    private fun setupBankField() {
        editTemplateViewModel.banksResult.observe(viewLifecycleOwner) {
            if (it is Result.Success) {
                // Setting first available bank as selected
                // TODO: Select banks depending on IBAN. This will be done when national and international transfers are implemented
                if (it.data.isNotEmpty()) {
                    editTemplateViewModel.setBank(it.data[0])
                }
            } else {
                DialogFactory.createNonCancelableInformativeDialog(
                    requireContext(),
                    null,
                    R.string.error_fetching_banks,
                    R.string.common_ok,
                    onEndDialogConfirmListener
                ).show()
            }
        }
    }

    private val onEndDialogConfirmListener: () -> Any = {
        if (editTemplateViewModel.result.value is Result.Success) {
            setFragmentResult(
                TemplateDetailsFragment::class.simpleName.toString(), bundleOf(
                    "template" to (editTemplateViewModel.result.value as Result.Success).data
                )
            )
        }
        findNavController().popBackStack()
    }

    private fun setupNextButton() {
        binding.newTemplateNextButton.setOnClickListener {
            onConfirmPressed()
        }
    }

    private fun setupTransferTypeField() {

        val transferTypes = BuildConfig.SUPPORTED_TEMPLATE_TRANSFER_TYPES
        val enabledTransferTypes = mutableListOf<TemplateTransferType>()
        // Build array of available transfer types
        for (transferType in transferTypes) {
            val transferTypeToTemplateTypeId = TemplatesDataSource.TRANSFER_TYPE_TO_TEMPLATE_TYPE_ID
            enabledTransferTypes.add(
                TemplateTransferType(
                    transferTypeToTemplateTypeId[transferType]!!, getTemplateTypeLabel(transferType)
                )
            )
        }
        // Select first transfer type
        if (enabledTransferTypes.size > 0) {
            editTemplateViewModel.setAccountTypeId(enabledTransferTypes[0])
        }

        val adapter = TransferTypeDropDownAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            enabledTransferTypes
        )
        binding.newTemplateTransferType.adapter = adapter

        binding.newTemplateTransferType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    if (binding.newTemplateTransferType.selectedItemPosition > 0) {
                        editTemplateViewModel.setAccountTypeId(binding.newTemplateTransferType.selectedItem as TemplateTransferType)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        // TODO: Update UI according to transfer type - update available currencies and show/hide needed fields
        editTemplateViewModel.accountTypeId.observe(viewLifecycleOwner) {
            // val newTransferType = TransferType.of(it)
            // newTemplateViewModel.setAvailableCurrencies(R.array.cash_withdrawal_currencies)
        }

        editTemplateViewModel.accountTypeIdError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newTemplateTransferTypeWrapper.error = resources.getString(it)
            } else {
                binding.newTemplateTransferTypeWrapper.error = null
            }
        }
    }

    private fun setupPayeeNameField() {
        binding.newTemplatePayeeName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                editTemplateViewModel.setPayeeName(text.toString())
            }
        })
        editTemplateViewModel.payeeNameError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newTemplatePayeeName.error = resources.getString(it)
            } else {
                binding.newTemplatePayeeName.error = null
            }
        }
    }

    private fun setupPayeeEmailField() {
        binding.newTemplatePayeeEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                editTemplateViewModel.setPayeeEmail(text.toString())
            }
        })
        editTemplateViewModel.payeeEmailError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newTemplatePayeeEmail.error = resources.getString(it)
            } else {
                binding.newTemplatePayeeEmail.error = null
            }
        }
    }

    private fun setupAccountNumberField() {
        binding.newTemplateAccountNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                editTemplateViewModel.setAccountNumber(text.toString())
            }
        })
        editTemplateViewModel.accountNumberError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newTemplateAccountNumber.error = resources.getString(it)
            } else {
                binding.newTemplateAccountNumber.error = null
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCurrencyField() {
        val currencyDropDown: AutoCompleteTextView = binding.newTemplateCurrency
        val transferDetailsCurrencyLayout = binding.newTemplateCurrencyLayout

        currencyDropDown.isEnabled = editTemplateViewModel.enableCurrencies.value == true
        currencyDropDown.isClickable = editTemplateViewModel.enableCurrencies.value == true

        val currencies =
            resources.getStringArray(editTemplateViewModel.availableCurrenciesResourceId.value!!)
        val adapter = CurrencyDropDownAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            currencies
        )
        currencyDropDown.setAdapter(adapter)
        editTemplateViewModel.setCurrency(currencies[0])

        if (currencyDropDown.isEnabled && currencyDropDown.isClickable) {
            currencyDropDown.onItemClickListener =
                AdapterView.OnItemClickListener { parent, _, position, _ ->
                    if (parent != null) {
                        val currency = parent.getItemAtPosition(position) as String
                        editTemplateViewModel.setCurrency(currency)
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
            if (editTemplateViewModel.currency.value == null) {
                editTemplateViewModel.setCurrency(currencies[0])
            }
            editTemplateViewModel.currency.observe(viewLifecycleOwner) {
                currencyDropDown.setText(currencies[currencies.indexOf(it)], false)
            }
        } else {
            transferDetailsCurrencyLayout.isClickable = false
            transferDetailsCurrencyLayout.endIconMode = TextInputLayout.END_ICON_NONE
            editTemplateViewModel.currency.observe(viewLifecycleOwner) {
                currencyDropDown.setText(currencies[currencies.indexOf(it)], false)
            }
        }

        editTemplateViewModel.currencyError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newTemplateCurrencyLayout.error = resources.getString(it)
            } else {
                binding.newTemplateCurrencyLayout.error = null
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onConfirmPressed() {
        binding.newTemplateNextButton.isEnabled = false
        if (formIsValid()) {
            editTemplateViewModel.sendEditRequest(template.payeeId, requireContext())
        } else {
            binding.newTemplateNextButton.isEnabled = true
        }
    }

    private fun formIsValid(): Boolean {
        with(editTemplateViewModel) {
            validateAccountTypeId()
            validatePayeeName()
            validatePayeeEmail()
            validateAccountNumber()
            validateCurrency()

            return accountTypeIdError.value == null
                    && payeeNameError.value == null
                    && payeeEmailError.value == null
                    && accountNumberError.value == null
                    && currencyError.value == null
        }
    }

    private fun populateTemplateDetails(template: Template) {
        with(binding) {
            newTemplatePayeeName.setText(template.name)

            if (template.email != null) {
                newTemplatePayeeEmail.setText(template.email!!)
            }
            newTemplateAccountNumber.setText(template.accountNumber)
            editTemplateViewModel.setCurrency(template.currency)
        }

    }

    private fun getTemplateTypeLabel(transferType: String?): String {
        val stringId = resources.getIdentifier(
            "transfer_type_$transferType", "string", requireActivity().packageName
        )
        return getString(stringId)
    }
}