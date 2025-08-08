//
//  DocumentsHistoryModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 02/02/22.
//

import Foundation

class DocumentsHistoryData: Codable, Identifiable {
 
    let transferId: String
    let amount: Double
    let transferDate: String
    let status: String
    let currency: String
    let valueDate: String
    let issuerName: String?
    let transferIdIssuer: String
    let sourceAccount: String?
    let destinationAccount: String?
    let beneficiary: String?
    let description: String?
    let additionalDescription: String?
    let processType: String?
    let beneficiaryBankName: String?
    let beneficiaryBankSwiftCode: String?
    
    static var previewList: [DocumentsHistoryData] {
        let rawData = Data("""
            [
                {
                  "valueDate": "2021-11-25 09:04:46",
                  "transferDate": "2021-11-25 09:04:46",
                  "issuerName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "transferIdIssuer": "",
                  "transferId": "24414939",
                  "sourceAccount": "BG42BUIN95611000254053",
                  "destinationAccount": "BG88BUIN95611000677309",
                  "beneficiary": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "description": "Transfer",
                  "processType": "realtime",
                  "status": "error",
                  "amount": -10.00,
                  "currency": "BGN",
                  "beneficiaryBankName": "",
                  "beneficiaryBankSwiftCode": ""
                },
                {
                  "valueDate": "2021-11-25 08:43:46",
                  "transferDate": "2021-11-25 08:43:46",
                  "issuerName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "transferIdIssuer": "",
                  "transferId": "24414938",
                  "sourceAccount": "BG42BUIN95611000254053",
                  "destinationAccount": "BG88BUIN95611000677309",
                  "beneficiary": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "description": "Transfer test",
                  "processType": "realtime",
                  "status": "error",
                  "amount": -1.00,
                  "currency": "BGN",
                  "beneficiaryBankName": "",
                  "beneficiaryBankSwiftCode": ""
                },
                {
                  "valueDate": "2021-11-25 08:42:16",
                  "transferDate": "2021-11-25 08:42:16",
                  "issuerName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "transferIdIssuer": "",
                  "transferId": "24414937",
                  "sourceAccount": "BG42BUIN95611000254053",
                  "destinationAccount": "BG88BUIN95611000677309",
                  "beneficiary": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "description": "Transfer",
                  "processType": "realtime",
                  "status": "error",
                  "amount": -1.00,
                  "currency": "BGN",
                  "beneficiaryBankName": "",
                  "beneficiaryBankSwiftCode": ""
                },
                {
                  "valueDate": "2021-11-25 08:37:56",
                  "transferDate": "2021-11-25 08:37:56",
                  "issuerName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "transferIdIssuer": "",
                  "transferId": "24414936",
                  "sourceAccount": "BG42BUIN95611000254053",
                  "destinationAccount": "BG88BUIN95611000677309",
                  "beneficiary": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "description": "Transfer",
                  "processType": "realtime",
                  "status": "error",
                  "amount": -1.00,
                  "currency": "BGN",
                  "beneficiaryBankName": "",
                  "beneficiaryBankSwiftCode": ""
                },
                {
                  "valueDate": "2021-11-24 13:45:36",
                  "transferDate": "2021-11-24 13:45:36",
                  "issuerName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "transferIdIssuer": "",
                  "transferId": "24414933",
                  "sourceAccount": "BG42BUIN95611000254053",
                  "destinationAccount": "BG88BUIN95611000677309",
                  "beneficiary": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "description": "Saving",
                  "processType": "realtime",
                  "status": "error",
                  "amount": -5.00,
                  "currency": "BGN",
                  "beneficiaryBankName": "",
                  "beneficiaryBankSwiftCode": ""
                },
                {
                  "valueDate": "2021-11-24 10:25:22",
                  "transferDate": "2021-11-24 10:25:22",
                  "issuerName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                  "transferIdIssuer": "",
                  "transferId": "24414919",
                  "sourceAccount": "BG42BUIN95611000254053",
                  "destinationAccount": "BG88BUIN95611000677309",
                  "beneficiary": "Харалампи Иванов Харалампиев",
                  "description": "Захранване сметка",
                  "processType": "realtime",
                  "status": "error",
                  "amount": -2.00,
                  "currency": "BGN",
                  "beneficiaryBankName": "",
                  "beneficiaryBankSwiftCode": ""
                }
              ]
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode([DocumentsHistoryData].self, from: rawData) else {
            return []
        }
        
        return parsedData
    }
}

extension DocumentsHistoryData: PaymentDetails {

    var transactionType: String? {
        nil
    }

}
