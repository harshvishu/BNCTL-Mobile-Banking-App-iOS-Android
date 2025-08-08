//
//  LocationService.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 26.01.22.
//

import Foundation

class LocationService {
    
    func fetchBranches(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ branches: [Location]?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(url: .locationsBranches, method: .get, parameters: emptyBody) {_, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let result = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let locationsResult = try decoder.decode(LocationsResult.self, from: result)
                        completionHandler(nil, locationsResult.records)
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
    
    func fetchATMs(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ branches: [Location]?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(url: .locationsATMs, method: .get, parameters: emptyBody) {_, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let result = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let locationsResult = try decoder.decode(LocationsResult.self, from: result)
                        completionHandler(nil, locationsResult.records)
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
    
}

struct LocationsResult: Codable {
    let records: [Location]
}
