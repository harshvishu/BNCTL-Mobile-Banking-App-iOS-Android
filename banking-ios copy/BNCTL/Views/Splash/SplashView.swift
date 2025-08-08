//
//  SplashView.swift
//  BNCTL
//
//  Created by Prem's on 03/02/23.
//

import SwiftUI

struct SplashView: View {

    @State var isActive: Bool = false
    @EnvironmentObject private var login: LoginModel

        var body: some View {
            ZStack {
                if self.isActive {
                    LoginView()
                } else {
                    Image("Logo")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 300, height: 300)
                }
            }
            .onAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                    withAnimation {
                        self.isActive = true
                    }
                }
            }
        }
}

struct SplashView_Previews: PreviewProvider {
    static var previews: some View {
        SplashView()
    }
}
