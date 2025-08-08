//
//  LandingView.swift
//  LandingView
//
//  Created by Evgeniy Raev on 11.08.21.
//

import SwiftUI

struct LandingView: View {
    @EnvironmentObject private var login: LoginModel
    
    var body: some View {
        Group {
            if (login.isLoggedIn == .loggedIn) {
                HomeView()
            } else {
                LoginView()
            }
        }
    }
}

struct LandingView_Previews: PreviewProvider {
    
    static var previews: some View {
        LandingView()
            .environmentObject(LoginModel())
    }
}
