//
//  LoginService.swift
//  Allianz (iOS)
//
//  Created by Prem's on 02/11/21.
//

import Foundation

class LoginService {
    
    func login(
        params: LoginRequestBody,
        completionHandler: @escaping (
            _ error: Error?,
            _ loginResponse: LoginResponse?
        ) -> Void
    ) {
        BasicRequest.shared.request(
            url: .login,
            method: .post,
            parameters: params,
            isExtendSession: true
        ) { statusCode, result, error in
            if let error = error {
                // Networking and Server errors
                completionHandler(error, nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let loginData = try decoder.decode(LoginResponse.self, from: resultData)
                        completionHandler(nil, loginData)
                    } catch {
                        // Parsing error
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(ResultError.parseError, nil)
                    }
                } else {
                    // No result to parse
                    completionHandler(ResultError.noResultError, nil)
                }
            }
        }
    }
    
    func loginWithFallback(
        params: LoginRequestBody,
        completionHandler: @escaping (
            _ error: Error?,
            _ loginResponse: FallbackResponse.Result?
        ) -> Void
    ) {
        BasicRequest.shared.request(
            url: .loginFallback,
            method: .post,
            parameters: params,
            isExtendSession: true
        ) { statusCode, result, error in
            if let error = error {
                // Networking and Server errors
                completionHandler(error, nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let loginData = try decoder.decode(FallbackResponse.self, from: resultData)
                        completionHandler(nil, loginData.result)
                    } catch {
                        // Parsing error
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(ResultError.parseError, nil)
                    }
                } else {
                    // No result to parse
                    completionHandler(ResultError.noResultError, nil)
                }
            }
        }
    }
    
    func confirmFallback(
        params: LoginRequestBody,
        completionHandler: @escaping (
            _ error: Error?,
            _ loginResponse: LoginResponse?
        ) -> Void
    ) {
        BasicRequest.shared.request(
            url: .loginFallbackConfirm,
            method: .post,
            parameters: params,
            isExtendSession: true
        ) { statusCode, result, error in
            //TODO: move to common handler
            if let error = error {
                // Networking and Server errors
                completionHandler(error, nil)
            } else {
                if let resultData = result {
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let loginData = try decoder.decode(LoginResponse.self, from: resultData)
                        completionHandler(nil, loginData)
                    } catch {
                        // Parsing error
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(ResultError.parseError, nil)
                    }
                } else {
                    // No result to parse
                    completionHandler(ResultError.noResultError, nil)
                }
            }
        }
    }
    
    func logout(
        completionHandler: @escaping (
            _ statusCode: Int?,
            _ error: AuthenticationError?
        ) -> Void
    ) {
        let emptyBody:EmptyBody? = nil
        
        BasicRequest.shared.request(
            url: .logout,
            method: .post,
            parameters: emptyBody,
            isExtendSession: true
        ) { statusCode, result, error in
            if let error = error {
                print(error.localizedDescription)
                completionHandler(statusCode, error as? AuthenticationError)
            }else{
                if let resultData = result{
                    do {
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let _ = try decoder.decode(LogoutResponse.self, from: resultData)
                        completionHandler(statusCode ,nil)
                    } catch {
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(statusCode, .custom(errorMessage: "Decoding error"))
                    }
                } else {
                    completionHandler(statusCode, .custom(errorMessage: "No Data"))
                }
            }
        }
    }
    
    // Model
    
    struct LoginRequestBody: Codable {
        let username: String
        let password: String
        let smsCode:String?
        let pin:String?
    }
    
    struct LoginResponse: Codable {
        
        struct Auth: Codable {
            let usernameChanged: Bool?
            let passwordChanged: Bool?
            let username: String?
        }
        
        struct UserRole: Codable {
            let name: String
            let attributes: Dictionary<String, String>
        }

        struct User: Codable {
            let userRoles: [UserRole]?
        }
        
        struct Result: Codable {
            let accessToken: String?
            let expiresIn: Double?
            let refreshToken: String?
            let refreshTokenExpiresIn: Int?
            let auth: Auth?
            let user: User?
        }
        
        let result: Result
    }
    
    struct FallbackResponse:Decodable {
        struct Result:Decodable {
            let smsSent:Bool
            let usePin:Bool
        }
        
        let result:Result
    }
    
    struct LoginErrorResponse:Decodable {
        struct Error:Decodable {
            let code:String // "AuthenticationException",
            let message:String // "Authentication fall-back failure",
            let target:String // "errFallBackAgreement"
            
        }
        let error:Error
        let method:String // "allianz.auth.login",
        let type:String // "allianz.errFallBackAgreement",
        let message:String // "Authentication fall-back failure"
    }
    
    struct LogoutResponse:Codable {
        
    }
}






