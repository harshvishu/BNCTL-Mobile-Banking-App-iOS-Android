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
                            TransfersDashboardContent(model: model)
                            .padding()
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
