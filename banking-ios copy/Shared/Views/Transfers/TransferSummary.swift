//
//  TransferSummary.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 14.10.21.
//

import SwiftUI

struct TransferValidate {
    let amount: String
    let reason: String
    
    let source: Account
    let destination: AccountDetailsProtocol
    
    let transferType: TransferType
    let executionType: String = ""
}

struct TransferSummary: View {
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    @EnvironmentObject var accountsModel: AccountPickerViewModel
    @EnvironmentObject var transfersDashboard: TransferDashboardViewModel
    
    let transfer: Transfer
    
    var body: some View {
        VStack {
            TitleViewWithBackAndRItem(title: "transfer_summary_title") {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconClose")
                }
            }
            ScrollView {
                VStack(spacing: Dimen.Spacing.regular) {
                    SummarySection(title: "transfer_summary_label_amount") {
                        HStack(alignment: .firstTextBaseline){
                            Text(transfer.amount)
                                .font(.system(size: Dimen.TextSize.amountSummary, weight: .bold, design: .default))
                            Text(transfer.transferCurrency)
                                .font(.system(size: Dimen.TextSize.amountSummary, weight: .light, design: .default))
                                .foregroundColor(Color("SecondaryTextColor"))
                        }
                    } action: {
                        presentationMode.wrappedValue.dismiss()
                    }
                    SummarySection(title: "transfer_summary_label_from") {
                        if let account = accountsModel.getAccount(
                            premisionFor: transfer.transferType.premisionCode,
                            direction: .debit,
                            accointID: transfer.sourceAccountId)
                        {
                            AccountListItem(account: account)
                        } else {
                            HStack(alignment: .top) {
                                Image("OperationStatusError")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 32.0, height: 32.0)
                                VStack(alignment: .leading, spacing: Dimen.Spacing.short) {
                                    Text("No Account selected")
                                        .font(.system(size: Dimen.TextSize.info, weight: .regular, design: .default))
                                        .multilineTextAlignment(.leading)
                                        .foregroundColor(Color("PrimaryTextColor"))
                                    Text("Select source Account.")
                                        .font(.footnote)
                                        .foregroundColor(Color("SecondaryTextColor"))
                                }
                                .frame(maxWidth:.infinity,alignment:.leading)
                            }
                        }
                    } action: {
                        presentationMode.wrappedValue.dismiss()
                    }
                    SummarySection(title: "transfer_summary_label_to") {
                        if let account = accountsModel.getAccount(
                            premisionFor: transfer.transferType.premisionCode,
                            direction: .debit,
                            iban: transfer.destinationAccount)
                        {
                            AccountListItem(account: account)
                        } else {
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
                                    Text(transfer.recipientName ?? "")
                                        .font(.system(size: Dimen.TextSize.info, weight: .regular, design: .default))
                                        .multilineTextAlignment(.leading)
                                        .foregroundColor(Color("PrimaryTextColor"))
                                    Text(transfer.destinationAccount)
                                        .font(.footnote)
                                        .foregroundColor(Color("SecondaryTextColor"))
                                }
                                .frame(maxWidth:.infinity,alignment:.leading)
                            }
                        }
                    } action: {
                        presentationMode.wrappedValue.dismiss()
                    }
                    if transfer.transferType != .currencyExchange {
                        SummarySection(title: "transfer_summary_label_reason") {
                            Text(transfer.description)
                                .font(.system(size: Dimen.TextSize.info, weight: .regular, design: .default))
                                .foregroundColor(Color("PrimaryTextColor"))
                        } action: {
                            presentationMode.wrappedValue.dismiss()
                        }
                        if let additionalDescription = transfer.additionalDescription {
                            SummarySection(title: "transfer_summary_label_additional_reason") {
                                Text(additionalDescription)
                                    .font(.system(size: Dimen.TextSize.info, weight: .regular, design: .default))
                                    .foregroundColor(Color("PrimaryTextColor"))
                            } action: {
                                presentationMode.wrappedValue.dismiss()
                            }
                        }
                    }

                    Spacer()
                    HStack {
                        Button {
                            transfersDashboard.makeTransfer(transfer: transfer)
                        } label: {
                            Text("transfer_summary_button_confirm_payment")
                                .commonButtonStyle()
                        }.padding(.horizontal)
                    }.padding()
                    
                }
                .padding(.horizontal)
            } .edgesIgnoringSafeArea(.bottom)

        }
        .background(Color("background").edgesIgnoringSafeArea(.all))
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
    }
}

extension Transfer {
    var transferCurrency:String {
        if(additionalDetails?.transactionType == .currencyExchange
           && additionalDetails?.orderType == .sell) {
            return sourceAccountCurrency
        } else {
            return destinationAccountCurrency
        }
    }
}

struct TransferSummary_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper: View {
        @State var presenting = true
        @State var source = false
        @State var destionation = false
        
        var body: some View {
            NavigationView {
                TransferSummary(transfer: Transfer.transferPreview(type: .interbank))      
            }
            .environmentObject(AccountsViewModel.preview)
            .environment(\.locale, .init(identifier: "bg"))
        }
    }
}
