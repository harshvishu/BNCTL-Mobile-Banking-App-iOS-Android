//
//  PayeeRequest.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 18/04/23.
//

import Foundation

struct PayeeRequest: Codable {
    let accountNumber: String
    let accountTypeId: String
    let bank: String
    let currency: String
    let email: String
    let name: String
    let notificationLanguage: String
    let saveNewPayee: Bool
    let swift: String
    let type: String
}
