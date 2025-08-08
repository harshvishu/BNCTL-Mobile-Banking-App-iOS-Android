package tl.bnctl.banking.ui.banking.fragments.cards.new_debit_card

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.branches.BranchRepository
import tl.bnctl.banking.data.branches.model.Branch
import tl.bnctl.banking.data.cards.CardsDataSource
import tl.bnctl.banking.data.cards.CardsRepository
import tl.bnctl.banking.data.cards.model.CardProduct
import tl.bnctl.banking.data.cards.model.NewDebitCardRequest
import tl.bnctl.banking.data.cards.model.NewDebitCardResult

class NewDebitCardViewModel(
    private val cardsRepository: CardsRepository,
    private val cardsDataSource: CardsDataSource,
    private val branchRepository: BranchRepository
) : ViewModel() {
    private val _request = MutableLiveData<NewDebitCardRequest>().apply {
        NewDebitCardRequest(
            "",
            "",
            "",
            "",
            "",
            false,
            false,
            ""
        )
    }
    val request: LiveData<NewDebitCardRequest> = _request;

    private val _result = MutableLiveData<Result<NewDebitCardResult>>().apply {
        NewDebitCardResult(false)
    }
    val result: LiveData<Result<NewDebitCardResult>> = _result

    private val _cardProducts = MutableLiveData<Result<List<CardProduct>>>().apply {}
    val cardProducts: LiveData<Result<List<CardProduct>>> = _cardProducts

    private val _branches = MutableLiveData<Result<List<Branch>>>().apply {}
    val branches: LiveData<Result<List<Branch>>> = _branches

    private val _agreed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    private val agreed: LiveData<Boolean> = _agreed

    // Errors
    private val _accountError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    private val _cardTypeError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    private val _embossNameError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    private val _locationError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    private val _emailError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    private val _agreementError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }

    // Immutable error LiveData
    val accountError: LiveData<Int?> = _accountError
    val cardTypeError: LiveData<Int?> = _cardTypeError
    val embossNameError: LiveData<Int?> = _embossNameError
    val locationError: LiveData<Int?> = _locationError
    val agreementError: LiveData<Int?> = _agreementError
    val emailError: LiveData<Int?> = _emailError

    // Loading checks
    private val _loadingCardProducts: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val loadingCardProducts: LiveData<Boolean> = _loadingCardProducts

    private val _loadingBranches: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val loadingBranches: LiveData<Boolean> = _loadingBranches

    private val _loadingAccounts: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val loadingAccounts: LiveData<Boolean> = _loadingAccounts

    fun fetchCardProducts() {
        _loadingCardProducts.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = cardsRepository.fetchCardProducts()
            _cardProducts.postValue(result)
            _loadingCardProducts.postValue(false)
        }
    }

    fun fetchBranches() {
        _loadingBranches.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = branchRepository.fetchBranches()
            _branches.postValue(result)
            _loadingBranches.postValue(false)
        }
    }

    fun sendCardRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = cardsDataSource.newDebitCard(request.value!!)
            _result.postValue(result)
        }
    }

    fun setAccount(accountIban: String) {
        _request.value?.accountIban = accountIban
        _request.postValue(_request.value)
    }

    fun setCardProductType(cardProduct: CardProduct) {
        with(_request.value!!) {
            cardProductCode = cardProduct.id
            cardProductName = cardProduct.name
        }
        _request.postValue(_request.value)
    }

    fun setEmbossName(embossName: String) {
        _request.value?.embossName = embossName
        _request.postValue(_request.value)
    }

    fun setPickupLocation(branch: Branch) {
        _request.value?.locationId = branch.id
        _request.postValue(_request.value)
    }

    fun setStatementsOnDemand(checked: Boolean) {
        _request.value?.statementOnDemand = checked
        _request.postValue(_request.value)
    }

    fun setStatementsOnEmail(checked: Boolean) {
        _request.value?.statementOnEmail = checked
        _request.postValue(_request.value)
    }

    fun setStatementsEmail(email: String) {
        _request.value?.statementEmail = email
        _request.postValue(_request.value)
    }

    fun setAgreed(agreed: Boolean) {
        _agreed.postValue(agreed)
    }

    private fun raiseAccountError(errorMessage: Int) {
        _accountError.value = errorMessage
    }

    private fun clearAccountError() {
        _accountError.value = null
    }

    private fun raiseCardTypeError(errorMessage: Int) {
        _cardTypeError.value = errorMessage
    }

    private fun clearCardTypeError() {
        _cardTypeError.value = null
    }

    private fun raiseEmbossNameError(errorMessage: Int) {
        _embossNameError.value = errorMessage
    }

    private fun clearEmbossNameError() {
        _embossNameError.value = null
    }

    private fun raiseLocationError(errorMessage: Int) {
        _locationError.value = errorMessage
    }

    private fun clearLocationError() {
        _locationError.value = null
    }

    private fun raiseEmailError(errorMessage: Int) {
        _emailError.value = errorMessage
    }

    private fun clearEmailError() {
        _emailError.value = null
    }

    private fun raiseAgreementError(errorMessage: Int) {
        _agreementError.value = errorMessage
    }

    private fun clearAgreementError() {
        _agreementError.value = null
    }

    fun validateAccount() {
        if (request.value!!.embossName.isBlank()) {
            raiseAccountError(R.string.new_debit_card_error_select_account)
        } else {
            clearAccountError()
        }
    }

    fun validateCardType() {
        if (request.value!!.cardProductCode.isBlank()) {
            raiseCardTypeError(R.string.new_debit_card_error_select_card_type)
        } else {
            clearCardTypeError()
        }
    }

    fun validateEmbossName() {
        if (request.value!!.embossName.isBlank()) {
            raiseEmbossNameError(R.string.new_debit_card_error_enter_name)
        } else {
            clearEmbossNameError()
        }
    }

    fun validatePickupLocation() {
        if (request.value!!.locationId.isBlank()) {
            raiseLocationError(R.string.new_debit_card_error_select_location)
        } else {
            clearLocationError()
        }
    }

    fun validateAgreed() {
        if (!agreed.value!!) {
            raiseAgreementError(R.string.new_debit_card_error_please_agree_to_terms)
        } else {
            clearAgreementError()
        }
    }

    fun validateEmail() {
        val statementEmail = request.value!!.statementEmail
        if (statementEmail.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(statementEmail)
                .matches()
        ) {
            raiseEmailError(R.string.new_debit_card_error_invalid_email);
        } else {
            clearEmailError()
        }
    }

    fun setAccountLoading(isLoading: Boolean) {
        _loadingAccounts.postValue(isLoading)
    }


    init {
        _request.postValue(
            NewDebitCardRequest(
                "",
                "",
                "",
                "",
                "",
                false,
                false,
                ""
            )
        )
    }
}