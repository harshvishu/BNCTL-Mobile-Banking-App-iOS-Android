//
//  Balance.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 7.10.21.
//

import Foundation

struct Balance: Codable, Hashable {
    let current: Double
    let available: Double
    let opening: Double
    let blocked: Double
    let overdraft:Double
}
