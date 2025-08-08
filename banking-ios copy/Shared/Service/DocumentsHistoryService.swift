//
//  DocumentsHistoryService.swift
//  Allianz (iOS)
//
//  Created by Prem's on 02/02/22.
//

import Foundation

class DocumentsHistoryService {
    
    func getDocumentsHistory(queryItems: [String: String], completionHandler: @escaping (_ error: AuthenticationError?, _ newsResponse: [DocumentsHistoryData]?) -> Void) {
        let emptyBody: EmptyBody? = nil
        BasicRequest.shared.request(url: .documentsHistory, method: .get, parameters: emptyBody, queryItems: queryItems) { _, result, error in
            if let error = error {
                print(error.localizedDescription)
                completionHandler(.invalidCredential, nil)
            }else{
                if let resultData = result{
                    do{
                        let decoder = JSONDecoder()
                        decoder.keyDecodingStrategy = .convertFromSnakeCase
                        decoder.dateDecodingStrategy = .iso8601
                        let documentsHistory = try decoder.decode(DocumentsHistoryModel.self, from: resultData)
                        completionHandler(nil, documentsHistory.records)
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
                        Logger.E(tag: APP_NAME, error.localizedDescription)
                        completionHandler(.custom(errorMessage: "Decoding error"), nil)
                    }
                }else{
                    completionHandler(.custom(errorMessage: "No data"), nil)
                }
            }
        }
    }
    class DocumentsHistoryModel: Codable, Identifiable{
     
        let records: [DocumentsHistoryData]
    
    }
}
