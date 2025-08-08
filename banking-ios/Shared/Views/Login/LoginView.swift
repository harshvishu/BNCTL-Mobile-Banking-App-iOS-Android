//
//  LoginView.swift
//  Shared
//
//  Created by Evgeniy Raev on 10.08.21.
//

import SwiftUI

struct LoginView: View, KeyboardReadable {
    @EnvironmentObject private var login: LoginModel
    
    var body: some View {
        NavigationView {
            GeometryReader { geometry in
                ScrollView([.vertical]) {
                    VStack {
                        Group { // Language Button & Logo
                            Spacer()
                                .frame(minHeight: Dimen.Spacing.huge)
                            
                            VStack(spacing: Dimen.Spacing.large) {
                                Image("Logo")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(height: 36.0)
                                Text("name")
                                    .foregroundColor(Color("PrimaryColor"))
                            }
                            Spacer()
                                .frame(minHeight: Dimen.Spacing.huge)
                        }
                        
                        LoginForm()
                    }
                    .padding(Dimen.Spacing.large)
                    .frame(minHeight: geometry.size.height)
                }
            }
            .hiddenNavigationBarStyle()
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    
    @State static var lang: String = "en"
    
    static var previews: some View {
        LoginView()
            .environmentObject(LoginModel())
            .environment(\.locale, .init(identifier: "bg"))
    }
}
