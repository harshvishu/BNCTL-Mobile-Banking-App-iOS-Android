//
//  AccountDetails.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 8.10.21.
//

import SwiftUI

struct AccountDetails: View {
    let account:Account
    
    var body: some View {
        ZStack {
            Color("background").edgesIgnoringSafeArea(.all)
            VStack {
                TitleViewWithBack(title: "account_additional_details_title")
                ScrollView {
                    VStack(alignment: .leading, spacing: 30) {
                        DetailsRow(
                            title: "account_additional_details_label_beneficiary",
                            value: account.beneficiary.name)
                        DetailsRow(
                            title: "account_additional_details_label_account_number",
                            value: account.accountNumber)
                        DetailsRow(
                            title: "account_additional_details_label_bic",
                            value: account.swift)
                        DetailsRow(
                            title: "account_additional_details_label_account_type",
                            value: account.accountTypeDescription,
                            canCopy: false)
                        DetailsRow(
                            title: "account_additional_details_label_currency",
                            value: account.currencyName,
                            canCopy: false)
                    }
                    .padding()
                    .background(Color.white.cornerRadius(15))
                }.padding(.horizontal)
            }
            .hiddenNavigationBarStyle()
            .hiddenTabBar()
        }
    }
}

struct AccountDetails_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            AccountDetails(account: Account.preview!)
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}
