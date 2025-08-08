//
//  InformationService.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 12.12.22.
//

import Foundation

class InformationService {
    
    func getContacts(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ contacts: Contacts?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .contacts,
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
                        let locationsResult = try decoder.decode(ContactsResult.self, from: result)
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
    
    func getContacts() async throws -> Contacts {
        return try await withCheckedThrowingContinuation({ continuation in
            getContacts { error, result in
                if let error = error {
                    continuation.resume(throwing: error)
                }
                if let result = result {
                    continuation.resume(returning: result)
                }
            }
        })
    }
    
    struct ContactsResult:Decodable {
        let result:Contacts
    }
}
