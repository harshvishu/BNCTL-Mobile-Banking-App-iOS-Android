//
//  VersionCheckService.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 10.01.23.
//

import Foundation

class VersionCheckService {
    
    func checkVersion() async throws -> MobileAppVersion {
        return try await withCheckedThrowingContinuation({ continuation in
            let emptyBody:EmptyBody? = nil
            
            BasicRequest.shared.request(url: .config(.version), method: .get, parameters: emptyBody) { _, result, error in
                
                if let error = error {
                    print(error.localizedDescription)
                    continuation.resume(throwing: error)
                }else{
                    if let resultData = result{
                        do{
                            let decoder = JSONDecoder()
                            let appVersion = try decoder.decode(
                                DataStructure<VersionResponse>.self,
                                from: resultData
                            )
                            
                            continuation.resume(returning: appVersion.result.version)
                        } catch DecodingError.valueNotFound(let type, let context) {
                            print("Type '\(type)' mismatch:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                        } catch DecodingError.typeMismatch(let type, let context) {
                            print("Type '\(type)' mismatch:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                        } catch DecodingError.keyNotFound(let key, let context){
                            print("Key '\(key)' not found:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                        } catch {
                            print(error.localizedDescription)
                        }
                    }else{
                        continuation.resume(throwing: UnhadeledError())
                    }
                }
            }
        })
    }
    func checkAll() async throws -> ConfigurationResoinse {
        return try await withCheckedThrowingContinuation({ continuation in
            let emptyBody:EmptyBody? = nil
            
            BasicRequest.shared.request(url: .config(.all), method: .get, parameters: emptyBody) { _, result, error in
                
                if let error = error {
                    print(error.localizedDescription)
                    continuation.resume(throwing: error)
                }else{
                    if let resultData = result{
                        do{
                            let dateFormatter = DateFormatter()
                            dateFormatter.locale = Locale(identifier: "bg-BG")
                            dateFormatter.dateFormat = DateFormatType.serverDateTime.rawValue
                            let decoder = JSONDecoder()
                            decoder.keyDecodingStrategy = .convertFromSnakeCase
                            decoder.dateDecodingStrategy = .formatted(dateFormatter)
                            let appVersion = try decoder.decode(
                                DataStructure<ConfigurationResoinse>.self,
                                from: resultData
                            )
                            continuation.resume(returning: appVersion.result)
                        } catch DecodingError.valueNotFound(let type, let context) {
                            print("Type '\(type)' mismatch:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                        } catch DecodingError.typeMismatch(let type, let context) {
                            print("Type '\(type)' mismatch:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                        } catch DecodingError.keyNotFound(let key, let context){
                            print("Key '\(key)' not found:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                        } catch {
                            print(error.localizedDescription)
                        }
                    }else{
                        continuation.resume(throwing: UnhadeledError())
                    }
                }
            }
        })
    }
    
    func checkState() async throws -> PushPrivisioningState {
        return try await withCheckedThrowingContinuation({ continuation in
            let emptyBody:EmptyBody? = nil
            
            BasicRequest.shared.request(url: .config(.pushProvisioning), method: .get, parameters: emptyBody) { _, result, error in
                
                if let error = error {
                    print(error.localizedDescription)
                    continuation.resume(throwing: error)
                }else{
                    if let resultData = result{
                        do{
                            let dateFormatter = DateFormatter()
                            dateFormatter.locale = Locale(identifier: "bg-BG")
                            dateFormatter.dateFormat = DateFormatType.serverDateTime.rawValue
                            let decoder = JSONDecoder()
                            
                            decoder.keyDecodingStrategy = .convertFromSnakeCase
                            decoder.dateDecodingStrategy = .formatted(dateFormatter)
                            let result = try decoder.decode(
                                DataStructure<PushProvisioningResponse>.self,
                                from: resultData
                            ).result.pushProvisioning
                            
                            continuation.resume(returning:result)
                            
                        } catch DecodingError.valueNotFound(let type, let context) {
                            print("Type '\(type)' mismatch:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                            continuation.resume(throwing: UnhadeledError())
                        } catch DecodingError.typeMismatch(let type, let context) {
                            print("Type '\(type)' mismatch:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                            continuation.resume(throwing: UnhadeledError())
                        } catch DecodingError.keyNotFound(let key, let context){
                            print("Key '\(key)' not found:", context.debugDescription)
                            print("codingPath:", context.codingPath)
                            continuation.resume(throwing: UnhadeledError())
                        } catch {
                            print(error.localizedDescription)
                            continuation.resume(throwing: UnhadeledError())
                        }
                    }else{
                        continuation.resume(throwing: UnhadeledError())
                    }
                }
            }
        })
    }
    
    struct ConfigurationResoinse:Decodable {
        let version:MobileAppVersion
        let pushProvisioning:PushPrivisioningState
    }
    
    struct VersionResponse:Decodable {
        let version:MobileAppVersion
    }
    struct PushProvisioningResponse:Decodable {
        let pushProvisioning:PushPrivisioningState
    }
}
