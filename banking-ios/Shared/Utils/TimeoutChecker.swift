//
//  TimeoutChecker.swift
//  MobileBanking
//
//  Created by Kavitha Sambandam on 16/12/22.
//

import Foundation

class TimeoutChecker {
    static let shared = TimeoutChecker()

    func loginTimedOut() -> Bool {
        let bgTime = DataStore.instance.getLastActiveTime() ?? Date()
        let interval = Int(Date().timeIntervalSince(bgTime))
        DataStore.instance.removeLastActiveTime()
        if interval > LOGIN_TIMEOUT {
            LoginViewModel().logoutState()
            return true
        }
        else {
            return false
        }
    }

    func checkLoginStatus() {
        CurrentUserService().getCurrentUser() { error, currentUserResponse in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if currentUserResponse != nil{
                    Logger.I(tag: APP_NAME, "Login is valid")
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }
}
