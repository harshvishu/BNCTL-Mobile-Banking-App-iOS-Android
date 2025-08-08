//
//  UtilityBillsPaymentSummary.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 11.01.22.
//

import SwiftUI

struct UtilityBillsPaymentSummary: View {
    
    @EnvironmentObject var viewModel: UtilityBillsViewModel
    @EnvironmentObject var transfersDashboard: TransferDashboardViewModel
    @Environment(\.presentationMode) var presentationMode
    
    @State var selectingSourceAccount = false
    @State var sourceAccount: Account?
    @State var showInsufficientFunds:Bool = false
    
    var body: some View {
        VStack(spacing: Dimen.Spacing.regular) {
            TitleViewWithBack(title: "utility_bills_summary_title")
            ForEach(viewModel.sumSelected.sorted(by: >), id: \.key) { key, value in
                VStack(alignment:.leading) {
                    SummarySection(title: "utility_bills_summary_label_amount") {
                        HStack {
                            Group {
                                Text(value.toCurrencyFormatter())
                                Text(key)
                            }
                            .font(.system(size: Dimen.TextSize.amountSummary, weight: .bold, design: .default))
                            Spacer()
                        }
                    }
                }
                .background(Color.white.cornerRadius(Dimen.CornerRadius.regular))
                .padding(.horizontal)
            }
            VStack(alignment: .leading) {
                SummarySection(title: "utility_bills_summary_label_from") {
                    AccountPicker(
                        selected: $sourceAccount,
                        pickingAccount: $selectingSourceAccount,
                        label: "utility_bills_summary_label_select_source_account",
                        title: "utility_bills_summary_select_source_account_picker_title",
                        filterAccounts: {account in
                            viewModel.sumSelected[account.currencyName] != nil
                        },
                        prmisionFor: .utilityBills,
                        direction: .debit
                    )
                }
            }
            .padding(.horizontal)
            Spacer()
            HStack {
                Button("utility_bills_summary_button_confirm_payment"){
                    if let sourceAccount = sourceAccount,
                       let value = viewModel.sumSelected.first?.value,
                       sourceAccount.balance.available > value {
                        presentationMode.wrappedValue.dismiss()
                        transfersDashboard.pay(
                            utilityBills: viewModel.getIds(),
                            withAccount: sourceAccount)
                    } else {
                        showInsufficientFunds = true
                    }
                    //viewModel.paySelected(sourceAccout: sourceAccount as! Account)
                }
                .commonButtonStyle()
                .disabled(sourceAccount == nil)
                .padding(.horizontal)
            }
            .padding()
        }
        .compatibleAllert(
            showAlert:$showInsufficientFunds,
            titleKey: LocalizedStringKey("operation_transfer_failed_insufficient_funds_label_message"),
            defailtLabel: LocalizedStringKey("error_transfer_alert_ok"))
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .background(Color("background").edgesIgnoringSafeArea(.all))
    }
}

struct UtilityBillsPaymentSummary_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
        UtilityBillsPaymentSummary()
            .environmentObject(UtilityBillsViewModel(true))
            .environmentObject(AccountsViewModel.preview)
        }
    }
}
