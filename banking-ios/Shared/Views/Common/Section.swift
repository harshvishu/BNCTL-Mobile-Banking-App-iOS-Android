//
//  Section.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 22.12.21.
//

import SwiftUI

struct SummarySection<Content: View>: View {
    
    let content: () -> Content
    let title: LocalizedStringKey
    let action: (() -> Void)?
    
    init(
        title: LocalizedStringKey,
        @ViewBuilder content:@escaping () -> Content,
        action:(() -> Void)? = nil
    ) {
        self.title = title
        self.content = content
        self.action = action
    }
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(title)
                    .font(.system(size: Dimen.TextSize.sectionLabel, weight: .regular, design: .default))
                    .foregroundColor(Color("SecondaryTextColor"))
                    .padding(.bottom, Dimen.Spacing.tiny)
                content()
            }
            Spacer()
            if let action = action {
                Button(action:action) {
                    Image("IconEdit")
                }
            }
        }
        .padding()
        .background(Color.white.cornerRadius(Dimen.CornerRadius.regular))
    }
}

struct Section_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            Color("background")
                .edgesIgnoringSafeArea(.all)
            VStack {
                SummarySection(title: "Field One") {
                    Text("This is the value of Field One")
                }
                
                SummarySection(title: "Editable Field Two") {
                    Text("Data for Field Two")
                } action: {
                    // Some action...
                }
            }
        }
    }
}
