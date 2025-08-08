package tl.bnctl.banking.ui.banking.fragments.information.exchangeRates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.exchange_rates.ExchangeRatesRepository
import tl.bnctl.banking.data.exchange_rates.model.ExchangeRate
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.getInstance

class ExchangeRatesViewModel(
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {

    private var _exchangeRates = MutableLiveData<Result<List<ExchangeRate>>?>()
    val exchangeRates: LiveData<Result<List<ExchangeRate>>?> = _exchangeRates

    private var _date = MutableLiveData<Date>()
    val date: LiveData<Date> = _date

    init {
        _date.value = getInstance().time
    }

    fun getExchangeRates() {
        viewModelScope.launch(Dispatchers.IO) {
            val result =
                exchangeRatesRepository.exchangeRatesFetch(convertToBackendFormat(_date.value!!))
            _exchangeRates.postValue(result)
        }
    }

    fun updateDate(date: Date) {
        _date.postValue(date)
    }

    private fun convertToBackendFormat(date: Date): String {
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

}
