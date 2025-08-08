//
//  AccountsDashboard.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 6.10.21.
//

import SwiftUI
import Combine

struct AccountsDashboard: View {
    
    @StateObject private var accountsModel: AccountsViewModel = .init()
    @EnvironmentObject private var permissionsModel: PermissionsViewModel
    
    @State var account: Account?
    
    var body: some View {
        NavigationView {
            ZStack {
                Color("background").edgesIgnoringSafeArea(.all)
                VStack(alignment: .leading, spacing: 0) {
                    HStack {
                        Image("Logo")
                            .resizable()
                            .scaledToFit()
                            .frame(height: 30.0)
                            .padding(.horizontal, 10)
                        Spacer()
                        NavigationLink {
                            NewsView()
                        } label: {
                            Image("IconNotification")
                                .padding(.top, 16)
                        }
                    }
                    .frame(maxHeight: Dimen.TitleView.height)
                    .padding(.horizontal, Dimen.TitleView.paddingHorizontal)
                    if (accountsModel.accounts.isEmpty) {
                        Spacer()
                        HStack {
                            Spacer()
                            if (accountsModel.isLoading) {
                                ActivityIndicator(label: "dashboard_label_loading")
                            } else {
                                Text("dashboard_label_no_accounts")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                    .font(.system(size: Dimen.TextSize.info))
                                    .multilineTextAlignment(.center)
                                    .padding()
                            }
                            Spacer()
                        }
                        Spacer()
                    } else {
                        ScrollView {
                            VStack(spacing:20) {
                                HStack {
                                    Text("dashboard_title_accounts")
                                        .font(.system(size: 14.0))
                                        .foregroundColor(.gray)
                                    Spacer()
                                }.padding(.top)
                                ForEach(accountsModel.accounts, id: \.id) { account in
                                    Button {
                                        accountsModel.selectedAccount = account
                                    } label: {
                                        AccountListItem(account: account)
                                            .padding()
                                            .background(Color.white.cornerRadius(16))
                                    }
                                }
                            }
                            .padding(.horizontal)
                        }
                    }
                }
                NavigationLink(isActive: $accountsModel.showAccountDetails) {
                    if let account = accountsModel.selectedAccount {
                        AccountView(account: account)
                    } else {
                        EmptyView()
                    }
                } label: {
                    EmptyView()
                }
            }
            .hiddenNavigationBarStyle()
            .onAppear {
                Tool.showTabBar()
            }
        }
        .onAppear(perform: {
            accountsModel.fetchAccounts()
        })
        .environmentObject(accountsModel)
        .compatibleAllert(
            showAlert: $accountsModel.showAllert,
            titleKey: LocalizedStringKey(accountsModel.error ?? "Someting went wrong"),
            defailtLabel: "Close"
        )
    }
}

extension Account: Equatable {
    static func == (lhs: Account, rhs: Account) -> Bool {
        return lhs.accountId == rhs.accountId && lhs.balance.available == rhs.balance.available
    }
}

struct AccountsDashboard_Previews: PreviewProvider {
    static var previews: some View {
        AccountsDashboard()
            .environmentObject(AccountsViewModel.preview)
    }
}
