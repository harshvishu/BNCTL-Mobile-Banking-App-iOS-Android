package tl.bnctl.banking.ui.banking.fragments.cards.credit_card_statement

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.cards.CardsRepository
import tl.bnctl.banking.data.cards.model.CreditCardStatement
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsFilterData
import tl.bnctl.banking.ui.utils.DateUtils
import java.io.InputStream
import java.util.*

class CreditCardStatementViewModel(
    private val cardsRepository: CardsRepository
) : ViewModel() {
    private val _creditCardStatement = MutableLiveData<Result<List<CreditCardStatement>>>()
    private val _shouldFetchStatements = MutableLiveData(true)
    private val _statementFileDestination = MutableLiveData<Uri?>(null)
    private val _statementTempFileDestination = MutableLiveData<Uri?>(null)
    private val _chosenCardStatement = MutableLiveData<CreditCardStatement?>(null)
    private val _creditCardStatementFileInputStream = MutableLiveData<Result<InputStream>>()
    private val _downloadInProgress = MutableLiveData(false)
    private val _statementsFilter: MutableLiveData<StatementsFilterData> by lazy {
        MutableLiveData<StatementsFilterData>(
            StatementsFilterData(
                Date(), Date()
            )
        )
    }

    val creditCardStatement: LiveData<Result<List<CreditCardStatement>>> = _creditCardStatement
    val statementsFilter: LiveData<StatementsFilterData> = _statementsFilter
    val shouldFetchStatements: LiveData<Boolean> = _shouldFetchStatements
    val statementFileDestination: LiveData<Uri?> = _statementFileDestination
    val statementTempFileDestination: LiveData<Uri?> = _statementTempFileDestination
    val chosenCardStatement: LiveData<CreditCardStatement?> = _chosenCardStatement
    val creditCardStatementFileInputStream: LiveData<Result<InputStream>> =
        _creditCardStatementFileInputStream
    val downloadInProgress: LiveData<Boolean> = _downloadInProgress

    fun setFilter(filter: StatementsFilterData) {
        _statementsFilter.value = filter
    }

    fun raiseShouldFetchStatementsFlag() {
        _shouldFetchStatements.value = true
    }

    fun dropShouldFetchStatementsFlag() {
        _shouldFetchStatements.value = false
    }

    fun getCreditCardStatement(cardNumber: String, fromDate: Date, toDate: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            val statementsResult = cardsRepository.fetchCreditCardStatement(
                cardNumber,
                DateUtils.formatDateISO(fromDate),
                DateUtils.formatDateISO(toDate),
            )
            _creditCardStatement.postValue(statementsResult)
        }
    }

    fun downloadCreditCardStatement() {
        _downloadInProgress.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val statementsResult = cardsRepository.downloadCreditCardStatement(
                    _chosenCardStatement.value!!.fileName!!
                )
                _creditCardStatementFileInputStream.postValue(statementsResult)
            } finally {
                _downloadInProgress.postValue(false)
            }
        }
    }

    fun setStatementFileDestination(destination: Uri?) {
        _statementFileDestination.value = destination
    }

    fun setStatementTempFileDestination(destination: Uri?) {
        _statementTempFileDestination.value = destination
    }

    fun setChosenCardStatement(statement: CreditCardStatement) {
        _chosenCardStatement.value = statement
    }

}