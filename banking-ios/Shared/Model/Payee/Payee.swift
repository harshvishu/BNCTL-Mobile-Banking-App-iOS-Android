//
//  PayeesResponse.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 12/04/23.
//

import Foundation

struct Payee: Codable {
    let walletAccountNumber: String?
    let accountNumber: String
    let type: PayeeType
    let userId: String
    let walletProvider: String?
    let bank: String
    let notificationLanguage: String?
    let accountTypeId: String
    let isDeleted: Bool
    let name: String
    let currency: String
    let payeeId: String
    let email: String?
    let swift: String
}

extension Payee: Identifiable {
    var id: String {
        get { self.payeeId }
    }
}

enum PayeeType: String, Codable {
    case bank
    
    /// Computed properties
    var displayName: String {
        let displayNameMap: [Self : String] = [.bank : "Same bank"]
        return displayNameMap[self] ?? ""
    }
}
