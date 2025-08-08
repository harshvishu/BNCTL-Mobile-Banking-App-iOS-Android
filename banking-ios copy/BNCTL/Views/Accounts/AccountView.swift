//
//  AccountView.swift
//  BNCTL
//
//  Created by Prem's on 20/02/23.
//

import SwiftUI

struct AccountView: View {
    let account: Account

    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @ObservedObject var accountModel: AccountViewModel

    @State var showFilter = false
    @State var showBalance = false

    init(account: Account, _ local: Bool = false) {
        self.account = account
        self.accountModel = AccountViewModel(account:account, local)
    }

    var body: some View {
        VStack {
            TitleView(
                title: "account_details_title",
                leftItem: {
                    Button {
                        self.presentationMode.wrappedValue.dismiss()
                    } label: {
                        Image("IconBack")
                    }
                },
                rightItem: {
                    NavigationLink(destination: {
                        AccountDetails(account: account)
                    }) {
                        Image("IconDetails")
                    }
                }
            )
            ScrollView {
                VStack(spacing:20) {
                    accountHeader()
                    accountStatements()
                }
                .padding()
                .compatibleFullScreen(isPresented: $showBalance) {
                    AccountBalanceView(showBalance: $showBalance, account: account)
                }
            }
        }
        .background(
            Color("background").edgesIgnoringSafeArea(.all)
        )

        .hiddenNavigationBarStyle()
        .showTabBar()
        .onAppear(perform: {
            accountModel.fetchStatements()
        })
    }

    func accountHeader () -> some View {
        VStack(alignment: .leading, spacing: 0) {
            // Product Name
            HStack {
                VStack(alignment: .leading) {
                    Text(account.product.name)
                        .foregroundColor(Color("PrimaryColor"))
                        .fontWeight(.bold)
                    Text(account.iban)
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
            // Balances
            VStack(alignment: .leading) {
                HStack(alignment: .bottom) {
                    VStack(alignment: .leading) {
                        HStack(alignment: .firstTextBaseline, spacing: 2){
                            Text("\(account.balance.available.toCurrencyFormatter())")
                                .fontWeight(.bold)
                            Text(account.currencyName)
                                .fontWeight(.light)
                                .foregroundColor(.gray)
                        }
                        .font(.system(size: Dimen.TextSize.balanceLarge))
                    }
                    Spacer()
                    Button {
                        showBalance = true
                    } label: {
                        HStack {
                            Text("account_details_button_balances")
                        }
                    }
                }
            }
            .padding()
            .background(Color.white)
        }
        .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
    }

    func accountStatements() -> some View {
        let statementFilterStartDate = accountModel
            .statementsFilter.startDate.formatToLocalDate()
        let statementFilterEndDate = accountModel
            .statementsFilter.endDate.formatToLocalDate()

        return LazyVStack(pinnedViews: .sectionHeaders) {
        Section {
            if (accountModel.statements.isEmpty) {
                if (accountModel.isLoadingStatements) {
                    Spacer()
                    ActivityIndicator(label: "account_details_label_loading_statements")
                        .padding()
                    Spacer()
                } else {
                    Spacer()
                    Text("transfers_history_no_items")
                        .foregroundColor(Color("SecondaryTextColor"))
                        .font(.system(size: Dimen.TextSize.info))
                        .multilineTextAlignment(.center)
                        .padding()
                    Spacer()
                }
            } else {
                ForEach(accountModel.statements) { statement in
                    PaymentListItem(
                        payment: statement,
                        infoField: .transactionType
                    )
                    .padding([.horizontal, .bottom])
                }
            }
        } header: {
            HStack {
                Text("statements_label")
                    .fontWeight(.bold)
                Text("\(statementFilterStartDate) - \(statementFilterEndDate)")
                Spacer()
                Button {
                    showFilter = true
                } label: {
                    FilledIcon(image: "IconAccountStatementsFilter")
                }.popover(isPresented: $showFilter) {
                    StatementsFilterView(
                        model: accountModel,
                        isPresented: $showFilter)
                }
            }
            .padding()
            .background(
                Color.white.cornerRadius(Dimen.CornerRadius.regular)
            )
        }
        }
        .background(
            Color.white.cornerRadius(Dimen.CornerRadius.regular)
        )
    }
}


struct AccountView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            AccountView(account: Account.preview!, true)
        }
    }
}

