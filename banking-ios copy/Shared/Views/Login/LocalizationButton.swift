//
//  LocalizationButton.swift
//  LocalizationButton
//
//  Created by Evgeniy Raev on 15.09.21.
//

import SwiftUI

struct LocalizationButton: View {
    
    @AppStorage("preferedLanguage") var preferedLanguage:String = Language.default.code
    
    var body: some View {
        Button {
            let lang = Language(rawValue: preferedLanguage)
            
            if (lang == .bg) {
                preferedLanguage = Language.en.code
            } else {
                preferedLanguage = Language.bg.code
            }
        } label: {
            Text("changeLanguage")
                .foregroundColor(Color("PrimaryButtonColor"))
        }
    }
}

struct LocalizationButton_Previews: PreviewProvider {
    
    static var previews: some View {
        LocalizationButton()
    }
}
