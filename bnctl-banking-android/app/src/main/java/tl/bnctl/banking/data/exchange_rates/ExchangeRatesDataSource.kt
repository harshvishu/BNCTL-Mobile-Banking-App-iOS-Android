package tl.bnctl.banking.data.exchange_rates

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.exchange_rates.model.ExchangeRate
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class ExchangeRatesDataSource(
    private val exchangeRatesService: ExchangeRatesService
) {
    val gson: Gson = Gson()

    suspend fun exchangeRatesFetch(date: String): Result<List<ExchangeRate>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val exchangeRatesResult: JsonObject
            exchangeRatesService.fetchExchangeRates(accessToken, date)
                .also { exchangeRatesResult = it }
            val exchangeRates: JsonArray = exchangeRatesResult.get("result") as JsonArray
            val exchangeRatesMap = HashMap<String, ExchangeRate>()
            for (exchangeRateJsonObject in exchangeRates) {
                transformJsonToExchangeRateObj(
                    exchangeRateJsonObject.asJsonObject,
                    exchangeRatesMap
                );
            }
            return Result.Success(exchangeRatesMap.values.toMutableList())
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx, "exchangeRates")
        } catch (e: Exception) {
            return Result.createError(e)
        }
    }

    private fun transformJsonToExchangeRateObj(
        exchangeRateJsonObject: JsonObject,
        exchangeRatesMap: HashMap<String, ExchangeRate>
    ) {
        val currency = exchangeRateJsonObject.get("currencyCode").asString
        val currencyUnits = exchangeRateJsonObject.get("currencyUnits").asInt
        val rateType = exchangeRateJsonObject.get("rateType").asString
        val buyRate = exchangeRateJsonObject.get("buyRate").asString
        val sellRate = exchangeRateJsonObject.get("sellRate").asString
        val fixedRate = exchangeRateJsonObject.get("fixedRate").asString

        if (!exchangeRatesMap.containsKey(currency)) {
            var cashlessBuyRate = ""
            var cashBuyRate = ""
            var cashlessSellRate = ""
            var cashSellRate = ""
            when (rateType) {
                "transfer" -> {
                    cashlessBuyRate = buyRate
                    cashlessSellRate = sellRate
                }
                "cash" -> {
                    cashBuyRate = buyRate
                    cashSellRate = sellRate
                }
            }
            val date = exchangeRateJsonObject.get("date").asString
            val exchangeRate = ExchangeRate(
                currency,
                currency,
                currencyUnits,
                date,
                cashlessBuyRate,
                cashBuyRate,
                cashlessSellRate,
                cashSellRate,
                fixedRate
            )
            exchangeRatesMap[currency] = exchangeRate
        } else {
            val exchangeRate = exchangeRatesMap[currency]!!
            when (rateType) {
                "transfer" -> {
                    exchangeRate.cashlessBuyRate = buyRate
                    exchangeRate.cashlessSellRate = sellRate
                }
                "cash" -> {
                    exchangeRate.cashBuyRate = buyRate
                    exchangeRate.cashSellRate = sellRate
                }
            }
        }
    }
}
