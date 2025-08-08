//
//  IvaDraganova.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 22.03.22.
//

import Foundation

extension UtilityBillPayment {
    
    static var ivadraganova: [UtilityBillPayment] {
        let rawData = Data("""
        [
            {
                "userBillerId": "1384237",
                "biller": {
                    "billerId": "0000100",
                    "name": "A1 Balgaria EAD",
                    "type": "Service",
                    "subType": "Стационарни и мобилни оператори",
                    "identifier": "0000100",
                    "description": "81"
                },
                "userId": "138239",
                "name": "A1 0886611313",
                "clientReference": "08866113131950",
                "billAmount": 0,
                "currencyName": "BGN",
                "status": "errBillsGeneralError"
            },
            {
                "userBillerId": "1384244",
                "biller": {
                    "billerId": "0000120",
                    "name": "TELENOR BALGARIA EAD",
                    "type": "Service",
                    "subType": "Стационарни и мобилни оператори",
                    "identifier": "0000120",
                    "description": "81"
                },
                "userId": "138239",
                "name": "Теленор Кирчо",
                "clientReference": "000095010",
                "billAmount": 33.65,
                "currencyName": "BGN",
                "status": "billsDueStatusOK"
            },
            {
                "userBillerId": "1384240",
                "biller": {
                    "billerId": "0000040",
                    "name": "CEZ Electro Bulgaria",
                    "type": "Service",
                    "subType": "Електроснабдителни дружества",
                    "identifier": "0000040",
                    "description": "82"
                },
                "userId": "138239",
                "name": "ЧЕЗ 3-фазен Симеоново",
                "clientReference": "310197950583",
                "billAmount": 73.77,
                "currencyName": "BGN",
                "status": "billsDueStatusOK"
            },
            {
                "userBillerId": "1384241",
                "biller": {
                    "billerId": "0000040",
                    "name": "CEZ Electro Bulgaria",
                    "type": "Service",
                    "subType": "Електроснабдителни дружества",
                    "identifier": "0000040",
                    "description": "82"
                },
                "userId": "138239",
                "name": "ЧЕЗ Овче поле",
                "clientReference": "300068503869",
                "billAmount": 0.19,
                "currencyName": "BGN",
                "status": "billsDueStatusOK"
            },
            {
                "userBillerId": "1383981",
                "biller": {
                    "billerId": "0000040",
                    "name": "CEZ Electro Bulgaria",
                    "type": "Service",
                    "subType": "Електроснабдителни дружества",
                    "identifier": "0000040",
                    "description": "82"
                },
                "userId": "138239",
                "name": "ЧЕЗ Симеоново 310197950682",
                "clientReference": "310197950682",
                "billAmount": 94.42,
                "currencyName": "BGN",
                "status": "billsDueStatusOK"
            },
            {
                "userBillerId": "1384243",
                "biller": {
                    "billerId": "1000348",
                    "type": "Service",
                    "subType": "Кабелна и сателитна телевизия",
                    "identifier": "1000348",
                    "description": "84"
                },
                "userId": "138239",
                "name": "Близу",
                "clientReference": "90018091689",
                "billAmount": 0,
                "currencyName": "BGN",
                "status": "errBillsGeneralError"
            },
            {
                "userBillerId": "1383979",
                "biller": {
                    "billerId": "0000701",
                    "name": "Overgaz Mrezhi AD",
                    "type": "Service",
                    "subType": "Природен газ",
                    "identifier": "0000701",
                    "description": "87"
                },
                "userId": "138239",
                "name": "овъргаз",
                "clientReference": "1000039450",
                "billAmount": 99.95,
                "currencyName": "BGN",
                "status": "billsDueStatusOK"
            },
            {
                "userBillerId": "1384245",
                "biller": {
                    "billerId": "0000130",
                    "name": "\\"Sofiyska voda\\" AD",
                    "type": "Service",
                    "subType": "ВиК дружества",
                    "identifier": "0000130",
                    "description": "88"
                },
                "userId": "138239",
                "name": "Софийска вода Овче поле",
                "clientReference": "5077565",
                "billAmount": 5.04,
                "currencyName": "BGN",
                "status": "billsDueStatusOK"
            },
            {
                "userBillerId": "1384242",
                "biller": {
                    "billerId": "0000130",
                    "name": "\\"Sofiyska voda\\" AD",
                    "type": "Service",
                    "subType": "ВиК дружества",
                    "identifier": "0000130",
                    "description": "88"
                },
                "userId": "138239",
                "name": "Софийска вода Симеоново",
                "clientReference": "5271830",
                "billAmount": 17.65,
                "currencyName": "BGN",
                "status": "billsDueStatusOK"
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
