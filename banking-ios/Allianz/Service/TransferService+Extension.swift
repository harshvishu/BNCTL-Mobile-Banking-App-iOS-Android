//
//  TransferService+Extension.swift
//  Allianz
//
//  Created by harsh vishwakarma on 07/04/23.
//

import Foundation

extension TransferService {
    
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
}
