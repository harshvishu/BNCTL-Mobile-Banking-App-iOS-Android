//
//  FilledIcon.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 30.12.21.
//

import SwiftUI

struct FilledIcon: View {
    let image: String
    var body: some View {
        ZStack {
            Circle()
                .fill(Color("SecondaryColor"))
                .frame(width: 32, height: 32)
            Image(image)
                .resizable()
                .scaledToFit()
                .frame(width: 24.0, height: 24.0)
        }
    }
}

struct FilledIcon_Previews: PreviewProvider {
    static var previews: some View {
        FilledIcon(image: "IconAccountStatementsFilter")
    }
}
