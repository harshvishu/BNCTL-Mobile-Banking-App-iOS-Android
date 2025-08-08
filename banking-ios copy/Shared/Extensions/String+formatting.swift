//
//  String+cardNumberFormat.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 23.12.21.
//

import Foundation
import SwiftUI

enum DateFormatType: String {
    case serverDateTime = "yyyy-MM-dd HH:mm:ss"
}

extension String {
        
    // Applies card number formatting to a string.
    // Splits it to 4 groups of 4 characters and adds space to them.
    func toCardNumberFormat() -> String {
        guard self.count == 16 else {
            Logger.E(tag: APP_NAME, "Attempting to format as a card number string with lengh different than 16.")
            return self
        }
        var result = ""
        for i in 1...4 {
            let start = i * 4 - 4
            let end = i * 4 - 1
            
            let startIndex = self.index(self.startIndex, offsetBy: start)
            let endIndex = self.index(self.startIndex, offsetBy: end)
            
            let range = startIndex...endIndex
            let part = String(self[range])
            
            if (result == "") {
                result = part
            } else {
                result = "\(result) \(part) "
            }
        }
        return result
    }

    func convertStringToDate(dateformat formatType: DateFormatType) -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = formatType.rawValue
        // dateFormatter.timeZone = TimeZone(abbreviation: "UTC")
        let newDate = dateFormatter.date(from: self)
        return newDate
    }

    func localeDate() -> String {
        let date = convertStringToDate(dateformat: .serverDateTime)
        let str = date?.formatToLocalDateTime() ?? self
        return str
    }
    
}
