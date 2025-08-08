//
//  SelectedAccountView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 7.03.22.
//

import SwiftUI

struct SelectedAccountView: View {
    
    @EnvironmentObject var accountsModel: AccountsViewModel
    @Binding var selected: AccountDetailsProtocol?
    let label: LocalizedStringKey // TODO: Translate!
    
    func getAccount() -> Account? {
        if let account = selected as? Account {
            return account;
        } else if
            let manual = selected as? ManualAccountDetails,
            let account = accountsModel.accounts.first(where: {
                $0.iban == manual.account
            })
        {
            selected = account
            return account
        }
        return nil
    }
    
    var body: some View {
        HStack {
            if let account = getAccount() {
                AccountListItem(account: account)
            } else {
                Text(label)
            }
            Spacer()
            Image("IconEdit")
        }
    }
}

struct SelectedAccountView_Previews: PreviewProvider {
    
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper: View {
        @State var account: AccountDetailsProtocol? = Account.preview
        
        var body: some View {
            SelectedAccountView(selected: $account, label: "No Account Selected")
                .environmentObject(AccountsViewModel.preview)
        }
    }
}
