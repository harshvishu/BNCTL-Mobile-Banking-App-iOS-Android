//
//  UtilityBillsService.swift
//  Allianz (iOS)
//
//  Created by Prem's on 22/12/21.
//

import Foundation

class UtilityBillsService {
    
    func fectchUtilityBills(completionHandler: @escaping (_ error: AuthenticationError?, _ utilityBillsResponse: [UtilityBillPayment]?) -> Void) {
        
        let emptyBody:EmptyBody? = nil
        
        BasicRequest.shared.request(
            url: .fetchUtilityBills,
            method: .get,
            parameters: emptyBody) { statusCode, result, error in
            
            if let error = error {
                print(error.localizedDescription)
                completionHandler(.custom(errorMessage: "No data"), nil)
            }else{
                if let resultData = result{
                    do{
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let UtilityBillPayment = try decoder.decode([UtilityBillPayment].self, from: resultData)
                        completionHandler(nil, UtilityBillPayment)
                    }catch{
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.custom(errorMessage: "Decoding error"), nil)
                    }
                }else{
                    completionHandler(.custom(errorMessage: "No data"), nil)
                }
            }
        }
    }
    
    func approveUtilityBills(
        billPayments:[String],
        sourceAccount:Account,
        fallback:Fallback? = nil,
        completionHandler: @escaping (
            _ error: Error?,
            _ transferResponse: UtilitBillSuccessPayment.Result?) -> Void) {
        
        BasicRequest.shared.request(
            url: .approveUtilityBills,
            method: .post,
            parameters: ApproveUtilityBillsRequestBody(
                billPayments: billPayments,
                sourceAccount: sourceAccount.iban,
                sourceAccountId: sourceAccount.id,
                sourceAccountHolder: sourceAccount.beneficiary.name,
                fallbackParams: fallback
            )
        ) { _, result, error in
            if let error = error {
                print(error.localizedDescription)
                completionHandler(error, nil)
            }else{
                if let resultData = result{
                    let decoder = JSONDecoder()
                    decoder.keyDecodingStrategy = .convertFromSnakeCase
                    
                    if let tratransfersData = try? decoder.decode(
                        UtilitBillSuccessPayment.self,
                        from: resultData) {
                        
                        completionHandler(nil, tratransfersData.result)
                        
                    } else if let transferError = try? decoder.decode(ServerError.self, from: resultData) {
                        
                        completionHandler(transferError, nil)
                    } else {
                        Logger.E(tag: APP_NAME, error?.localizedDescription ?? "unhandaled error in pendingTransfers")
                        completionHandler(ResultError.parseError, nil)
                    }
                }else{
                    completionHandler(ResultError.noResultError, nil)
                }
            }
        }
        
    }
    
    struct UtilitBillSuccessPayment: Decodable {
        let result:Result
        
        struct Result:Decodable {
            let transferId:String
            let status:String
            let usePin:Bool
            let processingResult:String?
            let transferIdIssuer:String?
            let transferIdMerchant:String?
            let transferIdAcquirer:String?
            let transferIdLedger:String?
            let transactionType:String?
        }
    }
    
    struct ApproveUtilityBillsRequestBody:Encodable {
        let billPayments:[String]
        let sourceAccount:String
        let sourceAccountId:String
        let sourceAccountHolder:String
        
        let fallbackParams:Fallback?
    }
    
    
    func fetchUtilityBillsHistory(queryItems: [String: String],completionHandler: @escaping (_ error: AuthenticationError?, _ utilityBillsHistoryResponse: [UtilityBillsHistory]?) -> Void) {
        
        let emptyBody:EmptyBody? = nil
        
        BasicRequest.shared.request(url: .fetchUtilityBillsHistory, method: .get, parameters: emptyBody, queryItems: queryItems) { statusCode, result, error in
            
            if let error = error {
                print(error.localizedDescription)
                completionHandler(.custom(errorMessage: "No data"), nil)
            }else{
                if let resultData = result{
                    do{
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let utilityBillsHistoryData = try decoder.decode(UtilityBillsHistoryModel.self, from: resultData)
                        completionHandler(nil, utilityBillsHistoryData.records)
                    } catch DecodingError.valueNotFound(let type, let context) {
                        print("Type '\(type)' mismatch:", context.debugDescription)
                        print("codingPath:", context.codingPath)
                    } catch DecodingError.typeMismatch(let type, let context) {
                        print("Type '\(type)' mismatch:", context.debugDescription)
                        print("codingPath:", context.codingPath)
                    } catch DecodingError.keyNotFound(let key, let context){
                        print("Key '\(key)' not found:", context.debugDescription)
                        print("codingPath:", context.codingPath)
                    }catch{
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.custom(errorMessage: "Decoding error"), nil)
                    }
                }else{
                    completionHandler(.custom(errorMessage: "No data"), nil)
                }
            }
        }
    }
}
