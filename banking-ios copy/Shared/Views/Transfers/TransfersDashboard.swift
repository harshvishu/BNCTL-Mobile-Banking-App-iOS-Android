//
//  TransfersTestDashboard.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 17.02.22.
//

import SwiftUI

enum SelectedScreen:Hashable {
    case transfer(TransferType)
    case templates
    case pending
    case history
    case insurance
    case newDebitCard
}

struct TransfersDashboard: View {
    
    @EnvironmentObject var permissionsModel: PermissionsViewModel
    @StateObject var accountPickerViewModel: AccountPickerViewModel = .init()
    @StateObject var model = TransferDashboardViewModel()
    
    var body: some View {
        NavigationView {
            ZStack {
                Color("background").edgesIgnoringSafeArea(.all)
                VStack {
                    TitleViewSimple(title: "nav_transfers")
                    ScrollView {
                        ZStack {
                            Rectangle()
                                .foregroundColor(.white)
                                .cornerRadius(15)
                            VStack(alignment: .leading, spacing: 15) {
                                NavigationLink(tag: SelectedScreen.transfer(.betweenacc), selection: $model.screen) {
                                    TransferView(transferType: .betweenacc)
                                } label: {
                                    Text("transfers_button_between_accounts")
                                        .listNavigation(image: "IconTransferBetweenOwnAccounts")
                                }
                                .isDetailLink(false)
                                NavigationLink(tag: SelectedScreen.transfer(.interbank), selection: $model.screen) {
                                    TransferView(transferType: .interbank)
                                } label: {
                                    Text("transfers_button_national")
                                        .listNavigation(image: "IconTransferInterbank")
                                }
                                .isDetailLink(false)
                                NavigationLink(tag: SelectedScreen.transfer(.intrabank), selection: $model.screen) {
                                    TransferView(transferType: .intrabank)
                                } label: {
                                    Text("transfers_button_internal")
                                        .listNavigation(image: "IconTransferIntrabank")
                                }
                                .isDetailLink(false)
                                NavigationLink(tag: SelectedScreen.templates, selection: $model.screen) {
                                    TransferTemplates()
                                } label: {
                                    Text("transfers_button_templates")
                                        .listNavigation(image: "IconTransferTemplates")
                                }
                                .isDetailLink(false)
                                NavigationLink(tag:SelectedScreen.pending, selection: $model.screen) {
                                    PendingTransfers()
                                } label: {
                                    Text("transfers_button_pending")
                                        .listNavigation(image: "IconWaitingTransfers")
                                }
                                .isDetailLink(false)
                                NavigationLink(tag: SelectedScreen.history, selection: $model.screen) {
                                    DocumentsHistoryView()
                                } label: {
                                    Text("transfers_button_history")
                                        .listNavigation(image: "IconTransferHistory")
                                }
                                .isDetailLink(false)
                            }.padding()
                        }
                    }
                }
                .padding(.horizontal)
                .padding(.bottom)
                .onAppear(perform: {
                    Tool.showTabBar()
                })
            }
            .hiddenNavigationBarStyle()
        }
        .environmentObject(model)
        .environmentObject(permissionsModel)
        .environmentObject(accountPickerViewModel)
        .onDisappear(perform: {
            accountPickerViewModel.clearData()
        })
        .operationStatusModal(
            operationStatus: $model.operationStatus,
            prefix: "transfer",
            flowComletion: {
                model.screen = .history
            },
            acceptOTP: {
                model.makeTransferUsingFallback()
            },
            declineOTP: nil,
            otpHandler: { otp, pin in
                model.makeTransferUsingFallback(otp: otp, pin: pin)
            })
        
    }
}

struct TransfersTestDashboard_Previews: PreviewProvider {
    static var previews: some View {
        TransfersDashboard()
            .environment(\.locale, .init(identifier: "bg"))
            .environmentObject(PermissionsModel.shared)
    }
}
