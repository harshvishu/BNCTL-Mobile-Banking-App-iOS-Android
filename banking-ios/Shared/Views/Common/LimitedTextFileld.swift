//
//  LimitedTextFileld.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 4.11.22.
//

import SwiftUI
import UIKit

public struct LimitedTextFileld: UIViewRepresentable {
    
    
    var placeholder: String = ""
    @Binding var value: String
    let limit:Int
    
    
    init(_ placeholder: String = "",
         value: Binding<String>,
         limit: Int
    ) {
        self._value = value
        self.placeholder = placeholder
        self.limit = limit
    }
    
    public func makeUIView(context: Context) -> UITextField {
        let textField = UITextField()
        textField.delegate = context.coordinator
        textField.placeholder = self.placeholder
        
        textField.text = String(value.prefix(limit))
        textField.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        textField.setContentCompressionResistancePriority(.defaultLow, for: .vertical)
        
        return textField
    }
    
    public func updateUIView(
        _ textField: UITextField,
        context: UIViewRepresentableContext<LimitedTextFileld>)
    {
        if(value != context.coordinator.currentValue) {
            textField.text = String(value.prefix(limit))
        }
    }
    
    public func makeCoordinator() -> LimitedTextFileld.Coordinator {
        return Coordinator(
            value:$value,
            limit:limit
        )
    }
    
    public class Coordinator: NSObject, UITextFieldDelegate {
        @Binding var value: String
        var currentValue:String
        var didBecomeFirstResponder = false
        
        var limit:Int
        
        init(value:Binding<String>,
             limit:Int)
        {
            _value = value
            currentValue = value.wrappedValue
            
            self.limit = limit
        }
        
        /*
         - when pasting code
        */
        public func textField(
            _ textField: UITextField,
            shouldChangeCharactersIn range: NSRange,
            replacementString string: String) -> Bool {
                if var s = textField.text {
                    let start = s.index(s.startIndex, offsetBy: min(s.count, range.lowerBound))
                    let end = s.index(s.startIndex, offsetBy: min(s.count, range.upperBound))
                    
                    s.replaceSubrange(
                        start..<end,
                        with: string
                    )
                    if(s == "") {
                        value = ""
                        currentValue = ""
                        return true
                    }
                    
                    if s.count < limit {
                        value = String(s.prefix(limit))
                        currentValue = String(s.prefix(limit))
                        return true
                    } else {
                        return false
                    }
                } else {
                    return true
                }
        }
        
        public func textFieldDidEndEditing(_ textField: UITextField) {
            textField.text = String(value.prefix(limit))
        }
    }
}


struct LimitedTextFileld_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper:View {
        @State var v:String = ""
        var body: some View {
            LimitedTextFileld(value:$v, limit: 35)
            .padding()
        }
    }
}
