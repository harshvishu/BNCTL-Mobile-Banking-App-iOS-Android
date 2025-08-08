//
//  ServerError.swift
//  Allianz
//
//  Created by Peter Velchovski on 4.03.22.
//

import Foundation

struct ServerError: Error, Decodable {
    let error: Error
    
    let method:String
    let type:String
    let message:String
    
    struct Error: Codable {
        let code: String
        let message: String
        let target: String?
    }
    
}

struct ServerFailierError: Error, Decodable {
    let method:String
}

struct UnhadeledError:Error {
    
}
