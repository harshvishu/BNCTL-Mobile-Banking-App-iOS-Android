//
//  Card+Examples.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 22.11.22.
//

import Foundation

extension Card {
    
    static var preview1: Card? {
        let rawData = Data("""
        {
            "accountId": "276115",
            "accountBalance": 2252.98,
            "availableBalance": 2247.98,
            "blockedAmount": 5,
            "cardProduct": "mastercard",
            "cardProductCode": "5168950",
            "cardProductLabel": "MasterCard Debit безк.-Пакет-Персонални",
            "cardNumber": "516895******2775",
            "cardBIN": "516895",
            "cardIIN": "",
            "cardType": "debit",
            "description": "",
            "cardAccountNumber": "02BUIN95611000676723",
            "cardStatus": "new",
            "expiryDate": "2024-03-31",
            "cardPrintName": "IVAN PETROV GEORGIEV",
            "currency": "BGN",
            "approvedOverdraft": 0
        }
        """.utf8)
        guard let parsedData = try? JSONDecoder().decode(Card.self, from: rawData) else {
            return nil
        }
        return parsedData
    }

    static var preview2: Card? {
        let rawData = Data("""
        {
            "accountId": "276116",
            "accountBalance": 5120.20,
            "availableBalance": 4500.90,
            "blockedAmount": 5,
            "cardProduct": "mastercard",
            "cardProductCode": "5168959",
            "cardProductLabel": "MasterCard Debit безк.-Пакет-Персонални",
            "cardNumber": "516895******2776",
            "cardBIN": "516895",
            "cardIIN": "",
            "cardType": "credit",
            "description": "",
            "cardAccountNumber": "02BUIN95611000676723",
            "cardStatus": "new",
            "expiryDate": "2025-07-31",
            "cardPrintName": "IVAN PETROV GEORGIEV",
            "currency": "BGN",
            "approvedOverdraft": 0
        }
        """.utf8)
        guard let parsedData = try? JSONDecoder().decode(Card.self, from: rawData) else {
            return nil
        }
        return parsedData
    }

    static var preview3: Card? {
        let rawData = Data("""
        {
            "accountId": "276117",
            "accountBalance": 13050.20,
            "availableBalance": 10566.90,
            "blockedAmount": 5,
            "cardProduct": "mastercard",
            "cardProductCode": "5168959",
            "cardProductLabel": "MasterCard Debit безк.-Пакет-Персонални",
            "cardNumber": "516895******2777",
            "cardBIN": "516895",
            "cardIIN": "",
            "cardType": "debit",
            "description": "",
            "cardAccountNumber": "02BUIN95611000676724",
            "cardStatus": "active",
            "expiryDate": "2026-01-20",
            "cardPrintName": "IVAN PETROV GEORGIEV",
            "currency": "BGN",
            "approvedOverdraft": 2000
        }
        """.utf8)
        guard let parsedData = try? JSONDecoder().decode(Card.self, from: rawData) else {
            return nil
        }
        return parsedData
    }
    
    static var list:[Card] {
        let rawData = Data("""
            [
                {
                    "accountId": "226121",
                    "accountBalance": 1403.95,
                    "availableBalance": 1398.95,
                    "blockedAmount": 0,
                    "cardProduct": "mastercard",
                    "cardProductCode": "5168950",
                    "cardProductLabel": "MasterCard Debit безконт.-Персонални",
                    "cardNumber": "516895******8623",
                    "cardBIN": "516895",
                    "cardIIN": "",
                    "cardType": "debit",
                    "description": "",
                    "cardAccountNumber": "03BUIN95611000520826",
                    "cardStatus": "produced_not_received",
                    "expiryDate": "2025-08-31",
                    "cardPrintName": "DBBX NDND",
                    "cardOwner": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                    "currency": "EUR",
                    "approvedOverdraft": 0
                },
                {
                    "accountId": "226121",
                    "accountBalance": 1403.95,
                    "availableBalance": 1398.95,
                    "blockedAmount": 0,
                    "cardProduct": "mastercard",
                    "cardProductCode": "5168950",
                    "cardProductLabel": "MasterCard Debit безконт.-Персонални",
                    "cardNumber": "516895******8649",
                    "cardBIN": "516895",
                    "cardIIN": "",
                    "cardType": "debit",
                    "description": "",
                    "cardAccountNumber": "03BUIN95611000520826",
                    "cardStatus": "produced_not_received",
                    "expiryDate": "2025-08-31",
                    "cardPrintName": "EVG TEST",
                    "cardOwner": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                    "currency": "EUR",
                    "approvedOverdraft": 0
                },
                {
                    "accountId": "226122",
                    "accountBalance": 169.9,
                    "availableBalance": 2169.9,
                    "blockedAmount": 0,
                    "cardProduct": "mastercard",
                    "cardProductCode": "5200110",
                    "cardProductLabel": "MasterCard Standard безконтактна-Персонални",
                    "cardNumber": "520011******6164",
                    "cardBIN": "520011",
                    "cardIIN": "",
                    "cardType": "credit",
                    "description": "",
                    "cardAccountNumber": "22BUIN95611000521260",
                    "cardStatus": "active",
                    "expiryDate": "2025-12-31",
                    "cardPrintName": "DANIEL TOKUSHEV",
                    "cardOwner": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                    "currency": "BGN",
                    "approvedOverdraft": 2000
                }
            ]
        """.utf8)
        
        do {
            let parsedData = try JSONDecoder().decode([Card].self, from: rawData)
            return parsedData
        } catch DecodingError.valueNotFound(let type, let context) {
            print("Type '\(type)' mismatch:", context.debugDescription)
            print("codingPath:", context.codingPath)
        } catch DecodingError.typeMismatch(let type, let context) {
            print("Type '\(type)' mismatch:", context.debugDescription)
            print("codingPath:", context.codingPath)
        } catch DecodingError.keyNotFound(let key, let context){
            print("Key '\(key)' not found:", context.debugDescription)
            print("codingPath:", context.codingPath)
        } catch {
            print(error.localizedDescription)
        }
        return []
    }
}
