//
//  Currency.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 9.12.22.
//

import Foundation

enum Currency: String, CaseIterable, Identifiable {
    case BGN, JPY, DKK, SEK, CHF, EUR, GBP, NOK, USD
    var id: String { self.rawValue }
}
