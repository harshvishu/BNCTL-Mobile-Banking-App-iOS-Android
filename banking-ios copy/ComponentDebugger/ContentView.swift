//
//  ContentView.swift
//  ComponentDebugger
//
//  Created by Evgeniy Raev on 22.11.22.
//

import SwiftUI

struct ContentView: View {
    
    @State var text = ""
    @State var number = 1.245
    @State var value:Double?
    let locale:Locale
    let formatter:Formatter
    
    init() {
        let locale = Locale(identifier: "de-DE")
        
        self.locale = locale
        
        let formatter = NumberFormatter()
        //formatter.decimalSeparator = "."
        formatter.locale = locale
        formatter.allowsFloats = true
        formatter.minimumSignificantDigits = 2
        formatter.maximumSignificantDigits = 3
        
        self.formatter = formatter
    }
    
    var body: some View {
        VStack {
            TextField("test", text: $text)
                .keyboardType(.decimalPad)
            
            TextField("with formatter", value: $number, formatter: formatter)
                .keyboardType(.decimalPad)
            
            CurrencyTextField(value: $value)
        }
        .padding()
        .environment(\.locale, locale)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
