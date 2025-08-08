//
//  MobileBankingApp.swift
//  MobileBanking
//
//  Created by Kavitha Sambandam on 07/11/22.
//

import SwiftUI

@main
struct MobileBankingApp: App {
    @Environment(\.scenePhase) private var scenePhase
    let login = LoginModel.shared
    let permissionsModel = PermissionsModel.shared
    @AppStorage("preferedLanguage") var preferedLanguage:String = Language.default.code

    var body: some Scene {
        WindowGroup {
            LandingView()
                .modifier(DesingSystemSetupModifier())
                .modifier(VersionCheckViewModifier())
                .modifier(CheckTermsAndConditionsState())
                .environmentObject(login)
                .environmentObject(permissionsModel)
                .environment(\.locale, Locale(identifier: preferedLanguage))

        }
        .onChange(of: scenePhase) { newScenePhase in
            switch newScenePhase {
            case .active:
                print("scene is now active!")
                if TimeoutChecker.shared.loginTimedOut() == false {
                    TimeoutChecker.shared.checkLoginStatus()
                }
            case .inactive:
                print("scene is now inactive!")
                BasicRequest.shared.tryFinishActiveRequests()
            case .background:
                print("scene is now in the background!")
                DataStore.instance.setLastActiveTime()
            @unknown default:
                print("Apple must have added something new!")
            }
        }
    }
}
