//
//  CurrencyPicker.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 3.02.22.
//

import SwiftUI

struct CurrencyPicker: View {
    
    @Binding var selected: Currency?
    @State var isActive: Bool = false
    private let autoselect:Bool
    private let filter:(Currency) -> Bool
    
    init(selected:Binding<Currency?>,
         autoselect:Bool = true,
         filter:@escaping (Currency) -> Bool = {_ in true} )
    {
        self._selected = selected
        self.autoselect = autoselect
        self.filter = filter
    }
    
    var body: some View {
        if #available(iOS 15.0, *) {
            
        } else {
            Text(selected?.rawValue ?? "Currency")
        }
        Picker(
            selected?.rawValue ?? "Currency",
            selection: $selected)
        {
            if(autoselect == false) {
                Text("All")
                    .tag(nil as (Currency?))
            }
            ForEach(Currency.allCases.filter(filter)) { el in
                Text(el.rawValue)
                    .tag(el as Currency?)
            }
        }
        .pickerStyle(.menu)
        .onAppear {
            if #available(iOS 15, *) {
                if(autoselect
                    && selected == nil)
                {
                    selected = .BGN
                }
            }
        }
    }
}

struct CurrencyPicker_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }
        
    struct PreviewWrapper:View {
        @State var currency:Currency?
        
        var body: some View {
            CurrencyPicker(
                selected: $currency
            )
                .indicateError(true)
        }
    }
}
