//
//  InsuranceService.swift
//  Allianz (iOS)
//
//  Created by Prem's on 08/12/21.
//

import Foundation

class InsuranceService {
    
    func getInsurance(completionHandler: @escaping (_ error: AuthenticationError?, _ accountResponse: [InsuranceData]?) -> Void) {
        
        let emptyBody:EmptyBody? = nil
        
        BasicRequest.shared.request(url: .insurance, method: .get, parameters: emptyBody) { _, result, error in
            
            if let error = error {
                print(error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            }else{
                if let resultData = result{
                    do{
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let insuranceData = try decoder.decode(InsuranceModel.self, from: resultData)
                        completionHandler(nil, insuranceData.result)
                    }catch{
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.custom(errorMessage: "Decoding error"), nil)
                    }
                }else{
                    completionHandler(.custom(errorMessage: "No data"), nil)
                }
            }
        }
    }
    
}
