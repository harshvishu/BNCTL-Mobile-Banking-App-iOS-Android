package tl.bnctl.banking.data.exchange_rates

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.exchange_rates.model.ExchangeRate

class ExchangeRatesRepository(private val dataSource: ExchangeRatesDataSource) {

    var exchangeRates: List<ExchangeRate>? = null

    suspend fun exchangeRatesFetch(date: String): Result<List<ExchangeRate>> {
        val result = dataSource.exchangeRatesFetch(date)
        if (result is Result.Success) {
            exchangeRates = result.data
        }
        return result
    }
}
