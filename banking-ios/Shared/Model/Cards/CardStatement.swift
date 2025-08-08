//
//  CardStatements.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 16.09.22.
//

import Foundation

struct CardStatement: Decodable, Identifiable, Hashable {

    let amount: Double
    let transactionType: String?
    let debitAmount: Double
    let creditAmount: Double
    let beneficiary: String?
    let transferId: String
    let description: String?
    let additionalDescription: String?
    let valueDate: String //TODO
    let transferDate: String //TODO
    let balance: Double
    let closingBalance: Double
    let currency: String
    let sourceAccount: String?
    let transferReference: String
    
    let id = UUID()
    /*
    var id:String {
        self.transferId
    }
    */
}

extension CardStatement: PaymentDetails {
    
    var destinationAccount: String? {
        nil
    }
    
    var status: String {
        "Completed"
    }
    
}
