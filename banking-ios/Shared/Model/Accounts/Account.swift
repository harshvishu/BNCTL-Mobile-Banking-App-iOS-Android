//
//  Account.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 7.10.21.
//

import Foundation

struct Account: Codable, Identifiable, Hashable {
    let accountId: String
    let accountTypeDescription: String
    let product: Product
    let beneficiary: Beneficiary
    let balance: Balance
    let currencyName: String
    let accountName: String
    let accountNumber: String
    var iban: String?   // TODO: blocked is coming a nil from API
    let swift: String
    let ownerId: String
    
    var id: String { self.accountId }
    
    static var preview: Account? {
        let rawData = Data("""
        {
            "accountId": "276232",
            "product": {
                "code": "1RACC",
                "name": "Разплащателна сметка-ФЛ",
                "type": "operational",
                "group": "individual"
            },
            "accountTypeDescription": "Разплащателна сметка-ФЛ",
            "balance": {
                "current": 103.55,
                "available": 98.55,
                "opening": 4.85,
                "blocked": 0,
                "overdraft": 0
            },
            "beneficiary": {
                "name": "Разплащателна сметка-ФЛ"
            },
            "currencyName": "BGN",
            "accountName": "Разплащателна сметка-ФЛ",
            "iban": "BG88BUIN95611000677309",
            "accountNumber": "88BUIN95611000677309",
            "ownerId": "69792",
            "swift": "BUINBGSF"
        }
        """.utf8)
        guard let parsedData = try? JSONDecoder().decode(Account.self, from: rawData) else {
            return nil
        }
        return parsedData
    }
}
