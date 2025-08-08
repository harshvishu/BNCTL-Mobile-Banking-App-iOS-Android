//
//  PendingTransfeExample.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 17.11.22.
//

import Foundation

extension PendingTransfer {
   static var preview: PendingTransfer? {
        let rawData = Data("""
        {
            "transferId": "24415033",
            "description": "description",
            "beneficiaryBankName": "АЛИАНЦ БАНК БЪЛГАРИЯ",
            "beneficiaryName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
            "beneficiary": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
            "beneficiaryBankSwiftCode": "BUINBGSF",
            "currency": "BGN",
            "amount": 100,
            "sourceAccount": "BG88BUIN95611000677309",
            "issuerName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
            "destinationAccount": "BG06BUIN95611000677330",
            "status": "pending",
            "transactionType": "Кредитен превод"
        }
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode(PendingTransfer.self, from: rawData) else {
            return nil
        }
        
        return parsedData
    }
    
    static var previewList:[PendingTransfer] {
        let rawData = Data(#"""
        [
            {
                "transferId": "28998399",
                "description": "a",
                "additionalDescription": "b",
                "beneficiaryBankName": "АЛИАНЦ БАНК БЪЛГАРИЯ",
                "beneficiaryName": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
                "beneficiary": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
                "beneficiaryBankSwiftCode": "BUINBGSF",
                "currency": "BGN",
                "amount": 1,
                "sourceAccount": "BG03BUIN95611000576116",
                "issuerName": "ТРЕНДАФИЛ ИВАНОВ БОДИЛОВ",
                "destinationAccount": "BG89BUIN95611000576120",
                "status": "pending",
                "transactionType": "Кредитен превод"
            },
            {
                "transferId": "28998632",
                "description": "ROW 1",
                "additionalDescription": "ROW 2\nROW 3\nROW 4",
                "beneficiaryName": "dadadada",
                "beneficiary": "dadadada",
                "currency": "EUR",
                "amount": 20,
                "sourceAccount": "BG03BUIN95611000576116",
                "issuerName": "TRENDAFIL IVANOV BODILOV",
                "destinationAccount": "BG38STSA93000023990901",
                "status": "pending",
                "transactionType": "Междубанков валутен превод (SWIFT)"
            },
            {
                "transferId": "28998901",
                "description": "details row 1",
                "additionalDescription": "details row 2\ndetails row 3\ndetails row 4",
                "beneficiaryName": "Test Testvo",
                "beneficiary": "Test Testvo",
                "currency": "GBP",
                "amount": 20,
                "sourceAccount": "BG03BUIN95611000576116",
                "issuerName": "TRENDAFIL IVANOV BODILOV",
                "destinationAccount": "DE75512108001245126199",
                "status": "pending",
                "transactionType": "Междубанков валутен превод (SWIFT)"
            },
            {
                "transferId": "28998904",
                "description": "details row 1",
                "additionalDescription": "details row 2\ndetails row 3\ndetails row 4",
                "beneficiaryName": "name",
                "beneficiary": "name",
                "currency": "JPY",
                "amount": 1000,
                "sourceAccount": "BG03BUIN95611000576116",
                "issuerName": "TRENDAFIL IVANOV BODILOV",
                "destinationAccount": "DE75512108001245126199",
                "status": "pending",
                "transactionType": "Междубанков валутен превод (SWIFT)"
            }
        ]
        """#.utf8)
        
        do {
            let parsedData = try JSONDecoder().decode([PendingTransfer].self, from: rawData)
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
