//
//  CurrencyTextField.swift
//  SwiftUICurrencyTextField
//
//  Created by Evgeniy Raev on 13.10.22.
//

import SwiftUI
import UIKit

public struct CurrencyTextField: UIViewRepresentable {
    
    public static func generateGeneralCurrencyPattern(
        acceptedCurenciees:[String]
    ) ->String {
        
        let currenciesPart = "(\(acceptedCurenciees.joined(separator: "|")))?"
        let digitsPart = "((?:(?:[0-9](,|\\.|\\s))?[0-9]+)+)"
        let decimalSeparatorPart = "(\\.|,)"
        let decimalPart = "([0-9]{1,3}))?"
        
        let pattern = [
            "(?:\(currenciesPart)\\s)?",
            digitsPart,
            decimalSeparatorPart,
            decimalPart,
            "(?:\\s\(currenciesPart))?"
        ].joined(separator: "")
        
        return pattern
    }
    
    public static func generateValidCurrencyPatten() -> String {
        return ""
    }
    
    public static func cutCurrencyCode(format: String) -> String {
        let range = NSRange(location: 0, length: format.utf16.count)
        let regexToRemoveCurrencySymbolInFormat = try! NSRegularExpression(pattern: "\\s?¤+\\s?")
        let newString = regexToRemoveCurrencySymbolInFormat.stringByReplacingMatches(in: format, range: range, withTemplate: "")
        return newString
    }
    
    public static func generatePartalCurrnecyPattern(
        formatter:NumberFormatter
    ) -> String {
        var currencySymbol = formatter.currencySymbol!
        var groupSeparator = formatter.currencyGroupingSeparator!
        var decimalSeparator = formatter.currencyDecimalSeparator!
        let decimalPlaces = formatter.minimumFractionDigits
        
        let format = formatter.positiveFormat!
        let digitsPlace = format.firstIndex(of: "#") ?? format.firstIndex(of: "0")!
        let currencyPlace = format.firstIndex(of: "¤")
        
        let start = currencySymbol.startIndex
        for i in 0..<currencySymbol.count {
            let after = currencySymbol.index(start, offsetBy: i*2+1)
            currencySymbol.replaceSubrange(after..<after, with: "?")
        }
        
        func escapeRegexSymbols( text:inout String) {
            if let index = text.firstIndex(of: "$") {
                text.replaceSubrange(
                    index..<index, with: "\\"
                )
            }
            if let index = text.firstIndex(of: ".") {
                text.replaceSubrange(
                    index..<index, with: "\\"
                )
            }
        }
        if currencySymbol.count > 0 {
            escapeRegexSymbols(text: &currencySymbol)
        }
        if groupSeparator.count > 0 {
            escapeRegexSymbols(text: &groupSeparator)
        }
        if decimalSeparator.count > 0 {
            escapeRegexSymbols(text: &decimalSeparator)
        }
        
        //TODO: This checking have to be moved to separate regex
        let checkForDublets         = "(?!(?:\\.|,|\\s){2,})"
        let currencySymbolBeggining = "(\(currencySymbol))\\s?"
        let wholePart               = "((?:(?:[0-9]\(groupSeparator))?[0-9]*)*)"
        let decimalSeparatorPart    = "(?:\\.|,|\(decimalSeparator))?"
        let decimalDigitsPart       = "[0-9]{0,\(decimalPlaces)}"
        let decimalPart             = decimalPlaces > 0
                                        ? "(?:(\(decimalSeparatorPart))(\(decimalDigitsPart)))?"
                                        : ""
        let curencySymbolEnd        = "\\s?(\(currencySymbol))?"
       
        let allParts = [
            "^",
            checkForDublets,
            currencyPlace != nil && digitsPlace > currencyPlace! ? currencySymbolBeggining : "",
            wholePart,
            decimalPart,
            currencyPlace != nil && digitsPlace < currencyPlace! ? curencySymbolEnd : "",
            "$"
        ].joined(separator: "")
       
        let pattern = "^\(allParts)$"
        return pattern
    }
    
    var placeholder: String = ""
    @Binding var value: Double?
    private var formatter:NumberFormatter
    
    
    init(_ placeholder: String = "",
         value: Binding<Double?>,
         currencyCode: String? = nil,
         hideCurrencyCode: Bool = true
    ) {
        self._value = value
        self.placeholder = placeholder
        
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyGroupingSeparator = GROUP_SEPARATOR
        formatter.decimalSeparator = DECIMAL_SEPARATOR
        if let currencyCode = currencyCode {
            formatter.currencyCode = currencyCode
        }
        
        if (hideCurrencyCode) {
            formatter.positiveFormat = CurrencyTextField.cutCurrencyCode(
                format: formatter.positiveFormat
            )
            
            formatter.negativeFormat = CurrencyTextField.cutCurrencyCode(
                format: formatter.negativeFormat
            )
        }
        
        self.formatter = formatter
    }
    
