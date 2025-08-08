//
//  CashWithdrawalService.swift
//  Allianz (iOS)
//
//  Created by Prem's on 10/01/22.
//

import Foundation

class CashWithdrawalService {
    
    func requestCashWithdrawal(
        params: CashWithdrawalParams,
        completionHandler: @escaping (
            _ error: Error?,
            _ result: CashWithdrawalModel?
        ) -> Void
    ) {
        BasicRequest.shared.request(
            url: .cashWithdrawal,
            method: .post,
            parameters: params
        ) { _, result, error in
            if let error = error {
                // Networking and Server errors
                completionHandler(error, nil)
            } else {
                if let result = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let cashWithdrawalData = try decoder.decode(CashWithdrawalModel.self, from: result)
                        completionHandler(nil, cashWithdrawalData)
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
    
}

struct CashWithdrawalParams: Codable {
    
    var amount: String?
    var description: String?
    var currency: String?
    var executionDate: String?
    var branch: String?
}

struct CashWithdrawalModel: Codable {
    let success: Bool?
}
