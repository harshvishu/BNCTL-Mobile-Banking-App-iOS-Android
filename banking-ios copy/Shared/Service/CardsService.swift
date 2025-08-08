//
//  CardsService.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 23.12.21.
//

import Foundation

class CardsService {
    
    func fetchCards(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ cards: [Card]?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .cards,
            method: .get,
            parameters: emptyBody
        ) { _, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let result = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let cardsResult = try decoder.decode(CardsResult.self, from: result)
                        completionHandler(nil, cardsResult.result)
                    } catch {
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.custom(errorMessage: "Decoding error"), nil)
                    }
                } else {
                    completionHandler(.custom(errorMessage: "No data"), nil)
                }
            }
        }
    }
    
    func fetchCardProducts(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ cards: [CardProduct]?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .cardProducts,
            method: .get,
            parameters: emptyBody
        ) { _, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let result = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let cardProductsResult = try decoder.decode(CardProductsResult.self, from: result)
                        completionHandler(nil, cardProductsResult.result)
                    } catch {
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.custom(errorMessage: "Decoding error"), nil)
                    }
                } else {
                    completionHandler(.custom(errorMessage: "No data"), nil)
                }
            }
        }
    }
    
    func requestCard(
        params: CardRequest,
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ isSuccessful: Bool?) -> Void
    ) {
        BasicRequest.shared.request(
            url: .cards,
            method: .post,
            parameters: params
        ) { _, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let result = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let requestCardResult = try decoder.decode(CardRequestResult.self, from: result)
                        completionHandler(nil, requestCardResult.success)
                    } catch {
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.custom(errorMessage: "Decoding error"), nil)
                    }
                } else {
                    completionHandler(.custom(errorMessage: "No data"), nil)
                }
            }
        }
    }
    
    func fetchCardStatements (
            cardNumber: String,
            fromDate: Date,
            toDate: Date,
            completionHandler: @escaping (
                _ error: AuthenticationError?,
                _ statementsResponse: [CardStatement]?
            ) -> Void
        ) {
            BasicRequest.shared.request(
                url: .cardsStatements,
                method: .post,
                parameters: CardStatementsRequestBody(
                    cardAccountNumber: cardNumber,
                    dateFrom: fromDate.formatToIsoDate(),
                    dateTo: toDate.formatToIsoDate())
            ) { statusCode, result, error in
                if let error = error {
                    print(error.localizedDescription)
                    completionHandler(.custom(errorMessage: "No data"), nil)
                } else {
                    if let resultData = result {
                        do {
                            let decoder = JSONDecoder()
                            decoder.keyDecodingStrategy = .convertFromSnakeCase
                            let statementData = try decoder.decode(CardStatementsResponseBody.self, from: resultData)
                            completionHandler(nil, statementData.result)
                        } catch DecodingError.typeMismatch(let type, let context) {
                            print("Type '\(type)' mismatch:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                        } catch {
                            Logger.E(tag: APP_NAME, error.localizedDescription)
                            completionHandler(.custom(errorMessage: "Decoding error"), nil)
                        }
                    } else {
                        completionHandler(.custom(errorMessage: "No data"), nil)
                    }
                }
            }
    }
    
    func fetchCreditCardStatements(
        cardNumber: String,
        fromDate: Date,
        toDate: Date,
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ statementsResponse: [CreditCardStatement]?
        ) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        let queryItems: [String: String] = [
            "cardNumber": cardNumber,
            "fromDate": fromDate.formatToIsoDate(),
            "toDate": toDate.formatToIsoDate()
        ]
        BasicRequest.shared.request(
            url: .fetchCreditCardStatements,
            method: .get,
            parameters: emptyBody,
            queryItems: queryItems
        ) { statusCode, result, error in
            if let error = error {
                print(error.localizedDescription)
                completionHandler(.custom(errorMessage: "No data"), nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let statementData = try decoder.decode(CreditCardStatementsModel.self, from: resultData)
                        completionHandler(nil, statementData.result)
                    } catch {
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.custom(errorMessage: "Decoding error"), nil)
                    }
                } else {
                    completionHandler(.custom(errorMessage: "No data"), nil)
                }
            }
            
        }
    }
    
    func downloadCreditCardStatement(
        fileName: String
    ) {
        let emptyBody: EmptyBody? = nil
        let queryItems: [String: String] = [
            "fileName": fileName
        ]
        let downloadManager: DownloadManagerService = DownloadManagerService.instance
        downloadManager.downloadFile(
            fileName: fileName,
            url: .downloadCreditCardStatement,
            method: .get,
            parameters: emptyBody,
            queryItems: queryItems)
    }
    
    struct CardStatementsRequestBody:Encodable {
        var cardAccountNumber: String
        var dateFrom: String
        var dateTo: String
    }
    
    struct CardStatementsResponseBody:Decodable {
        let result:[CardStatement]
    }

    struct CardsResult: Codable {
        let result: [Card]
    }
    
    struct CardProductsResult: Codable {
        let result: [CardProduct]
    }
    
    struct CardRequestResult: Codable {
        let success: Bool
    }
}
