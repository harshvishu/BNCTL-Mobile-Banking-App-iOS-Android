//
//  TitleView.swift
//  Allianz
//
//  Created by Peter Velchovski on 17.02.22.
//

import SwiftUI

struct TitleView<Left, Right> :View where Left: View, Right: View {
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var title: LocalizedStringKey
    var leftItem: Left
    var rightItem: Right

    init(
        title: LocalizedStringKey,
        @ViewBuilder leftItem: () -> Left,
        @ViewBuilder rightItem: () -> Right
    ) {
        self.title = title
        self.leftItem = leftItem()
        self.rightItem = rightItem()
    }
    
    var body: some View {
        ZStack {
            HStack {
                leftItem
                Spacer()
            }
            HStack {
                Spacer()
                Text(title)
                    .font(.system(size: Dimen.TextSize.title, weight: .bold, design: .default))
                    .foregroundColor(Color("PrimaryColor"))
                    .padding()
                Spacer()
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

struct TitleView_Previews: PreviewProvider {
    static var previews: some View {
        TitleView(
            title: "offices_and_atms_title",
            leftItem: {
                Text("Left")
            },
            rightItem: {
                Text("Right")
            }
        )
    }
}
