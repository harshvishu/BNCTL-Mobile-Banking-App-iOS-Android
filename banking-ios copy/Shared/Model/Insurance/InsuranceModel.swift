//
//  InsuranceModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 08/12/21.
//

import Foundation

struct InsuranceModel: Codable {
    
    let result: [InsuranceData]?
    
}

struct InsuranceData: Codable, Identifiable {
    
    let policy: String
    let dueDate: String?
    let amount: String
    let currency: String
    let amountBgn: String?
    let insuranceAgencyName: String?
    let iban: String?
    let ibanBgn: String?
    let insurer: String?
    let reason: String?
    let billNumber: String?
    
    var id:String { self.policy }
    
    static var preview: InsuranceData? {
        let rawData = Data("""
        {
            "policy": "5062000404",
            "dueDate": "08/11/2015",
            "amount": "307.00",
            "currency": "EUR",
            "iban": "BG16BUIN95611410002115",
            "amountBgn": "600.44",
            "ibanBgn": "BG72BUIN95611010002110",
            "insurer": "Evgeniy Raev",
            "insuranceAgencyName": "Алианц България Живот",
            "reason": "LF:10905:08112015"
        }
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode(InsuranceData.self, from: rawData) else {
                
                return nil
        }
        
        return parsedData
    }
    
    static var listPreview: [InsuranceData] {
        let rawData = Data("""
        [
                {
                    "policy": "5062000404",
                    "dueDate": "08/11/2015",
                    "amount": "307.00",
                    "currency": "EUR",
                    "iban": "BG16BUIN95611410002115",
                    "amountBgn": "600.44",
                    "ibanBgn": "BG72BUIN95611010002110",
                    "insurer": "Evgeniy Raev",
                    "insuranceAgencyName": "Алианц България Живот",
                    "reason": "LF:10905:08112015"
                },
                {
                    "policy": "5062000412",
                    "dueDate": "13/11/2021",
                    "amount": "240.00",
                    "currency": "EUR",
                    "iban": "BG16BUIN95611410002115",
                    "amountBgn": "469.40",
                    "ibanBgn": "BG72BUIN95611010002110",
                    "insurer": "Evgeniy Raev",
                    "insuranceAgencyName": "Алианц България Живот",
                    "reason": "LF:11048:13112021"
                },
                {
                    "policy": "5062000521",
                    "dueDate": "22/12/2013",
                    "amount": "240.00",
                    "currency": "EUR",
                    "iban": "BG16BUIN95611410002115",
                    "amountBgn": "469.40",
                    "ibanBgn": "BG90BUIN76041010299924",
                    "insurer": "Evgeniy Raev",
                    "insuranceAgencyName": "Алианц България Живот",
                    "reason": "LF:12795:22122013"
                },
                {
                    "policy": "20-0300/411/5001121",
                    "dueDate": "30/05/2021",
                    "amount": "155.40",
                    "currency": "EUR",
                    "amountBgn": "303.94",
                    "ibanBgn": "BG51BUIN95611010006950",
                    "insurer": "Evgeniy Raev",
                    "insuranceAgencyName": "ЗАД Алианц България",
                    "reason": "MO/3625528/4",
                    "billNumber": "4"
                },
                {
                    "policy": "20-0300/170/5000884",
                    "dueDate": "27/06/2021",
                    "amount": "292.48",
                    "currency": "BGN",
                    "amountBgn": "292.48",
                    "ibanBgn": "BG26BUIN70071005008618",
                    "insurer": "Evgeniy Raev",
                    "insuranceAgencyName": "ЗАД Алианц България",
                    "reason": "MO/3652577/4",
                    "billNumber": "4"
                },
                {
                    "policy": "20-0300/302/5002002",
                    "dueDate": "08/06/2021",
                    "amount": "117.85",
                    "currency": "EUR",
                    "amountBgn": "230.49",
                    "ibanBgn": "BG51BUIN95611010006950",
                    "insurer": "SGTESTMB6",
                    "insuranceAgencyName": "ЗАД Алианц България",
                    "reason": "MO/3689897/3",
                    "billNumber": "3"
                },
                {
                    "policy": "21-0300/302/5000582",
                    "dueDate": "13/04/2021",
                    "amount": "97.94",
                    "currency": "EUR",
                    "amountBgn": "191.55",
                    "ibanBgn": "BG51BUIN95611010006950",
                    "insurer": "SGTESTMB6",
                    "insuranceAgencyName": "ЗАД Алианц България",
                    "reason": "MO/3769354/1",
                    "billNumber": "1"
                },
                {
                    "policy": "21-0300/302/5000315",
                    "dueDate": "23/05/2021",
                    "amount": "80.12",
                    "currency": "EUR",
                    "amountBgn": "156.70",
                    "ibanBgn": "BG51BUIN95611010006950",
                    "insurer": "SGTESTMB6",
                    "insuranceAgencyName": "ЗАД Алианц България",
                    "reason": "MO/3738913/2",
                    "billNumber": "2"
                },
                {
                    "policy": "20-0300/302/5001935",
                    "dueDate": "30/05/2021",
                    "amount": "77.10",
                    "currency": "EUR",
                    "amountBgn": "150.79",
                    "ibanBgn": "BG51BUIN95611010006950",
                    "insurer": "Evgeniy Raev",
                    "insuranceAgencyName": "ЗАД Алианц България",
                    "reason": "MO/3683011/3",
                    "billNumber": "3"
                },
                {
                    "policy": "21-0300/302/5000082",
                    "dueDate": "01/05/2021",
                    "amount": "71.24",
                    "currency": "EUR",
                    "amountBgn": "139.33",
                    "ibanBgn": "BG51BUIN95611010006950",
                    "insurer": "SGTESTMB6",
                    "insuranceAgencyName": "ЗАД Алианц България",
                    "reason": "MO/3717823/2",
                    "billNumber": "2"
                },
                {
                    "policy": "21-0300/170/5000008",
                    "dueDate": "09/07/2021",
                    "amount": "700.76",
                    "currency": "BGN",
                    "amountBgn": "700.76",
                    "ibanBgn": "BG26BUIN70071005008618",
                    "insurer": "SGTESTMB6",
                    "insuranceAgencyName": "ЗАД Алианц България",
                    "reason": "MO/3712305/3",
                    "billNumber": "3"
                }
            ]
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode([InsuranceData].self, from: rawData) else {
                
                return []
        }
        
        return parsedData
        
    }
    
}
