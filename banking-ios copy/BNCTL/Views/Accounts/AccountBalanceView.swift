//
//  AccountBalanceView.swift
//  BNCTL
//
//  Created by Prem's on 20/02/23.
//

import SwiftUI

struct AccountBalanceView: View {

    @Binding var showBalance:Bool

    let account:Account

    var body: some View {
        ZStack {
            BackgroundBlurView()
            ZStack {
                Rectangle()
                    .foregroundColor(.white)
                    .cornerRadius(30)
                VStack(alignment: .leading) {
                    TitleViewWithRItem(title: "account_details_dialog_balances_title") {
                        Button {
                            showBalance = false;
                        } label: {
                            Image("IconClose")
                        }
                    }
                    VStack(alignment: .leading, spacing: 28) {
                        DetailsRow(
                            title: "account_details_label_current_balance",
                            value: "\(account.balance.current.toCurrencyFormatter()) \(account.currency)",
                            canCopy: false)
                        DetailsRow(
                            title: "account_details_label_available_balance",
                            value: "\(account.balance.available.toCurrencyFormatter()) \(account.currency)",
                            canCopy: false)
                    }
                    .padding(.vertical, 20)
                    Spacer()
                }.padding()
            }
            .padding()
            .hiddenNavigationBarStyle()
        }
    }
}

struct AccountBalanceView_Previews: PreviewProvider {

    static var previews: some View {
        let showModal:Binding<Bool> = Binding<Bool> {
            true
        } set: { value in
            print(value)
        }
        AccountBalanceView(
            showBalance: showModal,
            account: Account.preview!
        )
    }
}
