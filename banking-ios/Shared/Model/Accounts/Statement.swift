//
//  Statement.swift
//  Allianz
//
//  Created by Evgeniy Raev on 27.10.21.
//

import Foundation


struct StatementModel: Codable {
    
    let records: [Statement]
}

struct Statement: Codable, Identifiable {
    
    // IDs are the same for Fee operations.
    // When filtering large periods, there are transfers with the same ID, which break the Lists.
    // TODO: Ask the bank it's normal to have duplicate transferIds
    let id = UUID()
    let transactionType: String?
    let amount: Double
    let debitAmount: Double
    let creditAmount: Double
    let transferId: String
    let description: String?
    let additionalDescription: String?
    let valueDate: String
    let transferDate: String
    let balance: Double
    let closingBalance: Double
    let beneficiary: String?
    let currency: String
    let sourceAccount: String?
    let destinationAccount: String?

    static var preview:Statement {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
    
        return Statement(
            transactionType: "Получен кредитен превод-IB",
            amount: 100,
            debitAmount: 0,
            creditAmount: 100,
            transferId: "001FTWC210740001",
            description: "Получен кредитен превод-IB",
            additionalDescription: "additional",
            valueDate: "2021-03-15",
            transferDate: "2021-03-15 16:16:13",
//            valueDate: dateFormatter.date(from:"2021-03-15 00:00:00")!,
//            transferDate: dateFormatter.date(from: "2021-03-15 16:16:13")!,
            balance: 100,
            closingBalance: 100,
            beneficiary: "TID",
            currency: "BGN",
            sourceAccount: "BG76BUIN95611000333854",
            destinationAccount: nil
        )
    }
    
    static var list: [Statement] {
        let rawData = Data("""
            [
                {
                    "amount": -17.42,
                    "transactionType": "Плащане чрез ПОС чужбина ",
                    "debitAmount": 17.42,
                    "creditAmount": 0,
                    "beneficiary": "Chipotle.com Online Newport Beach US",
                    "transferId": "970ADV8202820006",
                    "description": "Chipotle.com Online Order Newport Beach US",
                    "valueDate": "2020-10-07",
                    "transferDate": "2020-10-08 12:28:07",
                    "balance": 86.01,
                    "closingBalance": 86.01,
                    "currency": "BGN",
                    "sourceAccount": "BG41BUIN95611000320419",
                    "transferReference": "970ADV8202820006"
                },
                {
                    "amount": 100,
                    "transactionType": "Получен кредитен превод-IB",
                    "debitAmount": 0,
                    "creditAmount": 100,
                    "beneficiary": "ГЕОРГИ АНГЕЛОВ ТОДОРОВ",
                    "transferId": "970FTWU202790005",
                    "description": "Захранване на сметка",
                    "valueDate": "2020-10-05",
                    "transferDate": "2020-10-05 07:55:06",
                    "balance": 103.43,
                    "closingBalance": 103.43,
                    "currency": "BGN",
                    "sourceAccount": "BG84BUIN70141030369918",
                    "transferReference": "970FTWU202790005",
                    "destinationAccount": "BG41BUIN95611000320419"
                },
                {
                    "amount": -2.2,
                    "transactionType": "Такса за поддръжка на сметка",
                    "debitAmount": 2.2,
                    "creditAmount": 0,
                    "beneficiary": "",
                    "transferId": "970CHMRBGNL00001",
                    "description": "Такса за поддръжка на сметка",
                    "valueDate": "2020-10-02",
                    "transferDate": "2020-10-02 00:00:00",
                    "balance": 3.43,
                    "closingBalance": 3.43,
                    "currency": "BGN",
                    "sourceAccount": "BG41BUIN95611000320419",
                    "transferReference": "970CHMRBGNL00001"
                },
                {
                    "amount": -0.3,
                    "transactionType": "Такса поддръжка карта/пакет Алианц Старт/Старт+",
                    "debitAmount": 0.3,
                    "creditAmount": 0,
                    "beneficiary": "",
                    "transferId": "970CDM2220591320",
                    "description": "Такса поддръжка карта/пакет Алианц Старт/Старт+",
                    "valueDate": "2022-02-28",
                    "transferDate": "2022-02-28 01:36:03",
                    "balance": 159.72,
                    "closingBalance": 159.72,
                    "currency": "BGN",
                    "sourceAccount": "BG41BUIN95611000320419",
                    "transferReference": "970CDM2220591320"
                },
                {
                    "amount": -56.19,
                    "transactionType": "Плащане чрез ПОС чужбина ",
                    "debitAmount": 56.19,
                    "creditAmount": 0,
                    "beneficiary": "WALGREENS",
                    "transferId": "970ADM8220820027",
                    "description": "WALGREENS #3468100 BROAD ST PAWTUCKET 02860 RI USABG459135043",
                    "valueDate": "2022-03-22",
                    "transferDate": "2022-03-23 10:22:49",
                    "balance": 2691.48,
                    "closingBalance": 2691.48,
                    "currency": "BGN",
                    "sourceAccount": "BG41BUIN95611000320419",
                    "transferReference": "970ADM8220820027"
                },
                {
                    "amount": -29.05,
                    "transactionType": "Плащане чрез ПОС чужбина ",
                    "debitAmount": 29.05,
                    "creditAmount": 0,
                    "beneficiary": "WALGREENS",
                    "transferId": "970ADM8220820024",
                    "description": "WALGREENS #3468 100 BROAD ST PAWTUCKET 02860 RI USABG459135043",
                    "valueDate": "2022-03-22",
                    "transferDate": "2022-03-23 10:20:20",
                    "balance": 2747.67,
                    "closingBalance": 2747.67,
                    "currency": "BGN",
                    "sourceAccount": "BG41BUIN95611000320419",
                    "transferReference": "970ADM8220820024"
                },
                {
                    "amount": -11.02,
                    "transactionType": "Плащане чрез ПОС чужбина ",
                    "debitAmount": 11.02,
                    "creditAmount": 0,
                    "beneficiary": "Lyft *Temp Auth Hold SAN FRANCISCO CA",
                    "transferId": "970ADM8220820014",
                    "description": "LYFT *RIDE SUN 6PM 185 BERRY STREET 855-865-9553 94107 CA USABG459135043",
                    "valueDate": "2022-03-22",
                    "transferDate": "2022-03-23 10:14:40",
                    "balance": 2776.72,
                    "closingBalance": 2776.72,
                    "currency": "BGN",
                    "sourceAccount": "BG41BUIN95611000320419",
                    "transferReference": "970ADM8220820014"
                },
                {
                    "amount": -29.89,
                    "transactionType": "Плащане чрез ПОС чужбина ",
                    "debitAmount": 29.89,
                    "creditAmount": 0,
                    "beneficiary": "Lyft *Temp Auth Hold SAN FRANCISCO CA",
                    "transferId": "970ADM8220340011",
                    "description": "LYFT *1 RIDE 01-31 185 BERRY STREET 855-865-9553 94107 CA USABG459135043",
                    "valueDate": "2022-02-02",
                    "transferDate": "2022-02-03 11:07:44",
                    "balance": 143.89,
                    "closingBalance": 143.89,
                    "currency": "BGN",
                    "sourceAccount": "BG41BUIN95611000320419",
                    "transferReference": "970ADM8220340011"
                }
            ]
            """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode([Statement].self, from: rawData) else {
            return []
        }
        
        return parsedData
    }
}

extension Statement: PaymentDetails {

    var status: String {
        ""
    }

}
