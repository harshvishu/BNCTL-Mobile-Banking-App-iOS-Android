//
//  TitleViewLItemOnly.swift
//  Simplified version of the TitleView, with only a single Right Item.
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 17.02.22.
//

import SwiftUI

struct TitleViewWithRItem<Right: View>: View {
    
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
                Spacer()
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

struct TitleViewWithRItem_Previews: PreviewProvider {
    static var previews: some View {
        TitleViewWithRItem(title: "Title with Right") {
            Text("Right")
        }
    }
}
