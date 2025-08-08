//
//  AccountPicker.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 18.10.21.
//

import SwiftUI
import Combine

struct AccountPicker: View {
    
    @EnvironmentObject var accountsModel: AccountPickerViewModel
    @Environment(\.acountPickerAutoselect) private var autoselect:Autoselect
    
    @Binding var selected: Account?
    @Binding var pickingAccount: Bool
    
    let label: LocalizedStringKey // Used when there is nothing selected
    let title: LocalizedStringKey // Used in the custom NavBar Title
    
    let filterAccounts: ((Account) -> Bool)?
    
    let prmisionFor:OperationType?
    let direction: OperationDirection
    
    var body: some View {
        Group {
            if let type = prmisionFor,
               let accounts = accountsModel.accounts[type]?[direction]?
                .filter(filterAccounts ?? {_ in true}) {
                
                ZStack {
                    NavigationLink(isActive:$pickingAccount) {
                        VStack {
                            ScrollView {
                                TitleViewWithBack(title: title)
                                VStack(alignment: .leading) {
                                    HStack{
                                        Text("select_account_list_label_select_account")
                                            .font(.subheadline)
                                            .foregroundColor(.gray)
                                        Spacer()
                                    }
                                    ForEach(accounts) { account in
                                        Button {
                                            pickingAccount = false
                                            selected = account
                                        } label: {
                                            AccountListItem(account: account)
                                        }
                                        .padding()
                                        .frame(
                                            maxWidth:.infinity,
                                            minHeight: 64,
                                            alignment: .leading
                                        )
                                        .background(Color.white.cornerRadius(16))
                                    }
                                }
                                .padding()
                            }
                        }
                        .background(
                            Color("background")
                                .edgesIgnoringSafeArea([.all])
                        )
                        .hiddenNavigationBarStyle()
                    } label: {
                        EmptyView()
                    }
                    
                    Button {
                        pickingAccount = true
                    } label: {
                        HStack {
                            if let account = selected {
                                AccountListItem(account: account)
                                Spacer()
                                Image("IconEdit")
                            } else {
                                Text(label)
                            }
                        }
                    }
                }
            } else {
                ActivityIndicator(
                    label: "common_label_loading",
                    compact: true
                )
                    .onAppear {
                        if let prmisionFor = prmisionFor {
                            accountsModel.fetchAccounts(permissionCode: prmisionFor)
                        }
                    }
            }
        }
        .onReceive(accountsModel.$accounts) { dict in
            if let type = prmisionFor {
                switch autoselect {
                case .auto:
                    if let accounts = dict[type]?[direction]?.filter(filterAccounts ?? {_ in true}) {
                        autoSelect(accounts: accounts)
                    }
                case .iban(let iban):
                    selected = accountsModel.getAccount(
                        premisionFor: type,
                        direction: direction,
                        iban: iban)
                case .id(let id):
                    selected = accountsModel
                        .getAccount(
                            premisionFor: type,
                            direction: direction,
                            accointID: id)
                }
            }
        }
    }
            
    private func autoSelect(accounts:[Account]) {
        if accounts.count == 1,
           selected == nil,
           let autoselect = accounts.first
        {
            selected = autoselect
        }
    }
    
    struct AccountList:View {
        var body: some View {
            Text("Hi there")
        }
    }
    
    enum Autoselect {
        case auto
        case iban(String)
        case id(String)
    }
}

struct AccountPicker_Previews: PreviewProvider {
    static var previews: some View {
        AccountPicker.AccountList()
            .previewDisplayName("List")
        
        PreviewWrapper()
            .previewDisplayName("Demo")
    }
    
    struct PreviewWrapper: View {
        @State var account: Account?
        @State var showAccountPicker = false
        
        var body: some View {
            NavigationView {
                VStack {
                    Group {
                        AccountPicker(
                            selected: $account,
                            pickingAccount: $showAccountPicker,
                            label: "Select source account",
                            title: "From",
                            filterAccounts: nil,
                            prmisionFor: nil,
                            direction: .credit
                        )
                    }
                    .padding()
                    .frame(maxWidth:.infinity, minHeight: 64, alignment: .leading)
                    .background(Color.white.cornerRadius(16))
                    Button {
                        account = nil
                    } label: {
                        Text("Clear")
                            .commonButtonStyle()
                    }
                    
                }
                .environmentObject(AccountsViewModel(isLocal: true))
                .padding()
                .background(Color("background").ignoresSafeArea(.all))
            }
        }
    }
}
