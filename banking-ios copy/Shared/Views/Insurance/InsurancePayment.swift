//
//  InsurancePayment.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 25.02.22.
//

import SwiftUI

struct InsurancePayment: View {
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @EnvironmentObject var model: InsurancePaymentViewModel
    @EnvironmentObject var dashboard: TransferDashboardViewModel
    let insurance: InsuranceData
    
    init(insurance: InsuranceData) {
        self.insurance = insurance
    }
    
    var body: some View {
        VStack {
            TitleViewWithBackAndRItem(title: "insurance_payment_title") {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconClose")
                }
            }
            SummarySection(title: "insurance_payment_label_amount") {
                if model.amounts.count > 0 {
                    if model.amounts.count > 1 {
                        RadioGroupWithView(
                            items: model.amounts,
                            selected: $model.selectedAmount
                        ) { amount in amountInfo(amount) }
                    } else {
                        let amount = model.amounts[0]
                        amountInfo(amount).onAppear {
                            model.selectedAmount = amount
                        }
                    }
                }
            }.padding(.horizontal)
            SummarySection(title: "insurance_payment_label_from") {
                AccountPicker(
                    selected: $model.sourceAccount,
                    pickingAccount: $model.selectingSourceAccount,
                    label: "insurance_payment_label_select_source_account",
                    title: "insurance_payment_select_source_account_picker_title",
                    filterAccounts: nil,
                    prmisionFor: .insurancePayment,
                    direction: .debit
                )

            }.padding(.horizontal)
            Spacer()
            HStack {
                Button() {
                    dashboard.makeTransfer(transfer: model.createTransferObject())
                    //model.makeTransfer()
                } label: {
                    Text("insurance_payment_button_confirm")
                }
                .commonButtonStyle()
                .disabled(model.selectedAmount == nil || model.sourceAccount == nil)
                .padding(.horizontal)
            }.padding()
        }
        .hiddenNavigationBarStyle()
        .background(Color("background").edgesIgnoringSafeArea(.all))
        .onAppear {
            model.insurance = insurance
        }
    }
    
    @ViewBuilder
    func amountInfo(_ amount: InsurancePaymentViewModel.Amount) -> some View {
        HStack(alignment: .firstTextBaseline, spacing: Dimen.Spacing.tiny) {
            Text(amount.amount)
                .foregroundColor(Color("PrimaryTextColor"))
                .font(.system(size: Dimen.TextSize.amountSummary, weight: .bold, design: .default))
            Text(amount.currency)
                .foregroundColor(Color("SecondaryTextColor"))
        }
    }
}

struct InsurancePayment_Previews: PreviewProvider {
    static var previews: some View {
        InsurancePayment(insurance: InsuranceData.preview!)
            .environmentObject(AccountsViewModel.preview)
            .environmentObject(InsurancePaymentViewModel(true))
    }
}
