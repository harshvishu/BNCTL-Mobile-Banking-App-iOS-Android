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
    let opening: Double?     // TODO: opening is coming a nil from API
    let blocked: Double?     // TODO: blocked is coming a nil from API
    let overdraft:Double?   // TODO: overdraft is coming a nil from API
}
