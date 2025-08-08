//
//  ExchangeRate.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 13.12.21.
//

import Foundation

struct ExchangeRateModel: Identifiable, Hashable {

    var currencyCode: String
    var currencyName: String
    var currencyUnits: Int
    var date: String
    var cashlessBuyRate: Double?
    var cashBuyRate: Double?
    var cashlessSellRate: Double?
    var cashSellRate: Double?
    var fixedRate: Double?
    
    var id: String {
        self.currencyCode
    }
    
    static func from(rawExchangeRates: [ExchangeRateService.ExchangeRate]) -> [ExchangeRateModel] {
        var exchangeRatesMap = [String: ExchangeRateModel]()
        rawExchangeRates.forEach({ rawExchangeRate in
            var exchangeRate = exchangeRatesMap[rawExchangeRate.id] ?? ExchangeRateModel(
                currencyCode: rawExchangeRate.currencyCode,
                currencyName: rawExchangeRate.currencyName,
                currencyUnits: rawExchangeRate.currencyUnits,
                date: rawExchangeRate.date,
                fixedRate: rawExchangeRate.fixedRate
            )
            switch rawExchangeRate.rateType {
            case .transfer:
                exchangeRate.cashlessBuyRate = rawExchangeRate.buyRate
                exchangeRate.cashlessSellRate = rawExchangeRate.sellRate
            case .cash:
                exchangeRate.cashBuyRate = rawExchangeRate.buyRate
                exchangeRate.cashSellRate = rawExchangeRate.sellRate
            }
            exchangeRatesMap[rawExchangeRate.id] = exchangeRate
        })
        return exchangeRatesMap.map {(_, value: ExchangeRateModel) in return value}
    }
    
    static var listPreview: [ExchangeRateModel] {
        let rawData = ExchangeRateService.ExchangeRate.listPreview
        let data = from(rawExchangeRates: rawData)
        return data
    }
    
    enum RateType: String, Codable {
        case cash
        case transfer
    }
}
