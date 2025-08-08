//
//  PendingTransfers.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 26.11.21.
//

import SwiftUI

struct PendingTransfers: View {
    @StateObject var model = PendingTransfersViewModel()
    @EnvironmentObject var transfersDashboard: TransferDashboardViewModel
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>

    @State var showAlert: Bool = false
    @State var alertMessage: String = ""
    
    var body: some View {
        VStack(spacing: 0){
            TitleViewWithBackAndRItem(title: "transfers_button_pending") {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconClose")
                }
            }
            if (model.transfers.isEmpty) {
                Spacer()
                if (model.isLoadingTransfers) {
                    ActivityIndicator(label: "transfer_pending_label_loading")
                } else {
                    Text("transfer_pending_label_no_pending_transfers")
                }
                Spacer()
            } else {
                Checkable(state: model.selectAllState) {
                    model.selectAll()
                } label: {
                    Text("transfer_pending_label_select_all")
                        .foregroundColor(Color("PrimaryTextColor"))
                        .font(.system(size: Dimen.TextSize.info, design: .default))
                }
                .disabled(model.disabledTypes.contains(.individual))
                .padding()
                .background(Color.white.cornerRadius(16))
                .padding()
                
                ScrollView {
                    VStack(spacing: Dimen.Spacing.short) {
                        ForEach(model.transfers) { transfer in
                            Checkable(state: model.transferState(transfer)) {
                                model.toggleTransfer(transfer)
                            } label: {
                                Text(transfer.transactionType)
                                    .font(.system(size: Dimen.TextSize.info, design: .default))
                                    .foregroundColor(Color("PrimaryTextColor"))
                            } content: {
                                Spacer().frame(height: Dimen.Spacing.regular)
                                PendingTransferDataView(pendingTransfer: transfer)
                            }
                            .disabled(
                                model.selected.contains(transfer)
                                    ? false
                                    : model.disabledTypes.contains(transfer.type)
                                )
                            .padding()
                            .background(Color.white.cornerRadius(16))
                        }
                        .padding(.horizontal)
                    }
                }

                HStack(spacing: Dimen.Spacing.regular) {
                    Group {
                        Button {
                            if model.selected.count > 0 {
                                transfersDashboard.rejectSelectedTransfer(ids: model.selected.map { $0.id })
                            } else {
                                showAlert = true
                                alertMessage = "error_transfer_not_selected_reject"
                            }
                        } label: {
                            Text("transfer_pending_button_reject")
                                .frame(maxWidth:.infinity)
                                .padding(Dimen.Spacing.short)
                                .foregroundColor(Color("PrimaryButtonColor"))
                        }

                        Button {
                            if model.selected.count > 0 {
                                transfersDashboard.approveSelectedTransfers(ids: model.selected.map { $0.id })
                            } else {
                                showAlert = true
                                alertMessage = "error_transfer_not_selected_approve"
                            }
                        } label: {
                            Text("transfer_pending_button_confirm")
                                .padding(Dimen.Spacing.short)
                                .frame(maxWidth:.infinity)
                                .background(Color("PrimaryButtonColor"))
                                .foregroundColor(.white)
                        }
                    }
                    .overlay(
                        RoundedRectangle(cornerRadius: 5)
                            .stroke(Color("PrimaryButtonColor"), lineWidth: 1)
                    )
                    .compatibleAllert(
                        showAlert: $showAlert,
                        titleKey: LocalizedStringKey(alertMessage),
                        defailtLabel: LocalizedStringKey("error_transfer_alert_ok")
                        )
                }
                .padding()
            }

        }
        .background(Color("background").edgesIgnoringSafeArea(.all))
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .onAppear {
            model.getPendingTransfers()
        }
    }
}

struct PendingTransferDataView: View {
    let pendingTransfer: PendingTransfer

    var body: some View {
        VStack {
//            Group {
                if let beneficiary = pendingTransfer.beneficiaryName {
                    row(title: "transfer_pending_label_card_beneficiary", data: beneficiary)
                }
                
                if let descArray = pendingTransfer.additionalDescArray,
                   descArray.count > 1
                {
                    if let description = pendingTransfer.description {
                        row(
                            title:Text("transfer_pending_label_card_additional_reason \(1)"),
                            data: Text(description)
                        )
                    }
                    let count = descArray.count
                    ForEach(0..<count) { index in
                        let desc = descArray[index]
                        row(
                            title: Text("transfer_pending_label_card_additional_reason \(index+2)"),
                            data: Text(desc)
                        )
                    }
                } else {
                    if let description = pendingTransfer.description {
                        row(title: "transfer_pending_label_card_reason", data: description)
                    }
                    if let desc = pendingTransfer.additionalDescription {
                        row(
                            title: "transfer_pending_label_card_additional_reason",
                            data: desc
                        )
                    }
                    
                }
                if let numberOfDocuments = pendingTransfer.numberOfDocuments {
                    row(title: "transfer_pending_label_card_documents", data: String(numberOfDocuments))
                }
                row(title:"transfer_pending_label_card_from_account", data: pendingTransfer.sourceAccount)
                if let destinationAccount = pendingTransfer.destinationAccount {
                    row(title:"transfer_pending_label_card_to_account", data: destinationAccount)
                }
                Spacer()
                    .frame(height: 16)
                HStack {
                    Spacer()
                    Group {
                        Text(pendingTransfer.amount.toCurrencyFormatter())
                        Text(pendingTransfer.currency)
                    }
                    .foregroundColor(Color("PrimaryTextColor"))
                    .font(.system(size: Dimen.TextSize.amountInfo, weight: .bold, design: .default))
                }
//            }
        }
    }
    
    func row(title: String, data: String) -> some View {
        return HStack {
            Text(LocalizedStringKey(title))
                .font(.system(size: Dimen.TextSize.infoSmall))
                .foregroundColor(Color("SecondaryTextColor"))
            Spacer()
            Text(data)
                .font(.system(size: Dimen.TextSize.infoSmall))
                .foregroundColor(Color("PrimaryTextColor"))
        }
    }
    
    func row(title: Text, data: Text) -> some View {
        return HStack {
            title
                .font(.system(size: Dimen.TextSize.infoSmall))
                .foregroundColor(Color("SecondaryTextColor"))
            Spacer()
            data
                .font(.system(size: Dimen.TextSize.infoSmall))
                .foregroundColor(Color("PrimaryTextColor"))
        }
    }
}

fileprivate extension PendingTransfer {
    var additionalDescArray: [String]? {
        additionalDescription?.components(separatedBy: "\n")
    }
}

struct PendingTransfers_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            PreviewWrapper()
        }
    }
        
    struct PreviewWrapper:View {
        let model:PendingTransfersViewModel
        
        init() {
            model = PendingTransfersViewModel()
            model.transfers = PendingTransfer.previewList
        }
        
        var body: some View {
            PendingTransfers(model: model)
        }
    }
}
