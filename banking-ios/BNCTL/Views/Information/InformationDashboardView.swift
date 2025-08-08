//
//  InformationDashboardView.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 31/03/23.
//

import SwiftUI

struct InformationDashboardView: View {

@StateObject var loginViewModel = LoginViewModel()
    
@State var showAlert:Bool = false;

var body: some View {
    NavigationView {
        ZStack {
            Color("background").edgesIgnoringSafeArea(.all)
            VStack {
                TitleViewSimple(title: "nav_information")
                ScrollView {
                    VStack {
                        ZStack {
                            Rectangle()
                                .foregroundColor(.white)
                                .cornerRadius(15)
                            VStack(alignment: .leading, spacing: 15) {
                                // TODO: FIXME: Icon for Manage Payee missing
                                ListNavItem(label: "information_button_manage_payee", image: "IconNews") {
                                    ManagePayeesView()
                                }
                                ListNavItem(label: "information_button_settings", image: "IconSettings") {
                                    SettingsView()
                                }
                            }.padding()
                        }
                        .padding(.bottom, 10)
                        ZStack {
                            Rectangle()
                                .foregroundColor(.white)
                                .cornerRadius(15)
                            VStack(alignment: .leading, spacing: 15) {
                                ListButtonItem(label: "information_button_logout", image: "IconLogout") {
                                    showAlert = true
                                }
                                .alert(isPresented: $showAlert) {
                                    Alert(title: Text("information_button_logout"), message: Text("information_logout_dialog_text"), primaryButton: .destructive(Text("information_logout_dialog_confirmation_text"), action: {
                                        withAnimation {
                                            loginViewModel.logout()
                                        }
                                    }), secondaryButton: .cancel(Text("common_reject_message")))
                                }
                            }.padding()
                        }
                        .fixedSize(horizontal: false, vertical: true)
                    }
                    .padding(.horizontal)
                }
                .onAppear {
                    Tool.showTabBar()
                }
            }
        }
        .hiddenNavigationBarStyle()
    }
}
}

struct InformationDashboardView_Previews: PreviewProvider {

static var previews: some View {
    InformationDashboardView()
}
}

