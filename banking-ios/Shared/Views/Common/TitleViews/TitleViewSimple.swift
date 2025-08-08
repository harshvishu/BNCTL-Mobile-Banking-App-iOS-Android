//
//  TitleViewSimple.swift
//  Simplified version of the TitleView, with only a single a Back Button as a Left Item.
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 17.02.22.
//

import SwiftUI

struct TitleViewSimple: View {
    
    var title: LocalizedStringKey
    
    init(title: LocalizedStringKey) {
        self.title = title
    }
    
    var body: some View {
        ZStack {
            HStack {
                Spacer()
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

struct TitleViewSimple_Previews: PreviewProvider {
    static var previews: some View {
        TitleViewSimple(title: "Title with Back")
    }
}
