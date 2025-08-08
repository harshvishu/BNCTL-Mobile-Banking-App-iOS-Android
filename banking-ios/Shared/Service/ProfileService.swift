//
//  ProfileService.swift
//  Allianz (iOS)
//
//  Created by Dimitar Stoyanov Chukov on 11.03.22.
//

import Foundation

class ProfileService {
    
    func getProfiles(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ profileResponse: [Profile]?) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .profile,
            method: .get,
            parameters: emptyBody
        ) { _, result, error in 
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let profileData = try decoder.decode(ProfileModel.self, from: resultData)
                        completionHandler(nil, profileData.result)
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
