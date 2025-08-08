//
//  ExchangeRateService.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 13.12.21.
//

import Foundation

class ExchangeRateService {
    
    // API Call
    
    func fetchExchangeRates(
        forDate: Date?,
        completionHandler: @escaping (
        _ error: Error?,
        _ exchangeRatesResponse: [ExchangeRateModel]?
        ) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .exchangeRates,
            method: .get,
            parameters: emptyBody,
            queryItems: forDate == nil ? nil : [
                "date": forDate!.formatTo(format: "dd/MM/yyyy")
            ]
        ) { _, result, error in
            if let error = error {
                // Networking and Server errors
                completionHandler(error, nil)
            } else {
                // Try to parse the Result
                if let result = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let exchangeRateResult = try decoder.decode(ExchangeRateResult.self, from: result)
                        let exchangeRates = ExchangeRateModel.from(rawExchangeRates: exchangeRateResult.result)
                        completionHandler(nil, exchangeRates)
                    } catch {
                        // Parsing error
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(ResultError.parseError, nil)
                    }
                } else {
                    // No result to parse
                    completionHandler(ResultError.noResultError, nil)
                }
            }
        }
    }
    
    // Response Structures
    
    struct ExchangeRateResult: Codable {
        let result: [ExchangeRate]
    }
    
    struct ExchangeRate: Codable, Identifiable, Hashable {

        let currencyCode: String
        let rateType: RateType
        let currencyName: String
        let currencyUnits: Int
        let date: String
        let buyRate: Double
        let sellRate: Double
        let fixedRate: Double

        var id: String {
            self.currencyCode
        }
        
        static var preview: ExchangeRate? {
            let rawData = Data("""
                {
                    "currencyCode": "CHF",
                    "rateType": "cash",
                    "currencyName": "CHF",
                    "currencyUnits": 1,
                    "date": "10/12/2021",
                    "buyRate": 1.8452,
                    "sellRate": 1.9002,
                    "fixedRate": 1.87628
                }
            """.utf8)
            guard let parsedData = try? JSONDecoder().decode(ExchangeRate.self, from: rawData) else {
                return nil
            }
            return parsedData
        }
        
        static var listPreview: [ExchangeRate] {
            let rawData = Data("""
                [
                    {
                        "currencyCode": "CHF",
                        "rateType": "cash",
                        "currencyName": "CHF",
                        "date": "22/07/2022",
                        "currencyUnits": 1,
                        "buyRate": 1.9551,
                        "sellRate": 2.0101,
                        "fixedRate": 1.98925
                    },
                    {
                        "currencyCode": "CHF",
                        "rateType": "transfer",
                        "currencyName": "CHF",
                        "date": "22/07/2022",
                        "currencyUnits": 1,
                        "buyRate": 1.9551,
                        "sellRate": 2.0101,
                        "fixedRate": 1.98925
                    },
                    {
                        "currencyCode": "DKK",
                        "rateType": "transfer",
                        "currencyName": "DKK",
                        "date": "22/07/2022",
                        "currencyUnits": 10,
                        "buyRate": 2.5975,
                        "sellRate": 2.6565,
                        "fixedRate": 2.62729
                    },
                    {
                        "currencyCode": "DKK",
                        "rateType": "cash",
                        "currencyName": "DKK",
                        "date": "22/07/2022",
                        "currencyUnits": 10,
                        "buyRate": 2.5975,
                        "sellRate": 2.6565,
                        "fixedRate": 2.62729
                    },
                    {
                        "currencyCode": "EUR",
                        "rateType": "transfer",
                        "currencyName": "EUR",
                        "date": "18/06/2020",
                        "currencyUnits": 1,
                        "buyRate": 1.95,
                        "sellRate": 1.96,
                        "fixedRate": 1.95583
                    },
                    {
                        "currencyCode": "EUR",
                        "rateType": "cash",
                        "currencyName": "EUR",
                        "date": "18/06/2020",
                        "currencyUnits": 1,
                        "buyRate": 1.95,
                        "sellRate": 1.96,
                        "fixedRate": 1.95583
                    },
                    {
                        "currencyCode": "GBP",
                        "rateType": "cash",
                        "currencyName": "GBP",
                        "date": "22/07/2022",
                        "currencyUnits": 1,
                        "buyRate": 2.268,
                        "sellRate": 2.327,
                        "fixedRate": 2.29717
                    },
                    {
                        "currencyCode": "GBP",
                        "rateType": "transfer",
                        "currencyName": "GBP",
                        "date": "22/07/2022",
                        "currencyUnits": 1,
                        "buyRate": 2.268,
                        "sellRate": 2.327,
                        "fixedRate": 2.29717
                    },
                    {
                        "currencyCode": "JPY",
                        "rateType": "cash",
                        "currencyName": "JPY",
                        "date": "22/07/2022",
                        "currencyUnits": 100,
                        "buyRate": 1.3635,
                        "sellRate": 1.4225,
                        "fixedRate": 1.40193
                    },
                    {
                        "currencyCode": "JPY",
                        "rateType": "transfer",
                        "currencyName": "JPY",
                        "date": "22/07/2022",
                        "currencyUnits": 100,
                        "buyRate": 1.3635,
                        "sellRate": 1.4225,
                        "fixedRate": 1.40193
                    },
                    {
                        "currencyCode": "NOK",
                        "rateType": "cash",
                        "currencyName": "NOK",
                        "date": "22/07/2022",
                        "currencyUnits": 10,
                        "buyRate": 1.8945,
                        "sellRate": 1.9535,
                        "fixedRate": 1.92696
                    },
                    {
                        "currencyCode": "NOK",
                        "rateType": "transfer",
                        "currencyName": "NOK",
                        "date": "22/07/2022",
                        "currencyUnits": 10,
                        "buyRate": 1.8945,
                        "sellRate": 1.9535,
                        "fixedRate": 1.92696
                    },
                    {
                        "currencyCode": "SEK",
                        "rateType": "transfer",
                        "currencyName": "SEK",
                        "date": "22/07/2022",
                        "currencyUnits": 10,
                        "buyRate": 1.8465,
                        "sellRate": 1.9055,
                        "fixedRate": 1.87469
                    },
                    {
                        "currencyCode": "SEK",
                        "rateType": "cash",
                        "currencyName": "SEK",
                        "date": "22/07/2022",
                        "currencyUnits": 10,
                        "buyRate": 1.8465,
                        "sellRate": 1.9055,
                        "fixedRate": 1.87469
                    },
                    {
                        "currencyCode": "USD",
                        "rateType": "transfer",
                        "currencyName": "USD",
                        "date": "22/07/2022",
                        "currencyUnits": 1,
                        "buyRate": 1.896,
                        "sellRate": 1.945,
                        "fixedRate": 1.91936
                    },
                    {
                        "currencyCode": "USD",
                        "rateType": "cash",
                        "currencyName": "USD",
                        "date": "22/07/2022",
                        "currencyUnits": 1,
                        "buyRate": 1.896,
                        "sellRate": 1.945,
                        "fixedRate": 1.91936
                    }
                ]
            """.utf8)
            guard let parsedData = try? JSONDecoder().decode([ExchangeRate].self, from: rawData) else {
                return []
            }
            return parsedData
        }
        
        enum RateType: String, Codable {
            case cash
            case transfer
        }
    }

}
