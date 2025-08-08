//
//  FormSection.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 6.01.22.
//

import SwiftUI

struct FormSection<Content: View>: View {
    
    let title: LocalizedStringKey
    let content: Content
    
    init(title: LocalizedStringKey, @ViewBuilder content:() -> Content) {
        self.title = title
        self.content = content()
    }
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(title)
                .padding(.horizontal)
                .font(.footnote)
                .foregroundColor(Color("SecondaryTextColor"))
            VStack(alignment:.leading) {
                content
            }
            .padding()
            .frame(
                maxWidth:.infinity,
                alignment: .leading)
            .background(
                Color.white.cornerRadius(15))
        }
    }
}


struct FormSection_Previews: PreviewProvider {
    static var previews: some View {
        FormSection(title: "Form Section") {
            TextField("Enter Value Here", text: .constant("Value"))
        }
    }
}
