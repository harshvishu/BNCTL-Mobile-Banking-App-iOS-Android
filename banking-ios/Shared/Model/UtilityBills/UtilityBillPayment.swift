//
//  UtilityBillsModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 22/12/21.
//

import Foundation

struct UtilityBillPayment: Codable, Identifiable, Hashable {

    var id: String { self.userBillerId }
    
    let userBillerId: String
    let userId: String
    let name: String
    let clientReference: String
    let billAmount: String
    let currencyName: String
    let biller: Biller
    
    struct Biller: Codable, Hashable {
        let billerId: String
        let name: String
        let type: String
        let subType: String
        let identifier: String
        let description: String
    }
    
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
            "name": "ох боли ме ",
            "clientReference": "3212",
            "billAmount": "0.01",
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
                "name": "ох боли ме ",
                "clientReference": "3212",
                "billAmount": "0.01",
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
                "name": "my dealer",
                "clientReference": "123123",
                "billAmount": "0.01",
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
                "name": "мутрите",
                "clientReference": "1232131",
                "billAmount": "0.01",
                "currencyName": "BGN"
            }
        ]
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode([UtilityBillPayment].self, from: rawData) else {
            return []
        }
        
        return parsedData
    }
}
