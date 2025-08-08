    //
    //  SettingsView.swift
    //  Allianz (iOS)
    //
    //  Created by Prem's on 26/11/21.
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
                                    ListNavItem(label: "information_button_news", image: "IconNews") {
                                        NewsView()
                                    }
                                    ListNavItem(label: "information_button_offices_and_atms", image: "IconBranchesAndATMs") {
                                        BranchesAndATMsView()
                                    }
                                    ListNavItem(label: "information_button_exchange_rates", image: "IconExchangeRates") {
                                        ExchangeRatesView()
                                    }
                                    ListNavItem(label: "information_button_settings", image: "IconSettings") {
                                        SettingsView()
                                    }
                                    ListNavItem(label: "information_button_switch_user", image: "IconSwitchAccountGroup") {
                                        ChangeProfileView()
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
