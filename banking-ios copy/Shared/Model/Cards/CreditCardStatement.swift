    //
    //  CreditCardStatement.swift
    //  Allianz (iOS)
    //
    //  Created by Dimitar Stoyanov Chukov on 4.04.22.
    //

import Foundation

struct CreditCardStatementsModel: Codable {
    
    let result: [CreditCardStatement]
}

struct CreditCardStatement: Codable, Identifiable {
    
    let statementId: String
    let cardNumber: String
    let fileName: String
    let date: String
    
    var id: String { self.statementId }

    static var list: [CreditCardStatement] {
        let rawData = Data("""
            {
                "result": [
                    {
                        "statementId": "20210131",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20210131.XLSX",
                        "date": "2021-08-02 14:11:32"
                    },
                    {
                        "statementId": "20201231",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20201231.XLSX",
                        "date": "2021-05-01 12:23:52"
                    },
                    {
                        "statementId": "20201130",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20201130.XLSX",
                        "date": "2020-07-12 16:47:52"
                    },
                    {
                        "statementId": "20201031",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20201031.XLSX",
                        "date": "2020-05-11 13:50:09"
                    },
                    {
                        "statementId": "20200930",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20200930.XLSX",
                        "date": "2020-08-10 12:36:01"
                    },
                    {
                        "statementId": "20200831",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20200831.XLSX",
                        "date": "2020-11-09 14:08:05"
                    },
                    {
                        "statementId": "20200731",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20200731.XLSX",
                        "date": "2020-07-08 11:06:08"
                    },
                    {
                        "statementId": "20200531",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20200531.XLSX",
                        "date": "2020-05-06 13:29:20"
                    },
                    {
                        "statementId": "20200430",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20200430.XLSX",
                        "date": "2020-05-05 13:11:52"
                    },
                    {
                        "statementId": "20200331",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20200331.XLSX",
                        "date": "2020-06-04 00:11:06"
                    },
                    {
                        "statementId": "20200229",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20200229.XLSX",
                        "date": "2020-12-03 12:40:20"
                    },
                    {
                        "statementId": "20200131",
                        "cardNumber": "52XXXXXXXXXXXX73",
                        "fileName": "9561_006487_52XXXXXXXXXXXX73_20200131.XLSX",
                        "date": "2020-04-02 15:58:56"
                    }
                ]
            }
            """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode(CreditCardStatementsModel.self, from: rawData) else {
            
            return []
        }
        
        return parsedData.result
    }
}
