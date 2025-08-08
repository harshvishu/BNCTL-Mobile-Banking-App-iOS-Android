//
//  TransferService.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 17.11.21.
//

import Foundation

class TransferService {
    
    func createTransfer(
        params: Transfer,
        completionHandler: @escaping (
            _ error: Error?,
            _ transferResponse: TransferConfirmResponse?,
            _ fallback: FallbackResponse?
        ) -> Void
    ) {
        BasicRequest.shared.request(
            url: .transferExcecute,
            method: .post,
            parameters: params
        ) { _, result, error in
            if let error = error {
                // Network and Server errors
                
                completionHandler(error, nil, nil)
            } else {
                if let resultData = result {
                    let decoder = JSONDecoder()
                    decoder.keyDecodingStrategy = .convertFromSnakeCase
                    
                    if let transferData = try? decoder.decode(
                        DataStructure<TransferConfirmResponse>.self,
                        from: resultData
                    ) {
                        completionHandler(
                            nil,
                            transferData.result,
                            nil
                        )
                    } else if let fallbackData = try? decoder.decode(
                        DataStructure<FallbackResponse>.self,
                        from: resultData
                    ) {
                        completionHandler(nil, nil, fallbackData.result)
                    } else {
                        // Parsing error
                        Logger.E(tag: APP_NAME, "unsupported error")
                        completionHandler(ResultError.parseError, nil, nil)
                    }
                } else {
                    // No result to parse
                    completionHandler(ResultError.noResultError, nil, nil)
                }
            }
        }
    }
    
    func validateTransfer(
        params: Transfer,
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ transferResponse: TransferValidateResponse?
        ) -> Void
    ) {
        BasicRequest.shared.request(
            url: .transferValidate,
            method: .post,
            parameters: params
        ) { _, result, error in
            if let error = error {
                print(error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let resultData = result{
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let transferData = try decoder.decode(DataStructure<TransferValidateResponse>.self, from: resultData)
                        completionHandler(nil, transferData.result)
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
    
    func validateTransfer(transfer:Transfer) async throws -> TransferValidateResponse {
        return try await withCheckedThrowingContinuation({ continuation in
            validateTransfer(params: transfer) { error, validationResponse in
                if let error = error {
                    continuation.resume(throwing: error)
                }
                if let validationResponse = validationResponse {
                    continuation.resume(returning: validationResponse)
                }
            }
        })
    }
}


struct DataStructure<Body:Decodable>: Decodable {
    let result:Body
}

struct TransferCreateResponse:Decodable {
    let validationRequestId:String
    let objectId:String
    let validationStatus:String
    let serviceId:String
    let actionId:String
    let tenantId:String
    let channel:String
    let authorizationType:String
    let recipient:String
}

struct TransferConfirmResponse:Decodable {
    let transferId:String? // "24414671",
    let status:ConfirmationStatus // "success",
    
    enum ConfirmationStatus:String, Decodable {
        case success
    }
}

struct TransferValidateResponse:Decodable {
    let canCreateTransfer: Bool
    let status: String
    let message: String
    let chargeAmount: Double
    let chargeCurrency: String
}

struct FallbackResponse:Decodable {
    let status:FallbackStatus
    let usePin:Bool
    
    enum FallbackStatus:String, Decodable {
        case available = "waiting_for_fallback"
        case scaExpired = "scaExpired"
    }
}

/*
 {
 "error":{
 "code":"InternalErrorException",
 "message":"Allianz IB Core Banking Service currently unavailable",
 "target":"error"
 },
 "method":"allianz.transfer.create",
 "type":"allianz.errDatabase",
 "message":"Allianz IB Core Banking Service currently unavailable"
 }
 */
