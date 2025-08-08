//
//  TransferTemplatesService.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 5.12.21.
//

import Foundation

class TransferTemplatesService {
    
    func getTemplates (
        completionHandler: @escaping (
            _ error: AuthenticationError?,
            _ templatesResponse: [TransferTemplate]?) -> Void)
    {
        let emptyBody:EmptyBody? = nil
        
        BasicRequest.shared.request(
            url: .transferTemplates,
            method: .get,
            parameters: emptyBody) { _, result, error in
                if let error = error {
                    print(error.localizedDescription)
                    completionHandler(.invalidCredential, nil)
                }else{
                    if let resultData = result{
                        do{
                            let decoder = JSONDecoder()
                            decoder.keyDecodingStrategy = .convertFromSnakeCase
                            
                            let transferData = try decoder.decode(
                                DataStructure.self,
                                from: resultData)
                            
                            completionHandler(nil, transferData.records)
                        } catch {
                            Logger.E(tag: APP_NAME, error.localizedDescription)
                            completionHandler(.custom(errorMessage: "Decoding error"), nil)
                        }
                    } else {
                        completionHandler(.custom(errorMessage: "No data"), nil)
                    }
                }
            }
    }
    
    struct DataStructure:Decodable {
        let records:[TransferTemplate]
    }
}
