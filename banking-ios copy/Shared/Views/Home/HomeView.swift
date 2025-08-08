//
//  HomeView.swift
//  HomeView
//
//  Created by Evgeniy Raev on 11.08.21.
//

import SwiftUI

struct HomeView: View {
    
    @EnvironmentObject private var permissionsModel: PermissionsViewModel
    
//    let accountsModel = AccountsViewModel()
    
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
        TabView {
            AccountsDashboard()
                .tabItem {
                    Image("NavIconAccounts")
                    Text("nav_dashboard")
                }
            TransfersDashboard()
                .tabItem {
                    Image("NavIconTransfers")
                    Text("nav_transfers")
                }
            CardsDashboard()
                .tabItem {
                    Image("NavIconCards")
                    Text("nav_cards")
                }
            ServicesDashboardView()
                .tabItem {
                    Image("NavIconServices")
                    Text("nav_services")
                }
            InformationDashboardView()
                .tabItem {
                    Image("NavIconInformation")
                    Text("nav_information")
                }
        }
        .padding(0)

        .environmentObject(permissionsModel)
    }
}

struct HomeView_Previews: PreviewProvider {
        
    static var previews: some View {
        HomeView()
            .environmentObject(PermissionsModel.shared)
    }
}
