//
//  AuthenticationError.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 4.03.22.
//

import Foundation

// Deprecated.
// Please use a more concrete Error implementation.
//TODO: Mark it with @available
enum AuthenticationError: Error, Equatable {

    static func == (lhs: AuthenticationError, rhs: AuthenticationError) -> Bool {
        return lhs.localizedDescription == rhs.localizedDescription
    }
    
    case invalidCredential
    case custom(errorMessage: String)
    case cancelledSCA(message: String)
    case noInternetConnection
    case error(Error)
}
