//
//  CashWithdrawalView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 27.02.22.
//

import SwiftUI

struct CashWithdrawalView: View {
        
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    @StateObject var model = CashWithdrawalViewModel()
    
    var body: some View {
        VStack {
            TitleViewWithBackAndRItem(title: "cash_withdrawal_title") {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconClose")
                }
            }
            VStack {
                ScrollView {
                    VStack {
                        // Amount and currency
                        HStack(alignment: .bottom) {
                            FloatingLabelAmountTextField(
                                label: Text("cash_withdrawal_form_placeholder_amount"),
                                value: $model.amount,
                                error: model.formErrors["amount"])
                            VStack(alignment: .center) {
                                CurrencyPicker(selected: $model.currency)
                                    .indicateError((model.formErrors["currency"] == nil) == false)
                                    .padding(.leading)
                                Divider()
                                    .background(Color("TertiaryColor"))
                            }.fixedSize()
                        }
                        .padding(.top, Dimen.Spacing.short)
                        .padding(.bottom, Dimen.Spacing.large)
                        // Withdrawal Location
                        VStack {
                            ListSelectView(
                                label: "cash_withdrawal_form_placeholder_location",
                                items: model.branches,
                                itemsLocalized: false,
                                selected: $model.branch)
                            .indicateError((model.formErrors["branch"] == nil) == false)
                            Divider()
                                .background(Color("TertiaryColor"))
                        }
                        .padding(.bottom, Dimen.Spacing.tiny)
                        // Withdrawal date
                        VStack {
                            HStack(alignment: .bottom) {
                                Text("cash_withdrawal_form_placeholder_date")
                                Spacer()
                                DatePicker("",
                                    selection: $model.executionDate,
                                    in: (Date() + 24*60*60)...,
                                    displayedComponents: [.date]
                                )
                                .labelsHidden()
                            }
                            Divider()
                                .background(Color("TertiaryColor"))
                        }.padding(.bottom, Dimen.Spacing.large)
                        // Description
                        VStack {
                            FloatingLabelTextField(
                                label: Text("cash_withdrawal_form_placeholder_description"),
                                text: $model.description)
                        }
                    }
                    .padding()
                    .background(Color.white.cornerRadius(Dimen.CornerRadius.regular))
                    .padding(.horizontal)
                }
                HStack {
                    Button {
                        if (model.formValidation()) {
                            model.requestWithdrawal()
                        }
                    } label: {
                        Text("cash_withdrawal_button_next")
                    }
                    .commonButtonStyle()
                    .padding(.horizontal)
                }.padding()
            }
        }
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .background(Color("background").edgesIgnoringSafeArea(.all))
        .compatibleFullScreen(item: $model.operationStatus) { operationStatus in
            switch operationStatus {
            case .waiting:
                OperationWaitingView(type: .simple)
            case .failed:
                if let error = model.operationStatusError {
                    OperationOutcomeView(
                        imageKey: "OperationStatusError",
                        titleKey: error.title,
                        messageKey: error.message
                    ) {
                        model.operationStatus = nil
                        self.presentationMode.wrappedValue.dismiss()
                    }
                } else {
                    OperationOutcomeView(status: operationStatus) {
                        model.operationStatus = nil
                        self.presentationMode.wrappedValue.dismiss()
                    }
                }
                
            default:
                OperationOutcomeView(status: operationStatus) {
                    model.operationStatus = nil
                    self.presentationMode.wrappedValue.dismiss()
                }
            }
        }
        .onAppear {
            model.fetchBranches()
        }
    }
}

struct CashWithdrawalView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            CashWithdrawalView()
                .environment(\.locale, Locale(identifier: "bg-BG"))
        }
    }
}
