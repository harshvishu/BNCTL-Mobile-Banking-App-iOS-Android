//
//  TransferService.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 17.11.21.
//

import Foundation

class TransferService {
    
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
                        decoder.dateDecodingStrategy = .deferredToDate
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
    let recipient:String?
}

struct TransferConfirmResponse:Decodable {
    let transferId:String? // "24414671",
    let status:ConfirmationStatus // "success",
    
    enum ConfirmationStatus:String, Decodable {
        case success
        case failure
    }
}

struct TransferValidateResponse:Decodable {
    let canCreateTransfer: Bool
    let status: String?
    let message: String
    let chargeAmount: Double
    let chargeCurrency: String
}

extension TransferValidateResponse {
    enum CodingKeys: String, CodingKey {
        case canCreateTransfer
        case status
        case message
        case chargeAmount
        case chargeCurrency
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        canCreateTransfer = try container.decode(Bool.self, forKey: .canCreateTransfer)
        status = try container.decodeIfPresent(String.self, forKey: .status)
        message = try container.decode(String.self, forKey: .message)
        if let chargeAmountDouble = try? container.decode(Double.self, forKey: .chargeAmount) {
            chargeAmount = chargeAmountDouble
        } else {
            let chargeAmountString = try container.decode(String.self, forKey: .chargeAmount)
            chargeAmount = Double(chargeAmountString) ?? 0.0
        }
        chargeCurrency = try container.decode(String.self, forKey: .chargeCurrency)
    }
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
