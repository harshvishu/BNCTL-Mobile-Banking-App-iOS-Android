//
//  TransferService+Extension.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 07/04/23.
//

import Foundation

extension TransferService {
    
    func createTransfer(
        params: Transfer,
        completionHandler: @escaping (
            _ error: Error?,
            _ transferResponse: TransferCreateResponse?
        ) -> Void
    ) {
        BasicRequest.shared.request(
            url: .transferCreate,
            method: .post,
            parameters: params
        ) { _, result, error in
            if let error = error {
                // Network and Server errors
                
                completionHandler(error, nil)
            } else {
                if let resultData = result {
                    let decoder = JSONDecoder()
                    decoder.keyDecodingStrategy = .convertFromSnakeCase
                    
                    if let transferData = try? decoder.decode(
                        DataStructure<TransferCreateResponse>.self,
                        from: resultData
                    ) {
                        completionHandler(
                            nil,
                            transferData.result
                        )
                    } else {
                        // Parsing error
                        Logger.E(tag: APP_NAME, "unsupported error")
                        completionHandler(ResultError.parseError, nil)
                    }
                } else {
                    // No result to parse
                    completionHandler(ResultError.noResultError, nil)
                }
            }
        }
    }
    
    func confirmTransfer(
        params: ConfirmTransfer,
        completionHandler: @escaping (
            _ error: Error?,
            _ transferResponse: TransferConfirmResponse?
        ) -> Void
    ) {
        BasicRequest.shared.request(
            url: .tranferConfirm,
            method: .post,
            encoding: .urlFormEncoded,
            parameters: params
        ) { _, result, error in
            if let error = error {
                // Network and Server errors
                
                completionHandler(error, nil)
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
                            transferData.result
                        )
                    } else {
                        // Parsing error
                        Logger.E(tag: APP_NAME, "unsupported error")
                        completionHandler(ResultError.parseError, nil)
                    }
                } else {
                    // No result to parse
                    completionHandler(ResultError.noResultError, nil)
                }
            }
        }
    }
    
}
