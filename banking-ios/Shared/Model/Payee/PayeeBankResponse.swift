//
//  PayeeBankResponse.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 12/04/23.
//

import Foundation

typealias PayeeBankResponse = [String: Bank]

struct Bank: Codable {
    let name: String
    let swift: String
}

extension Bank: Identifiable {
    var id: String {
        get { self.name + self.swift }
    }
}
