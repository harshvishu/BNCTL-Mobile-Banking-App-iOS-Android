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
            "accountId": "290010",
            "accountBalance": 7574.5,
            "availableBalance": 7569.5,
            "blockedAmount": 0,
            "cardProduct": "mastercard",
            "cardProductCode": "5168950",
            "cardProductLabel": "Mastercard Debit - Пакет",
            "cardNumber": "516895******4378",
            "cardBIN": "516895",
            "cardIIN": "",
            "cardType": "debit",
            "description": "",
            "cardAccountNumber": "03BUIN95611000576116",
            "cardStatus": "active",
            "expiryDate": "2023-06-30",
            "cardPrintName": "BOYAN ANGELOV",
            "cardOwner": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
            "currency": "BGN",
            "approvedOverdraft": 0,
            "cardId": "135P22820154919167",
            "cardSecret": "A5AC859BC93F5C0F9A4EA63F45F071C244077FF5594B32C68A44BD786C58ACF3"
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
            "accountId": "290010",
            "accountBalance": 7574.5,
            "availableBalance": 7569.5,
            "blockedAmount": 0,
            "cardProduct": "mastercard",
            "cardProductCode": "5168950",
            "cardProductLabel": "MasterCard Debit безконт.-Персонални",
            "cardNumber": "516895******7464",
            "cardBIN": "516895",
            "cardIIN": "",
            "cardType": "debit",
            "description": "",
            "cardAccountNumber": "03BUIN95611000576116",
            "cardStatus": "produced_not_received",
            "expiryDate": "2026-01-31",
            "cardPrintName": "EVGENIY RAEV",
            "cardOwner": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
            "currency": "BGN",
            "approvedOverdraft": 0,
            "cardId": "135P20723017088289",
            "cardSecret": "0003:card is not active"
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
            "accountId": "290010",
            "accountBalance": 7574.5,
            "availableBalance": 7569.5,
            "blockedAmount": 0,
            "cardProduct": "mastercard",
            "cardProductCode": "5168950",
            "cardProductLabel": "MasterCard Debit безконт.-Персонални",
            "cardNumber": "516895******7506",
            "cardBIN": "516895",
            "cardIIN": "",
            "cardType": "debit",
            "description": "",
            "cardAccountNumber": "03BUIN95611000576116",
            "cardStatus": "produced_not_received",
            "expiryDate": "2026-01-31",
            "cardPrintName": "TEST DEVICE",
            "cardOwner": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
            "currency": "BGN",
            "approvedOverdraft": 0,
            "cardId": "135P20723017088290",
            "cardSecret": "0003:card is not active"
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
                "accountId": "290010",
                "accountBalance": 7574.5,
                "availableBalance": 7569.5,
                "blockedAmount": 0,
                "cardProduct": "mastercard",
                "cardProductCode": "5168950",
                "cardProductLabel": "Mastercard Debit - Пакет",
                "cardNumber": "516895******4378",
                "cardBIN": "516895",
                "cardIIN": "",
                "cardType": "debit",
                "description": "",
                "cardAccountNumber": "03BUIN95611000576116",
                "cardStatus": "active",
                "expiryDate": "2023-06-30",
                "cardPrintName": "BOYAN ANGELOV",
                "cardOwner": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
                "currency": "BGN",
                "approvedOverdraft": 0,
                "cardId": "135P22820154919167",
                "cardSecret": "A5AC859BC93F5C0F9A4EA63F45F071C244077FF5594B32C68A44BD786C58ACF3"
            },
            {
                "accountId": "290010",
                "accountBalance": 7574.5,
                "availableBalance": 7569.5,
                "blockedAmount": 0,
                "cardProduct": "mastercard",
                "cardProductCode": "5168950",
                "cardProductLabel": "MasterCard Debit безконт.-Персонални",
                "cardNumber": "516895******7464",
                "cardBIN": "516895",
                "cardIIN": "",
                "cardType": "debit",
                "description": "",
                "cardAccountNumber": "03BUIN95611000576116",
                "cardStatus": "produced_not_received",
                "expiryDate": "2026-01-31",
                "cardPrintName": "EVGENIY RAEV",
                "cardOwner": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
                "currency": "BGN",
                "approvedOverdraft": 0,
                "cardId": "135P20723017088289",
                "cardSecret": "0003:card is not active"
            },
            {
                "accountId": "290010",
                "accountBalance": 7574.5,
                "availableBalance": 7569.5,
                "blockedAmount": 0,
                "cardProduct": "mastercard",
                "cardProductCode": "5168950",
                "cardProductLabel": "MasterCard Debit безконт.-Персонални",
                "cardNumber": "516895******7506",
                "cardBIN": "516895",
                "cardIIN": "",
                "cardType": "debit",
                "description": "",
                "cardAccountNumber": "03BUIN95611000576116",
                "cardStatus": "produced_not_received",
                "expiryDate": "2026-01-31",
                "cardPrintName": "TEST DEVICE",
                "cardOwner": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
                "currency": "BGN",
                "approvedOverdraft": 0,
                "cardId": "135P20723017088290",
                "cardSecret": "0003:card is not active"
            },
            {
                "accountId": "290011",
                "accountBalance": 10.89,
                "availableBalance": 510.89,
                "blockedAmount": 0,
                "cardProduct": "mastercard",
                "cardProductCode": "5200110",
                "cardProductLabel": "MasterCard Standard безконтактна-Персонални",
                "cardNumber": "520011******4281",
                "cardBIN": "520011",
                "cardIIN": "",
                "cardType": "credit",
                "description": "",
                "cardAccountNumber": "89BUIN95611000576120",
                "cardStatus": "active",
                "expiryDate": "2026-01-31",
                "cardPrintName": "BOYAN ANGELOV",
                "cardOwner": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
                "currency": "BGN",
                "approvedOverdraft": 500,
                "cardId": "135P21621029001429",
                "cardSecret": "011FD02574B8C3FD84E1513A14C366CCDB90AE0B7096208F48D3F46377F3719D"
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
