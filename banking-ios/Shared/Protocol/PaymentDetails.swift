//
//  DisplayDetails.swift
//  Allianz (iOS)
//
//  Created by Kavitha Sambandam on 14/10/22.
//

import Foundation

protocol PaymentDetails {
    var amount: Double { get }
    var currency: String { get }
    var transactionType: String? { get }
    var sourceAccount: String? { get }
    var destinationAccount: String? { get }
    var beneficiary: String? { get } // This is provider in Bill Payments History
    var description: String? { get }
    var additionalDescription: String? { get }
    var transferDate: String { get } // TODO: use date
    var status: String { get }
}
