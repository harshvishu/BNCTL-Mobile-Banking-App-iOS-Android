//
//  PendingTransfersEDG.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 17.11.22.
//

import Foundation

extension PendingTransfer {
    static var edg:[PendingTransfer] {
        let rawData = Data(
        """
        [
            {
                "transferId": "24553686",
                "currency": "BGN",
                "amount": 1000,
                "sourceAccount": "BG51BUIN95611000662649",
                "status": "pending",
                "transactionType": "Масови Плащания (файл)",
                "numberOfDocuments": 1000
            },
            {
                "transferId": "28485757",
                "beneficiaryBankName": "ИНВЕСТБАНК кл. ВАРНА",
                "beneficiaryName": "TEST",
                "beneficiary": "TEST",
                "beneficiaryBankSwiftCode": "IORTBGSF",
                "currency": "BGN",
                "amount": 0.01,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                "destinationAccount": "BG03IORT73778600360000",
                "status": "pending",
                "transactionType": "Плащане от/към бюджета"
            },
            {
                "transferId": "28485758",
                "beneficiaryBankName": "ИНВЕСТБАНК кл. ВАРНА",
                "beneficiaryName": "TEST",
                "beneficiary": "TEST",
                "beneficiaryBankSwiftCode": "IORTBGSF",
                "currency": "BGN",
                "amount": 0.01,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                "destinationAccount": "BG03IORT73778600360000",
                "status": "pending",
                "transactionType": "Плащане от/към бюджета"
            },
            {
                "transferId": "28485759",
                "description": "Кафе-Illy Tostatura Media ",
                "beneficiaryBankName": "АЛИАНЦБАНК Б-Я кл Мария Луиза",
                "beneficiaryName": "Боряна Везева",
                "beneficiary": "Боряна Везева",
                "beneficiaryBankSwiftCode": "BUINBGSF",
                "currency": "BGN",
                "amount": 16.2,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "ЕКАТЕРИНА ДРАГОМИРОВА ГЕКОВА",
                "destinationAccount": "BG29BUIN76041011213210",
                "status": "pending",
                "transactionType": "Кредитен превод"
            },
            {
                "transferId": "28485760",
                "description": "Славка Вълкова (подарък)",
                "beneficiaryBankName": "АЛИАНЦ БАНК БЪЛГАРИЯ",
                "beneficiaryName": "МИРОСЛАВА ГЕНКОВА ИВАНОВА",
                "beneficiary": "МИРОСЛАВА ГЕНКОВА ИВАНОВА",
                "beneficiaryBankSwiftCode": "BUINBGSF",
                "currency": "BGN",
                "amount": 10,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "ЕКАТЕРИНА ДРАГОМИРОВА ГЕКОВА",
                "destinationAccount": "BG69BUIN95611000445724",
                "status": "pending",
                "transactionType": "Кредитен превод"
            },
            {
                "transferId": "28485761",
                "description": "Захранване",
                "beneficiaryBankName": "РАЙФАЙЗЕНБАНК",
                "beneficiaryName": "Екатерина Гекова",
                "beneficiary": "Екатерина Гекова",
                "beneficiaryBankSwiftCode": "RZBBBGSF",
                "currency": "BGN",
                "amount": 1,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "ЕКАТЕРИНА ДРАГОМИРОВА ГЕКОВА",
                "destinationAccount": "BG57RZBB91551007918480",
                "status": "pending",
                "transactionType": "Кредитен превод"
            },
            {
                "transferId": "28485762",
                "description": "test IB",
                "beneficiaryBankName": "ОББ -  КЛ. МИЛЕНИУМ",
                "beneficiaryName": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                "beneficiary": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                "beneficiaryBankSwiftCode": "BUINBGSF",
                "currency": "BGN",
                "amount": 5.01,
                "sourceAccount": "BG72UBBS88888188842600",
                "issuerName": "YESYT",
                "destinationAccount": "BG51BUIN95611000662649",
                "status": "pending",
                "transactionType": "Директен дебит"
            },
            {
                "transferId": "28485769",
                "beneficiaryBankName": "ИНВЕСТБАНК кл. ВАРНА",
                "beneficiaryName": "TEST",
                "beneficiary": "TEST",
                "beneficiaryBankSwiftCode": "IORTBGSF",
                "currency": "BGN",
                "amount": 8.01,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                "destinationAccount": "BG03IORT73778600360000",
                "status": "pending",
                "transactionType": "Плащане от/към бюджета"
            },
            {
                "transferId": "28485770",
                "beneficiaryBankName": "ИНВЕСТБАНК кл. ВАРНА",
                "beneficiaryName": "TEST",
                "beneficiary": "TEST",
                "beneficiaryBankSwiftCode": "IORTBGSF",
                "currency": "BGN",
                "amount": 9.01,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                "destinationAccount": "BG03IORT73778600360000",
                "status": "pending",
                "transactionType": "Плащане от/към бюджета"
            },
            {
                "transferId": "28490983",
                "description": "test",
                "beneficiaryName": "test",
                "beneficiary": "test",
                "currency": "EUR",
                "amount": 0.01,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "EKATERINA DRAGOMIROVA GEKOVA",
                "destinationAccount": "UA193545070000026001501156431",
                "status": "pending",
                "transactionType": "Междубанков валутен превод (SWIFT)"
            },
            {
                "transferId": "28490985",
                "beneficiaryBankName": "ИНВЕСТБАНК кл. ВАРНА",
                "beneficiaryName": "TEST",
                "beneficiary": "TEST",
                "beneficiaryBankSwiftCode": "IORTBGSF",
                "currency": "BGN",
                "amount": 0.01,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                "destinationAccount": "BG03IORT73778600360000",
                "status": "pending",
                "transactionType": "Плащане от/към бюджета"
            },
            {
                "transferId": "28491000",
                "description": "narr1",
                "beneficiaryName": "Valuten v evro",
                "beneficiary": "Valuten v evro",
                "currency": "EUR",
                "amount": 13.13,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "EKATERINA DRAGOMIROVA GEKOVA",
                "destinationAccount": "DE90600501010405492677",
                "status": "pending",
                "transactionType": "Междубанков превод в EUR (SEPA, BISERA7)"
            },
            {
                "transferId": "28491001",
                "beneficiaryBankName": "ИНВЕСТБАНК кл. ВАРНА",
                "beneficiaryName": "TEST budget",
                "beneficiary": "TEST budget",
                "beneficiaryBankSwiftCode": "IORTBGSF",
                "currency": "BGN",
                "amount": 8.88,
                "sourceAccount": "BG51BUIN95611000662649",
                "issuerName": "ХАРАЛАМПИ ИВАНОВ ПЕТРОВ",
                "destinationAccount": "BG03IORT73778600360000",
                "status": "pending",
                "transactionType": "Плащане от/към бюджета"
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
