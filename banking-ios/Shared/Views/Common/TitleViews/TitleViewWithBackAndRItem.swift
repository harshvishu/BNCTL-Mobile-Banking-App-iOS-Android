//
//  TitleViewWithBackAndRItem.swift
//  Simplified version of the TitleView, with Back Button and Right Item.
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 17.02.22.
//

import SwiftUI

struct TitleViewWithBackAndRItem<Right: View>: View {
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var title: LocalizedStringKey
    var rightItem: Right
    
    init(
        title: LocalizedStringKey,
        @ViewBuilder rightItem: @escaping () -> Right
    ) {
        self.title = title
        self.rightItem = rightItem()
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
                Spacer(minLength: Dimen.Spacing.titleItem)
            }
            HStack {
                Spacer()
                rightItem
            }
        }
        .frame(maxHeight: Dimen.TitleView.height)
        .padding(.horizontal, Dimen.TitleView.paddingHorizontal)
    }
}

struct TitleViewWithBackAndRItem_Previews: PreviewProvider {
    static var previews: some View {
        TitleViewWithBackAndRItem(title: "Title with Back and Right") {
            Text("Right")
        }
    }
}
