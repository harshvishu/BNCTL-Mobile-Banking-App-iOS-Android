//
//  NewsService.swift
//  Allianz (iOS)
//
//  Created by Prem's on 29/12/21.
//

import Foundation

class NewsService {
    
    func getNews(completionHandler: @escaping (_ error: AuthenticationError?, _ newsResponse: [NewsModel]?) -> Void) {
        
        let emptyBody:EmptyBody? = nil
        
        BasicRequest.shared.request(url: .news, method: .get, parameters: emptyBody) { _, result, error in
            
            if let error = error {
                print(error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            }else{
                if let resultData = result{
                    do{
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        let newsobj = try decoder.decode([NewsModel].self, from: resultData)
                        completionHandler(nil, newsobj)
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
