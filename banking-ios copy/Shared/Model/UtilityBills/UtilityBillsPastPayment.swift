//
//  UtilityBillsPastPayment.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 11.01.22.
//

import Foundation

struct UtilityBillsHistoryModel: Codable {
    
    let records: [UtilityBillsHistory]
}

struct UtilityBillsHistory: Codable, Identifiable, Hashable {
    
    var id: String { self.billPaymentId }
    let amount: Double
    let status: String
    let billPaymentDate: String
    let currency: String
    let billPaymentId: String
    let billerId: String
    let sourceAccount: String?
    let transferId: String
    let paymentProcessedDateTime: String
    let reference: String?
    let creatorUserId: String
    let customerText: String
    let additionalDescription:String?
    let creatorName: String
    let billerName: String?
    let destinationAccount: String?
    let provider: String
    
    static var preview: UtilityBillsHistory? {
        let rawData = Data("""
        {
            "billPaymentId": "24416020",
            "billerId": "0001311",
            "sourceAccount": "BG88BUIN95611000677309",
            "transferId": "24416020",
            "billPaymentDate": "2021-12-22 15:39:45",
            "paymentProcessedDateTime": "2021-12-22 15:39:45",
            "amount": -0.01,
            "currency": "BGN",
            "reference": "123123",
            "creatorUserId": "",
            "status": "Платено",
            "customerText": "MID#0001311  AEC Kozlodui SNO#123123",
            "creatorName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
            "billerName": "my dealer",
            "billerType": "Service"
        }
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode(UtilityBillsHistory.self, from: rawData) else {
            return nil
        }
        
        return parsedData
    }
    
    static var listPreview:[UtilityBillsHistory] {
        let rawData = Data("""
        [
            {
                "billPaymentId": "24416235",
                "billerId": "0001311",
                "sourceAccount": "BG88BUIN95611000677309",
                "transferId": "24416235",
                "billPaymentDate": "2022-01-17 06:24:16",
                "paymentProcessedDateTime": "2022-01-17 06:24:16",
                "amount": -1520.56,
                "currency": "BGN",
                "reference": "123123",
                "creatorUserId": "",
                "status": "posted",
                "customerText": "MID#0001311  АЕЦ Козлодуй SNO#123123",
                "creatorName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                "billerName": "Парното на село",
                "billerType": "Service",
                "destinationAccount": "BG09BUIN95611900260611",
                "provider": "АЕЦ Козлодуй"
            },
            {
                "billPaymentId": "24416204",
                "billerId": "0000100",
                "sourceAccount": "BG88BUIN95611000677309",
                "transferId": "24416204",
                "billPaymentDate": "2022-01-17 06:24:15",
                "paymentProcessedDateTime": "2022-01-17 06:24:15",
                "amount": -50.25,
                "currency": "BGN",
                "reference": "3212",
                "creatorUserId": "",
                "status": "posted",
                "customerText": "MID#0000100  A1 SNO#3212",
                "creatorName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                "billerName": "Мобилен телефон",
                "billerType": "Service",
                "destinationAccount": "BG09BUIN95611900260611",
                "provider": "A1"
            },
            {
                "billPaymentId": "24416020",
                "billerId": "0001311",
                "sourceAccount": "BG88BUIN95611000677309",
                "transferId": "24416020",
                "billPaymentDate": "2022-01-08 11:13:29",
                "paymentProcessedDateTime": "2022-01-08 11:13:29",
                "amount": -26.30,
                "currency": "BGN",
                "reference": "123123",
                "creatorUserId": "",
                "status": "posted",
                "customerText": "MID#0001311  AEC Kozlodui SNO#123123",
                "creatorName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                "billerName": "Още едно минало плащане",
                "billerType": "Service",
                "destinationAccount": "BG09BUIN95611900260611",
                "provider": "AEC Kozlodui"
            },
            {
                "billPaymentId": "24416018",
                "billerId": "0000100",
                "sourceAccount": "BG88BUIN95611000677309",
                "transferId": "24416018",
                "billPaymentDate": "2022-01-08 11:13:29",
                "paymentProcessedDateTime": "2022-01-08 11:13:29",
                "amount": -105.32,
                "currency": "BGN",
                "reference": "3212",
                "creatorUserId": "",
                "status": "posted",
                "customerText": "MID#0000100  A1 SNO#23412",
                "creatorName": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                "billerName": "Мобилен телефон",
                "billerType": "Service",
                "destinationAccount": "BG09BUIN95611900260611",
                "provider": "A1"
            }
        ]
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode([UtilityBillsHistory].self, from: rawData) else {
            return []
        }
        
        return parsedData
    }
}

extension UtilityBillsHistory: PaymentDetails {
    var transferDate: String {
        billPaymentDate
    }
    
    var beneficiary: String? {
        billerName
    }
    
    var description: String? {
        customerText
    }

    var transactionType: String? {
        nil
    }

}
