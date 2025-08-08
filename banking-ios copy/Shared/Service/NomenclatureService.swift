//
//  NomenclatureService.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 20.01.22.
//

import Foundation

class NomenclatureService {
    
    func fetchBranches(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ branches: [Branch]?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(url: .branches, method: .get, parameters: emptyBody) {_, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let result = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let branchesResult = try decoder.decode(BranchesResult.self, from: result)
                        completionHandler(nil, branchesResult.result)
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
    
    struct BranchesResult: Codable {
        let result: [Branch]
    }
}
