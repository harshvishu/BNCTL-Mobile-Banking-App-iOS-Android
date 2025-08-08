//
//  DetailsRow.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 28.01.22.
//

import SwiftUI

struct DetailsRow: View {

    let title: LocalizedStringKey
    let value: String
    let canCopy: Bool
    
    init (title: LocalizedStringKey, value: String, canCopy: Bool = true) {
        self.title = title
        self.value = value
        self.canCopy = canCopy
    }
    
    var body:some View {
        HStack {
            VStack(alignment: .leading, spacing: 10) {
                Text(title)
                    .font(.footnote)
                    .foregroundColor(.gray)
                Text(value)
            }
            if (canCopy) {
                Spacer()
                Button {
                    UIPasteboard.general.string = value
                } label: {
                    Image("IconCopy")
                }
            }
        }
    }
    
}

struct DetailsRow_Previews: PreviewProvider {
    static var previews: some View {
        DetailsRow(
            title: "Title",
            value: "Value of the Detail"
        )
    }
}
