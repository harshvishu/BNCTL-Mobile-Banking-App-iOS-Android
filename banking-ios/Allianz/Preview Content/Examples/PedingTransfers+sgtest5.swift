//
//  PedingTransfers+sgtest5.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 18.11.22.
//

import Foundation

extension PendingTransfer {
    static var sgtest5:[PendingTransfer] {
        let rawData = Data(
        """
        [
            {
                "transferId": "2707494",
                "description": "вноска каско полица",
                "beneficiaryBankName": "АЛИАНЦ БАНК БЪЛГАРИЯ",
                "beneficiaryName": "зад алианц българия",
                "beneficiary": "зад алианц българия",
                "beneficiaryBankSwiftCode": "BUINBGSF",
                "currency": "BGN",
                "amount": 137.34,
                "sourceAccount": "BG69BUIN76041010005228",
                "issuerName": "БОЖИДАР ИВАНОВ ДИМИТРОВ",
                "destinationAccount": "BG51BUIN95611010006950",
                "status": "pending",
                "transactionType": "Кредитен превод"
            },
            {
                "transferId": "24554084",
                "currency": "BGN",
                "amount": 4.01,
                "sourceAccount": "BG69BUIN76041010005228",
                "status": "pending",
                "transactionType": "Масови Плащания (файл)",
                "numberOfDocuments": 3
            },
            {
                "transferId": "24554085",
                "currency": "BGN",
                "amount": 4.01,
                "sourceAccount": "BG69BUIN76041010005228",
                "status": "pending",
                "transactionType": "Масови Плащания (файл)",
                "numberOfDocuments": 3
            },
            {
                "transferId": "24554086",
                "currency": "BGN",
                "amount": 4.01,
                "sourceAccount": "BG69BUIN76041010005228",
                "status": "pending",
                "transactionType": "Масови Плащания (файл)",
                "numberOfDocuments": 3
            },
            {
                "transferId": "28490678",
                "description": "test",
                "beneficiaryBankName": "ЦЕНТРАЛНА КООПЕРАТИВНА БАНКА",
                "beneficiaryName": "test testov",
                "beneficiary": "test testov",
                "beneficiaryBankSwiftCode": "CECBBGSF",
                "currency": "BGN",
                "amount": 0.5,
                "sourceAccount": "BG69BUIN76041010005228",
                "issuerName": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                "destinationAccount": "BG12CECB97901065324001",
                "status": "pending",
                "transactionType": "Кредитен превод"
            }
        ]
        """.utf8)
        
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
