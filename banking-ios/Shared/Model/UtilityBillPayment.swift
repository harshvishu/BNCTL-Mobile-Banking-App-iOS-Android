//
//  UtilityBillsModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 22/12/21.
//

import Foundation

struct UtilityBillPayment: Codable, Identifiable, Hashable {

    var id: String { self.userBillerId }
    
    let userBillerId: String
    let userId: String
    let name: String
    let clientReference: String
    let billAmount: Double
    let currencyName: String
    let biller: Biller
    let status: String?
    
    var isPaid: Bool {
        get {
            return billAmount == 0
        }
    }
    
    struct Biller: Codable, Hashable {
        let billerId: String
        let name: String?
        let type: String
        let subType: String
        let identifier: String
        let description: String
    }
}
