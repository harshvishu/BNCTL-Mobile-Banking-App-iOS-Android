//
//  PendingTransfer.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 26.11.21.
//

import Foundation

struct PendingTransfer:Codable, Identifiable, Hashable  {
    let transferId:String
    let description:String?
    let additionalDescription:String?
    let beneficiaryBankName:String?
    let beneficiaryName:String?
    let beneficiary:String?
    let beneficiaryBankSwiftCode:String?
    let currency:String
    let amount:Double
    let sourceAccount:String
    let issuerName:String?
    let destinationAccount:String?
    let status:String
    let transactionType:String
    let numberOfDocuments: Int?
    
    var id: String { self.transferId }
}

