//
//  PayeesList.swift
//  BNCTL
//
//  Created by Rahul B on 22/02/23.
//

import SwiftUI

struct PayeesList: View {
    let title: String
    let id: String

    var body: some View {
        HStack(alignment: .center, spacing: 10) {
            ZStack {
                Circle()
                    .strokeBorder(Color("SecondaryColor"), lineWidth: 1)
                    .frame(width: 40, height: 40)
                Image("IconAccount")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 30.0, height: 30.0)
                    .padding()
            }
            VStack(alignment: .leading, spacing: Dimen.Spacing.tiny) {
                Text(title)
                    .font(.subheadline)
                    .multilineTextAlignment(.leading)
                    .foregroundColor(Color("PrimaryTextColor"))
                Text(id)
                    .font(.footnote)
                    .foregroundColor(Color("SecondaryTextColor"))
            }
            .frame(maxWidth:.infinity,alignment:.leading)
            .padding(-10)
        }
        .frame(height: 70)
        .background(Color.white.cornerRadius(16))
    }
}

struct PayeesList_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            Color("background")
            HStack {
                PayeesList(title: "Rahul", id: "12121212121212")
                    .padding()
            }
            .background(Color.white.cornerRadius(16))
            .padding()
        }
    }
}
