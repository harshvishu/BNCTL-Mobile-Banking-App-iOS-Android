//
//  User.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 21.12.22.
//

import Foundation

struct Customer: Decodable {
    let firstName: String
    let lastName: String
}

extension Customer:Equatable {
    
}
