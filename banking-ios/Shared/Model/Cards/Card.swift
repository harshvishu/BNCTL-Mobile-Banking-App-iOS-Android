//
//  Card.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 7.10.21.
//

import Foundation

struct Card: Codable, Hashable {

    let accountId: String
    let cardNumber: String
    let cardBIN: String
    let cardIIN: String
    let cardType: String
    let cardProduct: String
    let cardProductCode: String
    let cardProductLabel: String
    let cardAccountNumber: String
    let cardPrintName: String
    let cardOwner:String
    let cardStatus: String
    let expiryDate: String
    let currency: String
    let accountBalance: Double
    let availableBalance: Double
    let approvedOverdraft: Double
    let blockedAmount: Double
    let cardId: String
    let cardSecret: String
}
