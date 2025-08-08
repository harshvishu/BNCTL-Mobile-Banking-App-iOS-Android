//
//  PayeeService.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 12/04/23.
//

import Foundation

class PayeeService {
    
    func getBanks(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ response: [Bank]?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .payeesBanks,
            method: .get,
            parameters: emptyBody
        ) { _, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let response = try decoder.decode(DataStructure<PayeeBankResponse>.self, from: resultData)
                        completionHandler(nil, response.result.values.map({$0}))
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
    
    func getPayees(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ response: [Payee]?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .payees,
            method: .get,
            parameters: emptyBody
        ) { _, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let response = try decoder.decode(DataStructure<[Payee]>.self, from: resultData)
                        completionHandler(nil, response.result)
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
    
    func createPayee(
        payee: CreatePayeeRequest,
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ response: EditPayeeResponse?) -> Void
    ) {
        let body: PayeeRequest = PayeeRequest(
            accountNumber: payee.accountNumber,
            accountTypeId: payee.accountTypeId,
            bank: payee.bank,
            currency: payee.currency,
            email: payee.email,
            name: payee.name,
            notificationLanguage: Language.en.backend,  // FIXME: Get current set language
            saveNewPayee: true,
            swift: payee.swift,
            type: "bank"                                // FIXME: BNCTL *currently* doesn't support non-bank templates
        )
        
        BasicRequest.shared.request(
            url: .payees,
            method: .post,
            parameters: body
        ) { _, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let response = try decoder.decode(DataStructure<EditPayeeResponse>.self, from: resultData)
                        completionHandler(nil, response.result)
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
    
    func updatePayee(
        payee: UpdatePayeeRequest,
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ response: EditPayeeResponse?) -> Void
    ) {
        let body: PayeeRequest = PayeeRequest(
            accountNumber: payee.accountNumber,
            accountTypeId: payee.accountTypeId,
            bank: payee.bank,
            currency: payee.currency,
            email: payee.email,
            name: payee.name,
            notificationLanguage: Language.en.backend,  // FIXME: Get current set language
            saveNewPayee: true,
            swift: payee.swift,
            type: "bank"                                // FIXME: BNCTL *currently* doesn't support non-bank templates
        )
        
        BasicRequest.shared.request(
            url: .payee(payee.payeeId),
            method: .put,
            parameters: body
        ) { _, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let response = try decoder.decode(DataStructure<EditPayeeResponse>.self, from: resultData)
                        completionHandler(nil, response.result)
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
    
    func deletePayee(payeeId: String,
                     completionHandler: @escaping (
                        _ error: AuthenticationError?,
                        _ response: Void?) -> Void
    ){
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .payee(payeeId),
            method: .delete,
            parameters: emptyBody
        ) { _, result, error in
            
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let _ = result {
                    completionHandler(nil, ())
                } else {
                    completionHandler(.custom(errorMessage: "No data"), nil)
                }
            }
        }
    }
}

// MARK: Model for Update Payee API Call
struct UpdatePayeeRequest {
    let payeeId: String
    let accountNumber: String
    let accountTypeId: String
    let bank: String
    let currency: String
    let email: String
    let name: String
    let swift: String
}

// MARK: Model for Create Payee API Call
struct CreatePayeeRequest {
    let accountNumber: String
    let accountTypeId: String
    let bank: String
    let currency: String
    let email: String
    let name: String
    let swift: String
}
