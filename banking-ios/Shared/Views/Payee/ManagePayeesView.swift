//
//  ManagePayeesView.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 12/04/23.
//

import SwiftUI

struct ManagePayeesView: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var permissionsModel: PermissionsViewModel
    
    @StateObject var model = ManagePayeesViewModel()
    
    var body: some View {
        
        ZStack {
            Color("background").edgesIgnoringSafeArea(.all)
            // MARK: Navigation Bar
            VStack {

                TitleView(
                    title: "information_button_manage_payee",
                    leftItem: {
                        Button {
                            self.presentationMode.wrappedValue.dismiss()
                        } label: {
                            Image("IconBack")
                        }
                    },
                    rightItem: {
                        Button {
                            model.showAddPayee.toggle()
                        } label: {
                            // TODO: Missing ICON
                            Text("+")
                                .font(.title)
                                .padding(.horizontal)
                        }
                    }
                )
                
                if (model.isLoading && model.payees.isEmpty) {
                    ActivityIndicator(label: "payees_label_loading")
                } else if model.payees.isEmpty {
                    Text("payees_label_no_payees")
                        .foregroundColor(Color("SecondaryTextColor"))
                        .font(.system(size: Dimen.TextSize.info))
                        .multilineTextAlignment(.center)
                        .padding()
                }
                
                ScrollView {
                    // MARK: List
                    VStack(spacing: Dimen.Spacing.large) {
                        ForEach(model.payees) { payee in
                            PayeeListItem(payee: payee)
                        }
                    }
                }
            }
            .padding(.bottom)
            
            NavigationLink(isActive: $model.showAddPayee) {
                EditPayeeView(model: EditPayeeViewModel(editingMode: .create))
            } label: {
                EmptyView()
            }
            
        }
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .onAppear {
            Task(priority: .userInitiated) {
                model.fetchPayees()
            }
        }
        .compatibleAllert(
            showAlert: $model.showAllert,
            titleKey: LocalizedStringKey(model.error ?? "error_something_went_wrong"),
            defailtLabel: "Close"
        )
    }
}

struct ManagePayeesView_Previews: PreviewProvider {
    static var previews: some View {
        ManagePayeesView()
    }
}
