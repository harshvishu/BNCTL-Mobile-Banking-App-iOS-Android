package tl.bnctl.banking.ui.banking.fragments.information.exchangeRates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.exchange_rates.ExchangeRatesDataSource
import tl.bnctl.banking.data.exchange_rates.ExchangeRatesRepository
import tl.bnctl.banking.data.exchange_rates.ExchangeRatesService

class ExchangeRatesViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExchangeRatesViewModel::class.java)) {
            return ExchangeRatesViewModel(
                exchangeRatesRepository = ExchangeRatesRepository(
                    dataSource = ExchangeRatesDataSource(getExchangeRateService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getExchangeRateService(): ExchangeRatesService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(ExchangeRatesService::class.java)
    }
}
