//
//  LoginForm.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 17.11.22.
//

import SwiftUI

struct LoginForm: View {
    @StateObject var loginViewModel = LoginViewModel()
    
    @State var forgotPassword = false
    
    @State private var usernameError: String?
    @State private var passwordError: String?
    @State var username: String = ""
    @State var password: String = ""

    func clearLogin() {
        username = ""
        password = ""
    }
    
    var body: some View {
        VStack {
            if let error = loginViewModel.loginError {
                Group {
                    switch error {
                    case .wrongCredentials:
                        Text("error_authentication_invalid_credentials_login")
                    case .scaCancelled:
                        Text("error_authentication_sca_cancelled_login")
                    case .networkEroor:
                        Text("error_no_connection")
                    case .serverError:
                        Text("error_authentication_login")
                    case .noFallback:
                        Text("error_authentication_no_fallback")
                    case .invalidPhoneNumber:
                        Text("error_authentication_invalid_mobile_number_error_login")
                    default:
                        EmptyView()
                    }
                }
                .foregroundColor(Color.red)
                .fixedSize(horizontal: false, vertical: true)
                .multilineTextAlignment(.center)
                .padding(.bottom, Dimen.Spacing.huge)
            }
            
            Spacer()
                .frame(height: Dimen.Spacing.regular)

            VStack(spacing: Dimen.Spacing.huge) {
                FloatingLabelTextField(
                    label: Text("login_placeholder_username"),
                    text: $username,
                    error: usernameError
                )
                .textContentType(.username)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                FloatingLabelSecureField(
                    label: Text("login_placeholder_password"),
                    text: $password,
                    error: passwordError
                ) {
                    if (!username.isEmpty &&
                        !password.isEmpty) {
                        loginViewModel.login(
                            username: username,
                            password: password
                        )
                    }
                }
                .textContentType(.password)
            }
            
            Spacer()
                .frame(height: Dimen.Spacing.huge)

            VStack(spacing: Dimen.Spacing.huge) {
                Button {
                    usernameError = username.isEmpty
                        ? "common_error_field_required" : nil
                    passwordError = password.isEmpty
                        ? "common_error_field_required" : nil
                    
                    if (usernameError?.isEmpty ?? true
                        && passwordError?.isEmpty ?? true)
                    {
                        loginViewModel.login(
                            username: username,
                            password: password
                        )
                    }
                } label: {
                    Text("login_button_sign_in")
                        .commonButtonStyle()
                }
                
                Button {
                    self.forgotPassword = true
                } label: {
                    Text("login_button_forgot_password")
                        .foregroundColor(Color("PrimaryButtonColor"))
                }
                .compatibleAllert(
                    showAlert: $forgotPassword,
                    titleKey: "login_dialog_forgot_password_title",
                    defailtLabel: "OK"
                )
            }
        }
        .onAppear(perform: {
            loginViewModel.clearLogin = clearLogin
        })
        .operationStatusModal(
            operationStatus: $loginViewModel.loginStatus,
            prefix: "login") {
                
            } acceptOTP: {
                loginViewModel.loginWithOPT(
                    username: username,
                    password: password
                )
            } otpHandler: { otp, pin in
                loginViewModel.confirmLogin(
                    username: username,
                    password: password,
                    otp:otp,
                    pin:pin
                )
            }
    }
}

struct LooginForm_Previews: PreviewProvider {
    static var previews: some View {
        LoginForm()
            .previewLayout(.sizeThatFits)
    }
}
