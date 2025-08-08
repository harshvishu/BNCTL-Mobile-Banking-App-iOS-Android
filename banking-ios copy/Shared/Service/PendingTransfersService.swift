//
//  PendingTransfersService.swift
//  Allianz
//
//  Created by Evgeniy Raev on 1.12.21.
//

import Foundation

class PendingTransfersService {
    
    func getTransfers(
        completionHandler: @escaping (
            _ error: Error?,
            _ transfersResponse: [PendingTransfer]?) -> Void)
    {
        let emptyBody:EmptyBody? = nil
        
        BasicRequest.shared.request(
            url: .pendingTransfers,
            method: .get,
            parameters: emptyBody) { _, result, error in
            
            if let error = error {
                print(error.localizedDescription)
                completionHandler(error, nil)
            }else{
                if let resultData = result{
                    do{
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        
                        let transferData = try decoder.decode(
                            DataStructure.self,
                            from: resultData)
                        
                        completionHandler(nil, transferData.result)
                    } catch DecodingError.valueNotFound(let type, let context) {
                        print("Type '\(type)' mismatch:", context.debugDescription)
                        print("codingPath:", context.codingPath)
                        completionHandler(ResultError.parseError, nil)
                    } catch DecodingError.typeMismatch(let type, let context) {
                        print("Type '\(type)' mismatch:", context.debugDescription)
                        print("codingPath:", context.codingPath)
                        completionHandler(ResultError.parseError, nil)
                    } catch DecodingError.keyNotFound(let key, let context){
                        print("Key '\(key)' not found:", context.debugDescription)
                        print("codingPath:", context.codingPath)
                        completionHandler(ResultError.parseError, nil)
                    } catch {
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(ResultError.parseError, nil)
                    }
                }else{
                    completionHandler(ResultError.noResultError, nil)
                }
            }
        }
    }
    
    func approveTransfers(
        transferIds:[String],
        fallback:Fallback? = nil,
        completionHandler: @escaping (
            _ error: Error?,
            _ transferResponse: TransferActionSuccess?) -> Void) {
        
        BasicRequest.shared.request(
            url: .pendingTransfersApprove,
            method: .post,
            parameters: TransfersActionBody(transfers: transferIds, fallbackParams: fallback)
        ) { _, result, error in
            
            if let error = error {
                print(error.localizedDescription)
                completionHandler(error, nil)
            }else{
                if let resultData = result{
                    let decoder = JSONDecoder()
                    decoder.keyDecodingStrategy = .convertFromSnakeCase
                    
                    do {
                        let tratransfersData = try decoder.decode(
                            TransferActionSuccess.self,
                            from: resultData)
                            
                        completionHandler(nil, tratransfersData)
                                
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
                           completionHandler(ResultError.parseError, nil)
                       }
                }else{
                    completionHandler(ResultError.noResultError, nil)
                }
            }
        }
    }
    
    func rejectTransfers(
        transferIds:[String],
        fallback:Fallback? = nil,
        completionHandler: @escaping (
            _ error: Error?,
            _ transferResponse: TransferActionSuccess?) -> Void) {
        
        BasicRequest.shared.request(
            url: .pendingTransfersReject,
            method: .post,
            parameters: TransfersActionBody(transfers: transferIds, fallbackParams: fallback)
        ) { _, result, error in
            
            if let error = error {
                print(error.localizedDescription)
                completionHandler(error, nil)
                
            }else{
                if let resultData = result{
                    let decoder = JSONDecoder()
                    decoder.keyDecodingStrategy = .convertFromSnakeCase
                    
                    if let tratransfersData = try? decoder.decode(
                        TransferActionSuccess.self,
                        from: resultData) {
                        
                        completionHandler(nil, tratransfersData)
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
    
    struct DataStructure:Codable {
        let result:[PendingTransfer]
    }
    
    struct TransfersActionBody: Encodable {
        let transfers: [String]
        
        let fallbackParams:Fallback?
    }
    
    struct TransfersActionError: Decodable {
        let error:ErrorPart
        let method:String
        let type:String
        let message:String
        
        struct ErrorPart:Decodable {
            let code:String
            let message:String
            let target:String
        }
    }
    
    struct TransferActionSuccess: Decodable {
        let status:String
        let fallback:Fallback
        
        struct Fallback:Decodable {
            let usePin:Bool
            let useFallback:Bool
        }
    }
}

struct Fallback:Encodable {
    let useFallback:Bool
    let smsCode:String?
    let pin:String?
}
