//
//  EndOfDayService.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 28.09.22.
//

import Foundation

class EndOfDayService {
    
    func checkEndOfDay(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ isEndOfDay: Bool?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .checkEoD,
            method: .get,
            parameters: emptyBody
        ) {_, result, error in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let result = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let locationsResult = try decoder.decode(EndOfDayResult.self, from: result)
                        completionHandler(nil, locationsResult.result)
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
    
    func checkEndOfDay() async throws -> Bool {
        return try await withCheckedThrowingContinuation({ continuation in
            checkEndOfDay { error, result in
                if let error = error {
                    continuation.resume(throwing: error)
                }
                if let result = result {
                    continuation.resume(returning: result)
                }
            }
        })
    }
    
    struct EndOfDayResult:Decodable {
        let result:Bool
    }
}
