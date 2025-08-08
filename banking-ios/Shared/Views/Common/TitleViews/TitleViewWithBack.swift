//
//  TitleViewWithBack.swift
//  Simplified version of the TitleView, with only a single a Back Button as a Left Item.
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 17.02.22.
//

import SwiftUI

struct TitleViewWithBack: View {
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var title: LocalizedStringKey
    
    init(title: LocalizedStringKey) {
        self.title = title
    }
    
    var body: some View {
        ZStack {
            HStack {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconBack")
                }
                Spacer()
            }
            HStack {
                Spacer(minLength: Dimen.Spacing.titleItem)
                Text(title)
                    .font(.system(size: Dimen.TextSize.title, weight: .bold, design: .default))
                    .foregroundColor(Color("PrimaryColor"))
                    .padding()
                Spacer()
            }
        }
        .frame(maxHeight: Dimen.TitleView.height)
        .padding(.horizontal, Dimen.TitleView.paddingHorizontal)
    }
}

struct TitleViewWithBack_Previews: PreviewProvider {
    static var previews: some View {
        TitleViewWithBack(title: "История на платежни за комунални услуги")
    }
}
