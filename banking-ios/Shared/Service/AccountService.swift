//
//  AccountService.swift
//  Allianz (iOS)
//
//  Created by Prem's on 08/11/21.
//

import Foundation

class AccountService {
    
    func getAccounts(
        permissionCode: OperationType? = nil,
        debit:Bool? = nil,
        completionHandler: @escaping (
            _ error: AccountsError?,
            _ accountResponse: [Account]?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        var queryItems: [String:String] = [:]
        if let permissionCode = permissionCode {
            queryItems["transactionType"] = permissionCode.rawValue
        }
        if let debit = debit {
            queryItems["debit"] = debit.description
        }
        BasicRequest.shared.request(
            url: .account,
            method: .get,
            parameters: emptyBody,
            queryItems: queryItems.count > 0 ? queryItems : nil
        ) { _, result, error in
            if let error = error {
                print(error.localizedDescription)
                completionHandler(.requestError, nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let accountData = try decoder.decode(AccountsResponse.self, from: resultData)
                        completionHandler(nil, accountData.result)
                    } catch {
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.decodingError, nil)
                    }
                } else {
                    completionHandler(.noData, nil)
                }
            }
        }
    }
    
    func getAccountStatement(
        accountId: String,
        queryItems: [String: String],
        completionHandler: @escaping (
            _ error: AccountsError?,
            _ statementResponse: [Statement]?) -> Void
    ) {
        let emptyBody:EmptyBody? = nil
        BasicRequest.shared.request(
            url: .accountStatment(accountId),
            method: .get,
            parameters: emptyBody,
            queryItems: queryItems
        ) { statusCode, result, error in
            if let error = error {
                print(error.localizedDescription)
                completionHandler(.noData, nil)
            } else {
                if let resultData = result{
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let statementData = try decoder.decode(StatementModel.self, from: resultData)
                        completionHandler(nil, statementData.records)
                    } catch DecodingError.valueNotFound(let type, let context) {
                        print("Type '\(type)' mismatch:", context.debugDescription)
                        print("codingPath:", context.codingPath)
                    } catch DecodingError.typeMismatch(let type, let context) {
                        print("Type '\(type)' mismatch:", context.debugDescription)
                        print("codingPath:", context.codingPath)
                    } catch DecodingError.keyNotFound(let key, let context){
                        print("Key '\(key)' not found:", context.debugDescription)
                        print("codingPath:", context.codingPath)
                    } catch {
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.decodingError, nil)
                    }
                } else {
                    completionHandler(.noData, nil)
                }
            }
        }
    }
    
    enum AccountsError:Error {
        case noData
        case decodingError
        case requestError
    }
}
