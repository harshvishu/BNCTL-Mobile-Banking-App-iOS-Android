//
//  ServicesDashboardView.swift
//  Allianz (iOS)
//
//  Created by Prem's on 08/12/21.
//

import SwiftUI

struct ServicesDashboardView: View {
    
    @EnvironmentObject private var permissionsModel: PermissionsViewModel
    @StateObject var accountPickerViewModel: AccountPickerViewModel = .init()
    @StateObject var model = TransferDashboardViewModel()
    
    @State var showInsurance = false
    
    var body: some View {
        NavigationView {
            ZStack {
                Color("background")
                    .edgesIgnoringSafeArea(.all)
                VStack {
                    TitleViewSimple(title: "nav_services")
                    ScrollView {
                        ZStack {
                            Rectangle()
                                .foregroundColor(.white)
                                .cornerRadius(15)
                            VStack(alignment: .leading, spacing: 15) {
                                ListNavItem(label: "services_button_utility_bills", image: "IconUtilityBills") {
                                    UtilityBillsView()
                                }
                                NavigationLink(tag: SelectedScreen.insurance, selection: $model.screen) {
                                    InsuranceView()
                                } label: {
                                    Text("services_button_insurance")
                                        .listNavigation(image: "IconInsurance")
                                }
                                .isDetailLink(false)
                                ListNavItem(label: "services_button_cash_withdrawal", image: "IconCashWithdrawal") {
                                    CashWithdrawalView()
                                }
                                NavigationLink(tag: SelectedScreen.transfer(.betweenacc), selection: $model.screen) {
                                    TransferView(transferType: .currencyExchange)
                                } label: {
                                    Text("services_button_foreign_currency_exchange")
                                        .listNavigation(
                                            image: "IconForeignCurrencyExchange"
                                        )
                                }
                                .isDetailLink(false)
                                NavigationLink(tag: SelectedScreen.newDebitCard, selection: $model.screen) {
                                    RequestCardView()
                                        .environmentObject(accountPickerViewModel)
                                } label: {
                                    Text("services_button_request_new_card")
                                        .listNavigation(
                                            image: "IconReqeustNewCard"
                                        )
                                }
                                .isDetailLink(false)
                            }
                            .padding()
                            Spacer()
                        }
                    }
                    .padding(.horizontal)
                    .padding(.bottom)
                    .onAppear(perform: {
                        Tool.showTabBar()
                    })
                }
            }
            .hiddenNavigationBarStyle()
        }
        .operationStatusModal(
            operationStatus: $model.operationStatus,
            prefix: "transfer",
            flowComletion: nil,
            acceptOTP: {
                model.makeTransferUsingFallback()
            },
            declineOTP: nil,
            otpHandler: { otp, pin in
                model.makeTransferUsingFallback(otp: otp, pin: pin)
            })
        .onDisappear(perform: {
            accountPickerViewModel.clearData()
        })
        .environmentObject(accountPickerViewModel)
        .environmentObject(model)
    }
}

struct ServicesDashboardView_Previews: PreviewProvider {
    static var previews: some View {
        ServicesDashboardView()
            .environmentObject(PermissionsModel.shared)
    }
}
