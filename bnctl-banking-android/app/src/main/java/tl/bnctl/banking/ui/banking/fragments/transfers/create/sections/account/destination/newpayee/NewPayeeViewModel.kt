package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.destination.newpayee

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.templates.model.Bank
import tl.bnctl.banking.data.transfers.enums.TransferType

class NewPayeeViewModel : ViewModel() {

    private val _beneficiaryName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val beneficiaryName: LiveData<String> = _beneficiaryName

    private val _destinationAccount: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val destinationAccount: LiveData<String> = _destinationAccount

    // Let view model store the error messages so they can be updated easily from parent fragments
    private val _beneficiaryNameError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val beneficiaryNameError: LiveData<Int?> = _beneficiaryNameError

    private val _destinationAccountError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val destinationAccountError: LiveData<Int?> = _destinationAccountError

    private val _email: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val email: LiveData<String> = _email

    private val _emailError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val emailError: LiveData<Int?> = _emailError

    private val _addToPayeeList: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val addToPayeeList: LiveData<Boolean> = _addToPayeeList

    private val _addToPayeeListVisible: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(
            false
        )
    }
    val addToPayeeListVisible: LiveData<Boolean> = _addToPayeeListVisible

    private val _bank: MutableLiveData<Bank> by lazy { MutableLiveData<Bank>() }
    val bank: LiveData<Bank> = _bank

    /**
     * Set transfer type so the validations can be done properly depending on it
     */
    private val _transferType: MutableLiveData<TransferType> by lazy { MutableLiveData<TransferType>() }
    val transferType: LiveData<TransferType> = _transferType

    var validateIbanIfItIsForCurrentBank = false

    fun validateBeneficiary() {
        if (isBeneficiaryValid()) {
            clearBeneficiaryError()
        } else {
            raiseBeneficiaryError(R.string.error_transfer_validation_required_beneficiary)
        }
    }

    fun setTransferType(transferType: TransferType) {
        _transferType.postValue(transferType)
    }

    fun validateDestinationAccountNumber() {
        // Validate IBAN only if the user is supposed to enter IBAN
        if (transferType.value == TransferType.INTERBANK ||
            transferType.value == TransferType.INTERNATIONAL
        ) {
            validateIBANField()
        } else {
            if (_destinationAccount.value.isNullOrBlank()
            ) {
                raiseDestinationAccountNumberError(R.string.common_error_field_required)
                return
            } else {
                clearDestinationAccountNumberError()
            }
            if (!_destinationAccount.value!!.matches(Regex("[0-9+]{8,36}"))) {
                raiseDestinationAccountNumberError(R.string.error_transfer_validation_destination_account_invalid)
            } else {
                clearDestinationAccountNumberError()
            }
        }

    }

    private fun validateIBANField() {
        if (isIbanValid()) {
            if (validateIbanIfItIsForCurrentBank) {
                if (_destinationAccount.value?.substring(
                        4,
                        8
                    ) != BuildConfig.BANK_SWIFT_CODE.substring(0, 4)
                ) {
                    raiseDestinationAccountNumberError(R.string.error_transfer_validation_iban_not_from_current_bank)
                    return
                }
            }
            clearDestinationAccountNumberError()
        } else {
            raiseDestinationAccountNumberError(R.string.error_transfer_validation_invalid_iban)
        }
    }

    fun validateDestinationAccountNumber(additionalValidation: (input: String) -> Boolean) {
        validateDestinationAccountNumber()
        if (_destinationAccountError.value == null) {
            if (additionalValidation(destinationAccount.value.toString())) {
                clearDestinationAccountNumberError()
            } else {
                raiseDestinationAccountNumberError(R.string.error_transfer_validation_invalid_iban)
            }
        }
    }

    fun validateDestinationAccountNumber(
        errorMessage: Int,
        additionalValidation: (input: String) -> Boolean
    ) {
        validateDestinationAccountNumber()
        if (_destinationAccountError.value == null) {
            if (additionalValidation(destinationAccount.value.toString())) {
                clearDestinationAccountNumberError()
            } else {
                raiseDestinationAccountNumberError(errorMessage)
            }
        }
    }

    fun setBeneficiary(name: String) {
        if (name != _beneficiaryName.value) {
            showAddToPayeeListCheckbox()
        }
        _beneficiaryName.value = name
    }

    fun setDestinationAccount(destinationAccount: String) {
        if (destinationAccount != _destinationAccount.value) {
            showAddToPayeeListCheckbox()
        }
        _destinationAccount.value = destinationAccount.trim()
    }

    private fun isIbanValid(): Boolean {
        return ibanValidation()
    }

    private fun isBeneficiaryValid(additionalValidation: (input: String) -> Boolean): Boolean {
        return isBeneficiaryValid() && additionalValidation(
            beneficiaryName.value.toString()
        )
    }

    private fun raiseBeneficiaryError(name: Int) {
        _beneficiaryNameError.value = name
    }

    private fun raiseDestinationAccountNumberError(stringId: Int) {
        _destinationAccountError.value = stringId
    }

    private fun clearBeneficiaryError() {
        _beneficiaryNameError.value = null
    }

    private fun clearDestinationAccountNumberError() {
        _destinationAccountError.value = null
    }

    private fun isBeneficiaryValid(): Boolean {
        return !beneficiaryName.value.isNullOrBlank()
    }

    private fun ibanValidation(): Boolean {
        val symbols = destinationAccount.value.toString().trim()
        if (symbols.isBlank()) {
            return false
        }
        if (symbols.length < 15 || symbols.length > 34
            || !"^[0-9A-Z]*\$".toRegex().matches(symbols)
        ) {
            return false
        }
        val swapped = symbols.substring(4) + symbols.substring(0, 4)
        return swapped.toCharArray()
            .map { it.code }
            .fold(0) { previousMod: Int, _char: Int ->
                val value = Integer.parseInt(_char.toChar().toString(), 36)
                val factor = if (value < 10) 10 else 100
                (factor * previousMod + value) % 97
            } == 1
    }

    fun validateEmail() {
        if (isEmailValid()) {
            clearEmailError()
        } else {
            raiseEmailError(R.string.error_transfer_validation_email_invalid)
        }
    }

    fun setEmail(email: String) {
        if (_email.value != email) {
            showAddToPayeeListCheckbox()
        }
        _email.value = email
    }

    private fun isEmailValid(): Boolean {
        return if (!email.value.isNullOrBlank()) {
            Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()
        } else {
            true
        }
    }

    private fun raiseEmailError(errorMessage: Int) {
        _emailError.value = errorMessage
    }

    private fun clearEmailError() {
        _emailError.value = null
    }

    fun setAddToPayeeList(addToPayeeList: Boolean) {
        _addToPayeeList.value = addToPayeeList
    }

    fun setBank(bank: Bank) {
        _bank.value = bank
    }

    fun showAddToPayeeListCheckbox() {
        _addToPayeeListVisible.postValue(true)
    }

    fun hideAddToPayeeListCheckbox() {
        _addToPayeeListVisible.postValue(false)
    }
}
