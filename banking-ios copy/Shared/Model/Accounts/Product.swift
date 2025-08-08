//
//  Product.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 7.10.21.
//

import Foundation

struct Product: Codable, Hashable {
    let code: String
    let name: String
    let type: String
    let group: String
}
