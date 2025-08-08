//
//  DoubleExtension.swift
//  Allianz (iOS)
//
//  Created by Prem's on 11/11/21.
//

import Foundation

extension Double {
    
    func toCurrencyFormatter(showCurrencyCode: Bool = false, currencyCode: String? = nil) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currencyISOCode
        formatter.decimalSeparator = DECIMAL_SEPARATOR
        formatter.groupingSeparator = GROUP_SEPARATOR
        if let currencyCode = currencyCode {
            formatter.currencyCode = currencyCode
        }
        if (!showCurrencyCode) {
            formatter.positiveFormat = CurrencyTextField.cutCurrencyCode(format: formatter.positiveFormat)
            formatter.negativeFormat = CurrencyTextField.cutCurrencyCode(format: formatter.negativeFormat)
        }
        var negativeFormat = formatter.negativeFormat!
        let index = negativeFormat.firstIndex(of: "#") ?? negativeFormat.firstIndex(of: "0")!
        negativeFormat.replaceSubrange(index ..< index, with: " -")
        formatter.negativeFormat = negativeFormat
        
        let formattedString = formatter.string(from: NSNumber(value: self))
        return formattedString!
    }
}
