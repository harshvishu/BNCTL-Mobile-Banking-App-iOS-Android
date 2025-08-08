//
//  UtilityBillPaymentExample.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 3.11.22.
//

import Foundation

extension UtilityBillPayment {
    
    static var preview: UtilityBillPayment? {
        let rawData = Data("""
        {
            "userBillerId": "1571519",
            "biller": {
                "billerId": "0000100",
                "name": "A1",
                "type": "Service",
                "subType": "Стационарни и мобилни оператори",
                "identifier": "0000100",
                "description": "81"
            },
            "userId": "69792",
            "name": "Мобилен телефон",
            "clientReference": "3212",
            "billAmount": 0.01,
            "currencyName": "BGN"
        }
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode(UtilityBillPayment.self, from: rawData) else {
            return nil
        }
        
        return parsedData
    }
    
    static var listPreview: [UtilityBillPayment] {
        let rawData = Data("""
        [
            {
                "userBillerId": "1571519",
                "biller": {
                    "billerId": "0000100",
                    "name": "A1",
                    "type": "Service",
                    "subType": "Стационарни и мобилни оператори",
                    "identifier": "0000100",
                    "description": "81"
                },
                "userId": "69792",
                "name": "Мобилен телефон",
                "clientReference": "3212",
                "billAmount": 137.24,
                "currencyName": "BGN"
            },
            {
                "userBillerId": "1571520",
                "biller": {
                    "billerId": "0001311",
                    "name": "АЕЦ Козлодуй",
                    "type": "Service",
                    "subType": "Топлофикационни дружества",
                    "identifier": "0001311",
                    "description": "85"
                },
                "userId": "69792",
                "name": "Парното на село",
                "clientReference": "123123",
                "billAmount": 0,
                "currencyName": "BGN"
            },
            {
                "userBillerId": "1571525",
                "biller": {
                    "billerId": "0001666",
                    "name": "КРЕМЪК",
                    "type": "Service",
                    "subType": "Сигнално охранителна техника и услуги",
                    "identifier": "0001666",
                    "description": "86"
                },
                "userId": "69792",
                "name": "Охрана на вилата",
                "clientReference": "1232131",
                "billAmount": 50.45,
                "currencyName": "BGN"
            },
            {
                "userBillerId": "1571521",
                "biller": {
                    "billerId": "0001311",
                    "name": "АЕЦ Козлодуй",
                    "type": "Service",
                    "subType": "Топлофикационни дружества",
                    "identifier": "0001311",
                    "description": "85"
                },
                "userId": "69792",
                "name": "Парното в апартамента",
                "clientReference": "123123",
                "billAmount": 0,
                "currencyName": "BGN",
                "status": "errBillDueAmountRetrieval"
            }
        ]
        """.utf8)
        
        do {
            let parsedData = try JSONDecoder().decode([UtilityBillPayment].self, from: rawData)
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
