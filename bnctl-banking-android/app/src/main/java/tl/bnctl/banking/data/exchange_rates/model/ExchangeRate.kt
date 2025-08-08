package tl.bnctl.banking.data.exchange_rates.model

data class ExchangeRate(
    var currencyCode: String,
    var currencyName: String,
    var currencyUnits: Int,
    var date: String,
    var cashlessBuyRate: String,
    var cashBuyRate: String,
    var cashlessSellRate: String,
    var cashSellRate: String,
    var fixedRate: String
)
