//
//  TransferTemplateExample.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 1.11.22.
//

import Foundation

extension TransferTemplate {
    static var preview: TransferTemplate? {
        let rawData = Data("""
        {
           "templateId": "1571483",
           "templateName": "Пепо",
           "amount": 3.14,
           "description": "Макети",
           "sourceAccount": "BG88BUIN95611000677309",
           "sourceAccountCurrency": "BGN",
           "sourceAccountHolder": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
           "destinationAccount": "BG02BUIN95611000676723",
           "destinationAccountCurrency": "BGN",
           "destinationAccountHolder": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
           "transactionType": "BISERA",
           "transferType": "interbank"
        }
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode(TransferTemplate.self, from: rawData) else {
            return nil
        }
        
        return parsedData
    }
    
    static var previewList:[TransferTemplate] {
        let rawData = Data("""
        [
                {
                    "templateId": "1571631",
                    "templateName": "Rings",
                    "amount": 100,
                    "description": "освование",
                    "sourceAccount": "BG82BUIN95611000429776",
                    "sourceAccountCurrency": "BGN",
                    "sourceAccountHolder": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                    "destinationAccount": "BG16BUIN95611000314758",
                    "destinationAccountCurrency": "BGN",
                    "destinationAccountHolder": "dfdgdg rgdfgdfg",
                    "transactionType": "RINGS",
                    "transferType": "interbank",
                    "additionalDetails": {
                        "type": "",
                        "preferentialRatesPin": ""
                    }
                },
                {
                    "templateId": "1571666",
                    "templateName": "test",
                    "amount": 0.01,
                    "description": "test",
                    "sourceAccount": "BG82BUIN95611000429776",
                    "sourceAccountCurrency": "BGN",
                    "sourceAccountHolder": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                    "destinationAccount": "BG88BUIN95614000677401",
                    "destinationAccountCurrency": "BGN",
                    "destinationAccountHolder": "НИКОЛАЙ СВЕТЛИНОВ ПРЕСОЛСКИ",
                    "transactionType": "BISERA",
                    "transferType": "betweenacc",
                    "additionalDetails": {
                        "type": "",
                        "preferentialRatesPin": ""
                    }
                },
                {
                    "templateId": "1571478",
                    "templateName": "Макет",
                    "amount": 100,
                    "description": "освование",
                    "sourceAccount": "BG82BUIN95611000429776",
                    "sourceAccountCurrency": "BGN",
                    "sourceAccountHolder": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                    "destinationAccount": "BG16BUIN95611000314758",
                    "destinationAccountCurrency": "BGN",
                    "destinationAccountHolder": "dfdgdg rgdfgdfg",
                    "transactionType": "BISERA",
                    "transferType": "interbank",
                    "additionalDetails": {
                        "type": "",
                        "preferentialRatesPin": ""
                    }
                },
                {
                    "templateId": "1571479",
                    "templateName": "Макет2",
                    "amount": 120,
                    "description": "освованиеe",
                    "sourceAccount": "BG34BUIN95611000677311",
                    "sourceAccountCurrency": "BGN",
                    "sourceAccountHolder": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                    "destinationAccount": "BG82BUIN95611000429776",
                    "destinationAccountCurrency": "BGN",
                    "destinationAccountHolder": "Джулиана Костолова",
                    "transactionType": "BISERA",
                    "transferType": "betweenacc",
                    "additionalDetails": {
                        "type": "",
                        "preferentialRatesPin": ""
                    }
                },
                {
                    "templateId": "1571641",
                    "templateName": "МакетВалута",
                    "amount": 10,
                    "description": "освование",
                    "sourceAccount": "BG22BUIN95611000677333",
                    "sourceAccountCurrency": "EUR",
                    "sourceAccountHolder": "ХАРАЛАМПИ ИВАНОВ ХАРАЛАМПИЕВ",
                    "destinationAccount": "BG72BUIN95611000662659",
                    "destinationAccountCurrency": "EUR",
                    "destinationAccountHolder": "dfdgdg rgdfgdfg",
                    "transactionType": null,
                    "transferType": "intrabank",
                    "additionalDetails": {
                        "type": "",
                        "preferentialRatesPin": ""
                    }
                },
                {
                    "templateId": "1571674",
                    "templateName": "валута",
                    "amount": 10,
                    "description": "",
                    "sourceAccount": "BG82BUIN95611000429776",
                    "sourceAccountCurrency": "BGN",
                    "sourceAccountHolder": "",
                    "destinationAccount": "BG22BUIN95611000677333",
                    "destinationAccountCurrency": "EUR",
                    "destinationAccountHolder": "",
                    "transactionType": "foreignExchange",
                    "transferType": "betweenacc",
                    "additionalDetails": {
                        "type": "buy",
                        "preferentialRatesPin": ""
                    }
                }
            ]
        """.utf8)
        
        do {
            let parsedData = try JSONDecoder().decode([TransferTemplate].self, from: rawData)
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
