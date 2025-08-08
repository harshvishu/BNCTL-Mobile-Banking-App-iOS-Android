package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.destination.newpayee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.databinding.FragmentSectionPayeeNewBinding
import tl.bnctl.banking.ui.BaseFragment

class NewPayeeFragment : BaseFragment() {

    private var _binding: FragmentSectionPayeeNewBinding? = null
    private val binding get() = _binding!!

    private val newPayeeViewModel: NewPayeeViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSectionPayeeNewBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        setUpBeneficiary()
        setUpEmail()
        newPayeeViewModel.transferType.observe(viewLifecycleOwner) {
            updatePayeeTransferType(it)
            setUpDestinationAccount(it)
        }
        newPayeeViewModel.addToPayeeListVisible.observe(viewLifecycleOwner) {
            binding.newPayeeAddToPayeeListCheckbox.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        binding.newPayeeAddToPayeeListCheckbox.setOnCheckedChangeListener { compoundButton, checked ->
            newPayeeViewModel.setAddToPayeeList(checked)
        }
    }

    private fun updatePayeeTransferType(transferType: TransferType) {
        with(binding) {
            when (transferType) {
                TransferType.INTRABANK -> {
                    newPayeeBankLayout.visibility = View.GONE
                    newPayeeSwiftCodeLayout.visibility = View.GONE
                    newPayeeBeneficiaryNameLayout.visibility = View.VISIBLE
                    newPayeeEmailLayout.visibility = View.VISIBLE
                }
                TransferType.INTERBANK -> {
                    newPayeeBankLayout.visibility = View.VISIBLE
                    newPayeeSwiftCodeLayout.visibility = View.VISIBLE
                    newPayeeBeneficiaryNameLayout.visibility = View.VISIBLE
                    newPayeeEmailLayout.visibility = View.VISIBLE
                }
                else -> {
                    newPayeeBankLayout.visibility = View.GONE
                    newPayeeSwiftCodeLayout.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Setup destination field according to the transfer type.
     */
    private fun setUpDestinationAccount(transferType: TransferType) {
        val destinationAccountLayout: TextInputLayout = binding.newPayeeDestinationAccountLayout
        val destinationAccount: TextInputEditText = binding.newPayeeDestinationAccount
        destinationAccount.setText(newPayeeViewModel.destinationAccount.value)
        // Use the changed information from view model so parent fragments can raise errors in case of custom validations
        newPayeeViewModel.destinationAccountError.observe(viewLifecycleOwner) {
            if (it != null) {
                destinationAccountLayout.error = resources.getString(it)
            } else {
                destinationAccountLayout.error = null
                destinationAccountLayout.isErrorEnabled = false
            }
        }

        // Trigger validation when the user changes the focus from the edit text
        destinationAccount.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newInput = v as TextInputEditText
                newInput.setText(newInput.text.toString().uppercase())
                newPayeeViewModel.setDestinationAccount(newInput.text.toString())
                newPayeeViewModel.validateDestinationAccountNumber()
            }
        }
    }

    private fun setUpBeneficiary() {
        val beneficiaryLayout: TextInputLayout = binding.newPayeeBeneficiaryNameLayout
        val beneficiary: TextInputEditText = binding.newPayeeBeneficiaryName
        beneficiary.setText(newPayeeViewModel.beneficiaryName.value)
        newPayeeViewModel.beneficiaryNameError.observe(viewLifecycleOwner) {
            if (it != null) {
                beneficiaryLayout.error = resources.getString(it)
            } else {
                beneficiaryLayout.error = null
                beneficiaryLayout.isErrorEnabled = false
            }
        }
        beneficiary.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newInput = (v as TextInputEditText)
                newPayeeViewModel.setBeneficiary(newInput.text.toString())
                newPayeeViewModel.validateBeneficiary()
            }
        }
    }


    private fun setUpEmail() {
        val emailEditTextLayout: TextInputLayout = binding.newPayeeEmailLayout
        val emailEditText: TextInputEditText = binding.newPayeeEmail
        newPayeeViewModel.emailError.observe(viewLifecycleOwner) {
            if (it != null) {
                emailEditTextLayout.error = resources.getString(it)
            } else {
                emailEditTextLayout.error = null
                emailEditTextLayout.isErrorEnabled = false
            }
        }
        emailEditText.setText(newPayeeViewModel.email.value)
        emailEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val newInput = (v as TextInputEditText)
                newPayeeViewModel.setEmail(newInput.text.toString())
                newPayeeViewModel.validateEmail()
            }
        }
    }
}