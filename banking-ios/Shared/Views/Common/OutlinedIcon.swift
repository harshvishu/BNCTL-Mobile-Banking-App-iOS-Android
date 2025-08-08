//
//  OutlinedIcon.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 29.12.21.
//

import SwiftUI

struct OutlinedIcon: View {
    let image: String
    var body: some View {
        ZStack {
            Circle()
                .strokeBorder(Color("SecondaryColor"), lineWidth: 1)
                .frame(width: 32, height: 32)
            Image(image)
                .resizable()
                .scaledToFit()
                .frame(width: 24.0, height: 24.0)
        }
    }
}

struct OutlinedIcon_Previews: PreviewProvider {
    static var previews: some View {
        OutlinedIcon(image: "IconAccount")
    }
}
