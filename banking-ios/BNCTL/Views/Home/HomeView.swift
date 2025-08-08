//
//  HomeView.swift
//  HomeView
//
//  Created by Evgeniy Raev on 11.08.21.
//

import SwiftUI

struct HomeView: View {
    
    @EnvironmentObject private var permissionsModel: PermissionsViewModel
    
    @State private var screenSelection:Screen = .accounts
    
    init() {
        UINavigationBar.appearance().titleTextAttributes = [
            .foregroundColor: UIColor(named: "PrimaryColor") ?? UIColor.systemBlue
        ]
        UINavigationBar.appearance().largeTitleTextAttributes = [
            .foregroundColor: UIColor(named: "PrimaryColor") ?? UIColor.systemBlue
        ]
        if #available(iOS 15.0, *) {
            let appearance = UITabBarAppearance()
            UITabBar.appearance().scrollEdgeAppearance = appearance
        }
    }
    
    var body: some View {
        TabView(selection:$screenSelection) {
            AccountsDashboard()
                .tabItem {
                    Image("NavIconAccounts")
                    Text("nav_dashboard")
                }
                .tag(Screen.accounts)
            TransfersDashboard()
                .tabItem {
                    Image("NavIconTransfers")
                    Text("nav_transfers")
                }
                .tag(Screen.transfers)
            InformationDashboardView()
                .tabItem {
                    Image("NavIconInformation")
                    Text("nav_information")
                }
                .tag(Screen.information)
        }
        .padding(0)
        .environmentObject(permissionsModel)
    }
    
    enum Screen {
        case accounts
        case transfers
        case information
    }
}

struct HomeView_Previews: PreviewProvider {
        
    static var previews: some View {
        HomeView()
            .environmentObject(PermissionsModel.shared)
    }
}
