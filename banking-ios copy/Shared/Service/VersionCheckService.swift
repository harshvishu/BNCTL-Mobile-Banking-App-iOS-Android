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
            
            BasicRequest.shared.request(url: .version, method: .get, parameters: emptyBody) { _, result, error in
                
                if let error = error {
                    print(error.localizedDescription)
                    continuation.resume(throwing: error)
                }else{
                    if let resultData = result{
                        do{
                            let decoder = JSONDecoder()
                            decoder.keyDecodingStrategy = .convertFromSnakeCase
                            let appVersion = try decoder.decode(
                                CheckVersionResponse.self,
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
    
    struct CheckVersionResponse:Decodable {
        
        
        let result:VersionField
        
        struct VersionField:Decodable {
            let version:MobileAppVersion
        }
        /*
         {
             "result": {
                 "version": {
                     "latest": {
                         "versionName": "0.25.13",
                         "productVersion": 1
                     },
                     "minimum": {
                         "versionName": "0.25.13",
                         "productVersion": 1
                     }
                 }
             }
         }
        */
    }
}
