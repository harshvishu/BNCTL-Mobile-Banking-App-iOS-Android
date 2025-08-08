//
//  PayeeDetails.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 15/04/23.
//

import SwiftUI

struct PayeeDetails: View {
    @Environment(\.presentationMode) var presentationMode
    
    @StateObject var model: EditPayeeViewModel
    @State var showAlert:Bool = false
    @State private var isEditingPayee = false
    
    var body: some View {
        ZStack {
            Color("background").edgesIgnoringSafeArea(.all)
            
            VStack {
                TitleViewWithBackAndRItem(title: LocalizedStringKey(model.name)) {
                    Menu {
                        Button {
                            isEditingPayee = true
                        } label: {
                            Text("payees_label_menu_edit")
                                .foregroundColor(.red)
                        }
                       
                        Button {
                            model.deletePayee()
                        } label: {
                            Text("payees_label_menu_delete")
                                .foregroundColor(.red)
                        }
                    } label: {
                        Image(systemName: "ellipsis")
                            .font(.title)
                            .padding(.horizontal)
                    }

                }
                ScrollView {
                    VStack(alignment: .leading, spacing: Dimen.Spacing.regular) {
                        DetailRowView(
                            title: "payees_label_transfer_type",
                            detail: model.transferType?.displayName ?? "",
                            hasIcon: false,
                            backgroundColor: Color.clear,
                            cornerRadius: 0.0
                        )
                        if !model.email.isEmpty {
                            DetailRowView(
                                title: "payees_label_payee_email",
                                detail: model.email,
                                hasIcon: false,
                                backgroundColor: Color.clear,
                                cornerRadius: 0.0
                            )
                        }
                        DetailRowView(
                            title: "payees_label_account_number",
                            detail: model.accountNumber,
                            hasIcon: false,
                            backgroundColor: Color.clear,
                            cornerRadius: 0.0
                        )
                        
                        if let currency = model.currency?.rawValue {
                            DetailRowView(
                                title: "payees_label_payee_currency",
                                detail: currency,
                                hasIcon: false,
                                backgroundColor: Color.clear,
                                cornerRadius: 0.0
                            )
                        }
                    }
                    .padding(.horizontal)
                }
            }
            
            // MARK: Edit Payee
            NavigationLink(isActive: $isEditingPayee) {
                EditPayeeView(model: model)
            } label: {
                EmptyView()
            }
        }
        .hiddenNavigationBarStyle()
        .onReceive(model.$payeeDeleted) { onPayeeDelete in
            if onPayeeDelete {
                presentationMode.wrappedValue.dismiss()
            }
        }
    }
}

struct PayeeDetails_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            PayeeDetails(model: EditPayeeViewModel(editingMode: .edit(Payee(walletAccountNumber: nil, accountNumber: "123456", type: PayeeType(rawValue: "bank")!, userId: "123456", walletProvider: nil, bank: "BNCTL", notificationLanguage: nil, accountTypeId: "Current", isDeleted: false, name: "Same-bank-Test", currency: "USD", payeeId: "12345678", email: nil, swift: "BNCTL"))))
        }
    }
}
