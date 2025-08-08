    //
    //  CurrentUser.swift
    //  Allianz (iOS)
    //
    //  Created by Dimitar Stoyanov Chukov on 18.03.22.
    //

import Foundation

class CurrentUserService {
    
    func getCurrentUser(
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ currentUserResponse: CurrentUserReponse.Result?
        ) -> Void
    ) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(
            url: .currentUser,
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
                        let currentUserData = try decoder.decode(CurrentUserReponse.self, from: resultData)
                        completionHandler(nil, currentUserData.result)
                    }  catch {
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.custom(errorMessage: "Decoding error"), nil)
                    }
                } else {
                    completionHandler(.custom(errorMessage: "No Data"), nil)
                }
            }
        }
    }
    
    struct CurrentUserReponse: Decodable {
        
        struct Auth: Decodable {
            let firstLoginPassed: Bool
            let isLoggedIn: Bool
            let passwordChanged: Bool
            let permissions: [Permission]
            let userId: String
            let username: String
            let usernameChanged: Bool
        }

        let result: Result
    }
}