    public func makeUIView(context: Context) -> UITextField {
        let textField = UITextField()
        textField.delegate = context.coordinator
        textField.placeholder = self.placeholder
        textField.keyboardType = .decimalPad
            //textField.
        
        textField.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        textField.setContentCompressionResistancePriority(.defaultLow, for: .vertical)
        
        if let value = self.value {
            let formatted = formatter.string(
                from:NSNumber(floatLiteral: value)
            ) ?? "fail"
            textField.text = formatted
        }
        
        return textField
    }
    
    public func updateUIView(
        _ currencyTextField: UITextField,
        context: UIViewRepresentableContext<CurrencyTextField>)
    {
        if let value = self.value {
            if(value != context.coordinator.currentValue) {
                let formatted = formatter.string(
                    from:NSNumber(floatLiteral: value)
                ) ?? "fail"
                
                currencyTextField.text = formatted
            }
        } else {
            currencyTextField.text = nil
        }
    }
    
    public func makeCoordinator() -> CurrencyTextField.Coordinator {
        return Coordinator(
            value:$value,
            formatter: formatter
        )
    }
    
    public class Coordinator: NSObject, UITextFieldDelegate {
        @Binding var value: Double?
        var currentValue:Double?
        var didBecomeFirstResponder = false
        
        let formatter:NumberFormatter
        let parttalRegex:NSRegularExpression
        
        init(value:Binding<Double?>,
            formatter: NumberFormatter)
        {
            _value = value
            currentValue = value.wrappedValue
            
            self.formatter = formatter
            
            self.parttalRegex = try! NSRegularExpression(
                pattern: generatePartalCurrnecyPattern(formatter: formatter)
            )
        }
        
        /*
         - when pasting code
        */
        public func textField(
            _ textField: UITextField,
            shouldChangeCharactersIn range: NSRange,
            replacementString string: String) -> Bool {
                if var s = textField.text {
                    
                    let start = s.index(s.startIndex, offsetBy: range.lowerBound)
                    let end = s.index(s.startIndex, offsetBy: range.upperBound)
                    
                    s.replaceSubrange(
                        start..<end,
                        with: string
                    )
                    if(s == "") {
                        value = nil
                        currentValue = nil
                        return true
                    }
                    
                    let matchRange = NSRange(location: 0, length:s.utf16.count)
                    
                    if let match = parttalRegex.firstMatch(
                        in: s,
                        range: matchRange) {
                        
                        let ns = (s as NSString)
                        
                        var wholePart:String? = nil
                        var decimalPart:String? = nil
                        let groupingSeparator = formatter.currencyGroupingSeparator
                        for r in 1..<match.numberOfRanges {
                            let range = match.range(at: r)
                            if range.length > 0 {
                                let ss = ns.substring(with: range)
                                if(r == 1) {
                                    if let groupingSeparator = groupingSeparator {
                                        wholePart = ss.replacingOccurrences(
                                            of:  groupingSeparator,
                                            with: "")
                                    }
                                }
                                if(r == 3) {
                                    decimalPart = ss
                                }
                            }
                        }
                        if(wholePart == nil && decimalPart == nil) {
                            self.value = nil
                            self.currentValue = nil
                        } else {
                            let newValue = Double("\(wholePart ?? "").\(decimalPart ?? "")")!
                            
                            self.value = newValue
                            self.currentValue = newValue
   
                        }
                        
                        return true
                    } else {
                        return false
                    }
                } else {
                    return true
                }
        }
        
        public func textFieldDidEndEditing(_ textField: UITextField) {
            if let value = value {
                let formatted = formatter.string(
                    from:NSNumber(floatLiteral: value)
                ) ?? "fail"
                
                textField.text = formatted
            }
        }
    }
}

struct CurrencyTextField_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper:View {
        @State var v:Double? = 1.234
        var body: some View {
            VStack {
                CurrencyTextField(
                    "test",
                    value: $v
                )
                CurrencyTextField(
                    "test",
                    value: $v,
                    currencyCode: "USD"
                )
                CurrencyTextField(
                    "test",
                    value: $v,
                    currencyCode: "JPY"
                )
                CurrencyTextField(
                    "test",
                    value: $v,
                    currencyCode: "AUD"
                )
            }
            .padding()
        }
    }
}
