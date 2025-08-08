//
//  TitleViewWithLItem.swift
//  Simplified version of the TitleView, with only a single Left Item.
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 17.02.22.
//

import SwiftUI

struct TitleViewWithLItem<Left: View>: View {
    
    var title: LocalizedStringKey
    var leftItem: Left
    
    init(
        title: LocalizedStringKey,
        @ViewBuilder leftItem: @escaping () -> Left
    ) {
        self.title = title
        self.leftItem = leftItem()
    }
    
    var body: some View {
        ZStack {
            HStack {
                leftItem
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

struct TitleViewWithLItem_Previews: PreviewProvider {
    static var previews: some View {
        TitleViewWithLItem(title: "Title with Left") {
            Text("Left")
        }
    }
}
