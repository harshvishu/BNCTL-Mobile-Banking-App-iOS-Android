//
//  EditPayeeResponse.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 18/04/23.
//

import Foundation

struct EditPayeeResponse: Codable {
    let payeeId: String
    let payeeName: String
    let address: String?
    let city: String?
    let country: String?
    let bankName: String
    let accountNumber: String
    let accountTypeId: String
    let swift: String
    let userId: String
    let email: String
    let currency: String
    let notificationLanguage: String
    let type: String
}
