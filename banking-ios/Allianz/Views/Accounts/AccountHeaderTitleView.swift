//
//  AccountHeaderView.swift
//  Allianz
//
//  Created by harsh vishwakarma on 31/03/23.
//

import SwiftUI

struct AccountHeaderTitleView: View {
    var account: Account
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(account.product.name)
                    .foregroundColor(Color("PrimaryColor"))
                Text(account.iban ?? "")
                    .foregroundColor(Color("SecondaryColor"))
            }
            Spacer()
            Button {
                UIPasteboard.general.string = account.iban
            } label: {
                Image("IconCopyDark")
            }
        }
        .padding()
        .background(Color("SecondaryBackgroundColor"))
    }
}

struct AccountHeaderTitleView_Previews: PreviewProvider {
    static var previews: some View {
        AccountHeaderTitleView(account: Account.preview!)
    }
}
