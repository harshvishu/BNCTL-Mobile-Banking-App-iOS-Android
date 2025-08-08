//
//  LoginViewModel.swift
//  LoginViewModel
//
//  Created by Evgeniy Raev on 10.08.21.
//

import Foundation
import SwiftUI

class LoginViewModel: ObservableObject {
    
    @Published var loginStatus = Confirmation<OperationStatus>(restricted: [
        .otpAvailabe,
        .wrongOtp
    ])
    @Published var loginError:LoginModel.LoginError?
    var clearLogin:() -> Void = {}
    
    func login(username:String, password:String) {
        loginStatus.status = .waiting
        let request = LoginService.LoginRequestBody(
            username: username,
            password: password,
            smsCode: nil,
            pin: nil
        )
        LoginService().login(params: request, completionHandler: handleLogin)
    }
    
    func handleLogin(error:Error?, result:LoginService.LoginResponse?) {
        let model = LoginModel.shared
            if let error = error {
                if let networkError = error as? NetworkError {
                    loginError = .networkEroor
                    loginStatus.status = nil
                    Logger.E(tag: APP_NAME, networkError.localizedDescription)
                    clearLogin()
                } else if let serverError = error as? ServerError {
                    switch serverError.error.target {
                        //TODO: make them enum
                    case "invalidPassword", "invalidUsername":
                        loginError = .wrongCredentials
                        loginStatus.status = nil
                        clearLogin()
                    case "invalidPhoneNumber":
                        self.loginError = .invalidPhoneNumber
                        self.loginStatus.status = nil
                        clearLogin()
                    case "scaCancelled":
                        loginError = .scaCancelled
                        loginStatus.status = nil
                        clearLogin()
                    case "errFallBackAgreement", "scaExpired", "errScaError":
                        loginStatus.status = .otpAvailabe
                    case "errInvalidSms":
                        self.loginStatus.status = .wrongOtp
                    case "errNoFallBack":
                        self.loginError = .noFallback
                        self.loginStatus.status = nil
                        clearLogin()
                    default:
                        Logger.E(tag: APP_NAME, "unhandaled error login status")
                        self.loginStatus.status = nil
                        //self.loginError =
                        clearLogin()
                    }
                    Logger.E(tag: APP_NAME, serverError.localizedDescription)
                }
            } else {
                if let loginResult = result,
                   let accessToken = loginResult.result.accessToken
                {
                    DataStore.instance.setLoginToken(token: accessToken)
                    self.loginStatus.status = nil
                    checkCurrentUser()
                } else {
                    model.isLoggedIn = .notLoggedIn
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                    clearLogin()
                }
            }
    }
    
    func loginWithOPT(username:String, password:String) {
        
        let request = LoginService.LoginRequestBody(
            username: username,
            password: password,
            smsCode: nil,
            pin: nil
        )
        
        LoginService().loginWithFallback(params: request) { [self] error, result in
            if let error = error {
                  if let networkError = error as? NetworkError {
                    Logger.E(tag: APP_NAME, networkError.localizedDescription)
                  } else if let serverError = error as? ServerError {
                      Logger.E(tag: APP_NAME, serverError.error.message)
                  }
            } else {
                if let loginResult = result
                {
                    if loginResult.usePin {
                        self.loginStatus.status = .waitingOtpWithPin
                    } else {
                        self.loginStatus.status = .waitingOtp
                    }
                    
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                    clearLogin()
                }
            }
        }
    }
    
    func confirmLogin(username:String, password:String, otp:String, pin:String?) {
        let request = LoginService.LoginRequestBody(
            username: username,
            password: password,
            smsCode: otp,
            pin: pin
        )
        
        LoginService().confirmFallback( params: request, completionHandler: handleLogin)
    }
    
    private func checkCurrentUser() {
        let permissionsModel = PermissionsModel.shared
        let model = LoginModel.shared
        
        PermissionsModel.shared.updateUserPermissions()

        CurrentUserService().getCurrentUser() { error, currentUserResponse in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let currentUserResult = currentUserResponse {
                    permissionsModel.permissions = currentUserResult.auth.permissions
                    model.isLoggedIn = .loggedIn
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }
    
    func logoutState() {
        LoginModel.shared.isLoggedIn = .notLoggedIn
        DataStore.instance.clearDataStore()
        PermissionsModel.shared.permissions.removeAll()
        UIApplication.shared.windows.first?.rootViewController?.dismiss(animated: true, completion: nil)
        //TODO: Clean all other view models
    }
    
    func logout() {
        PermissionsModel.shared.permissions.removeAll()
        LoginService().logout() { [self] statusCode, error in
            if statusCode == nil{
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
            logoutState()
        }
    }
}

class LoginModel: ObservableObject {
    static let shared = LoginModel()
    
    @Published var isLoggedIn: Status = .notLoggedIn
    @Published var currentUser:Customer?
    
    enum Status: Hashable {
        case notLoggedIn
        case loggedIn
    }
    
    enum LoginError:Error {
        case serverError
        case networkEroor
        case wrongCredentials
        case scaCancelled
        case errFallBackAgreement
        case noFallback
        case invalidPhoneNumber
        //case errScaError
    }
}
