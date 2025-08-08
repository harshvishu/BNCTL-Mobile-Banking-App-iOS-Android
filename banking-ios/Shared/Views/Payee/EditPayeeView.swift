//
//  EditPayeeView.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 15/04/23.
//

import SwiftUI

struct EditPayeeView: View {
    @Environment(\.presentationMode) var presentationMode
    
    @StateObject var model: EditPayeeViewModel
    @State var showAlert:Bool = false
    
    var body: some View {
        ZStack {
            VStack {
                TitleViewWithBack(title: LocalizedStringKey(getNavigationTitle()))
                
                ScrollViewReader { proxi in
                    VStack {
                        
                        ScrollView {
                            VStack(alignment: .leading, spacing: Dimen.Spacing.huge) {
                                // MARK: Supported Transfer Type
                                VStack(alignment: .leading) {
                                    Text("payees_label_transfer_type")
                                        .foregroundColor(Color("SecondaryTextColor"))
                                    
                                    Picker("Transfer Type", selection: $model.transferType) {
                                        ForEach(model.supportedTransferTypes) {
                                            Text($0.displayName ?? "")
                                                .tag($0.id)
                                        }
                                    }
                                    .pickerStyle(.menu)
                                    .id("transferType")
                                }
                                
                                // MARK: Payee Details
                                FloatingLabelLimitTextField(
                                    label: Text("payees_label_payee_name"),
                                    text: $model.name,
                                    error: model.formErrors["name"],
                                    limit: 36
                                )
                                .textContentType(.name)
                                .autocapitalization(.none)
                                .disableAutocorrection(true)
                                .id("name")
                                
                                FloatingLabelLimitTextField(
                                    label: Text("payees_label_payee_email"),
                                    text: $model.email,
                                    error: model.formErrors["email"],
                                    limit: 36
                                )
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                                .disableAutocorrection(true)
                                .id("email")
                                
                                FloatingLabelLimitTextField(
                                    label: Text("payees_label_account_number"),
                                    text: $model.accountNumber,
                                    error: model.formErrors["accountNumber"],
                                    limit: 36
                                )
                                .keyboardType(.numberPad)
                                .id("accountNumber")
                                
                                if let currency = model.currency {
                                    FloatingLabelTextField(label: Text("payees_label_payee_currency"), text: .constant(currency.rawValue))
                                        .disabled(true)
                                }
                            }
                            .padding(.horizontal, Dimen.Spacing.large)
                        }
                        
                        Spacer()
                        
                        // MARK: Next Button
                        Button {
                            if model.formValidation() {
                                switch model.editingMode {
                                case .create:
                                    model.createPayee()
                                case .edit:
                                    model.updatePayee()
                                }
                            } else {
                                withAnimation {
                                    let key = [
                                        "transferType", "name", "email", "accountNumber"
                                    ].first { key in
                                        return model.formErrors[key] != nil
                                    }
                                    proxi.scrollTo(
                                        key,
                                        anchor: .top
                                    )
                                }
                            }
                            
                            
                        } label: {
                            Text("common_button_next").commonButtonStyle()
                        }
                        .padding(.horizontal)
                    }
                }
                .padding(.horizontal)
            }
        }
        .loading(isLoading: $model.isLoading)
        .hiddenNavigationBarStyle()
        .onAppear {
            if model.transferType == nil {
                model.transferType = model.supportedTransferTypes.first
            }
        }
        .onReceive(model.$formErrors, perform: { errors in
            if let _ = errors["alert"] {
                showAlert = true
            } else if let _ = errors["bank_api"] {
                showAlert = true
            } else if let _ = errors["success"] {
                showAlert = true
            }
        })
        .compatibleAllert(
            showAlert: $showAlert,
            titleKey: LocalizedStringKey(
                model.formErrors["alert"]
                ?? model.formErrors["bank_api"]
                ?? model.formErrors["success"]
                ?? "error_something_went_wrong"
            ),
            defailtLabel: "error_transfer_alert_ok",
            cancelLabel: nil,
            defaultAction: defaultAlertAction()
        )
    }
    
    fileprivate func defaultAlertAction() -> (() -> Void)? {
        if model.formErrors["bank_api"] != nil
            || model.formErrors["success"] != nil
        {
            return {
                presentationMode.wrappedValue.dismiss()
                model.formErrors["bank_api"] = nil
                model.formErrors["success"] = nil
            }
        } else {
            return nil
        }
    }
}

struct EditPayeeView_Previews: PreviewProvider {
    struct Preview: View {
        @State private var model: EditPayeeViewModel = EditPayeeViewModel(editingMode: .create)
        
        var body: some View {
            EditPayeeView(model: model)
        }
    }
    
    static var previews: some View {
        ZStack {
            Color("background").edgesIgnoringSafeArea(.all)
            NavigationView {
                Preview()
            }
        }
    }
}

extension EditPayeeView {
    enum EditingMode {
        case create
        case edit(Payee)
    }
}

extension EditPayeeView {
    func getNavigationTitle() -> String {
        switch model.editingMode {
        case .edit:
            return "payees_label_navigation_title_edit"
        case .create:
            return "payees_label_navigation_title_create"
        }
    }
}
