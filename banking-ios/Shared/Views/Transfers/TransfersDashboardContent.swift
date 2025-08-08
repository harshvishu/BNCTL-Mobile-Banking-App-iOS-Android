//
//  TransfersDashboardContent.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 04/04/23.
//

import SwiftUI

struct TransfersDashboardContent: View {
    @ObservedObject var model: TransferDashboardViewModel
    
    var body: some View {
        VStack(alignment: .leading, spacing: 15) {
            NavigationLink(tag: SelectedScreen.transfer(.betweenacc), selection: $model.screen) {
                TransferView(transferType: .betweenacc)
            } label: {
                Text("transfers_button_between_accounts")
                    .listNavigation(image: "IconTransferBetweenOwnAccounts")
            }
            .isDetailLink(false)
            NavigationLink(tag: SelectedScreen.history, selection: $model.screen) {
                DocumentsHistoryView()
            } label: {
                Text("transfers_button_history")
                    .listNavigation(image: "IconTransferHistory")
            }
            .isDetailLink(false)
        }
    }
}

struct TransfersDashboardContent_Previews: PreviewProvider {
    static var previews: some View {
        TransfersDashboardContent(model: TransferDashboardViewModel())
            .environmentObject(PermissionsModel.shared)
    }
}
