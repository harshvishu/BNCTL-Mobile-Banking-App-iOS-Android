//
//  DeviceError.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 4.03.22.
//

import Foundation

enum NetworkError: Error, Equatable {
    
    static func == (lhs: NetworkError, rhs: NetworkError) -> Bool {
        return lhs.localizedDescription == rhs.localizedDescription
    }
    
    case noInternetConnectionError
    case networkError(Error)
    case noResponseReceivedError
    case malformedResponseError
    
}
