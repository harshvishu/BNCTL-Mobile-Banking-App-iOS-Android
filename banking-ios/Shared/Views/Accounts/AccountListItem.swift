//
//  AccountListItem.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 6.10.21.
//

import SwiftUI

struct AccountListItem: View {
    
    let account:Account
    
    var body: some View {
        HStack(alignment: .top) {
            ZStack {
                Circle()
                    .strokeBorder(Color("SecondaryColor"), lineWidth: 1)
                    .frame(width: 32, height: 32)
                Image("IconAccount")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 24.0, height: 24.0)
            }
            VStack(alignment: .leading, spacing: Dimen.Spacing.short) {
                Text(account.product.name)
                    .font(.subheadline)
                    .multilineTextAlignment(.leading)
                    .foregroundColor(Color("PrimaryTextColor"))
                Text(account.accountNumber)
                    .font(.footnote)
                    .padding(.bottom, Dimen.Spacing.regular)
                    .foregroundColor(Color("SecondaryTextColor"))
                HStack {
                    Group {
                        Text("dashboard_accounts_label_balance")
                            .bold()
                        Text("\(account.balance.available.toCurrencyFormatter()) \(account.currencyName)")
                            .bold()
                    }
                    .font(.subheadline)
                    .foregroundColor(.black)
                }
            }
            .frame(maxWidth:.infinity,alignment:.leading)
        }
    }
}

struct AccountListItem_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            Color("background")
            HStack {
                AccountListItem(account: Account.preview!)
                    .padding()
            }
            .background(Color.white.cornerRadius(16))
            .padding()
        }
    }
}
