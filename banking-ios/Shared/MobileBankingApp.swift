//
//  MobileBankingApp.swift
//  MobileBanking
//
//  Created by Kavitha Sambandam on 07/11/22.
//

import SwiftUI
import Firebase

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        PushProvisioningSetupModifier.setup()
        
        return true
    }
}


@main
struct MobileBankingApp: App {
    // register app delegate for Firebase setup
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

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
                .modifier(PushProvisioningSetupModifier())
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
