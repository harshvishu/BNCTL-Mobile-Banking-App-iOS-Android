//
//  BasicRequest.swift
//  BasicRequest
//
//  Created by Evgeniy Raev on 10.08.21.
//

import Foundation
import SwiftUI

class BasicRequest: NSObject, URLSessionDelegate, NetworkReachabilityProtocol {
    
    var networkPathMonitor: NetworkPathMonitor?
    var backgroundTaskID: UIBackgroundTaskIdentifier?
    static let shared = BasicRequest()

    private var backgroundTasks: [UIBackgroundTaskIdentifier: URLSessionTask] = [:]

    private var session = URLSession.shared
    private let sessionConfig = URLSessionConfiguration.default
    
    private let requestTimeout = 180 //Move to config
    private let sessionTimeout = 180 //Move to config
    
    private let userDefaults = UserDefaults.standard
    
    override init() {
        super.init()
        self.startNetworkMonitoring()
    }

    deinit {
        self.stopNetworkMonitoring()
    }
    
    func request<Body: Encodable> (
        url: ServiceType,
        method: HTTPMethod,
        parameters: Body? = nil,
        queryItems: [String: String]? = nil,
        isExtendSession: Bool = true, // Adds the predefined session time before popping back to Login Screen
        completionHandler: @escaping (_ statusCode: Int?, _ result: Data?, _ error: Error?) -> Void
    ) {
        // self.startNetworkMonitoring()
        
        // Prepare Request details
        guard let url = URL(string: BASE_URL + url.description) else {
            return
        }
        
        //TODO: get that from DataStore
        let language = Language(rawValue: userDefaults.string(forKey: "preferedLanguage") ?? Language.bg.code)
        
        var request = URLRequest(url: url)
        request.timeoutInterval = TimeInterval(requestTimeout)
        request.httpMethod = method.description
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue(language.backend, forHTTPHeaderField: "accept-language")
        if let loginToken = DataStore.instance.getLoginToken(){
            request.addValue("Bearer \(loginToken)", forHTTPHeaderField: "Authorization")
        }
        if let body = parameters{
            //request.httpBody = try? JSONSerialization.data(withJSONObject: body, options: [])
            let httpBody = try? JSONEncoder().encode(body)
            if  let jsonString = String(data: httpBody!, encoding: .utf8){
                Logger.D(tag: APP_NAME,"JSON request: " + (jsonString))
            }
            request.httpBody = httpBody
        }
        
        // Add Query parameters
        if let query = queryItems  {
            var urlComponents = URLComponents(string: url.description)!
            urlComponents.queryItems = []
            for (key, value) in query {
                let queryItem = URLQueryItem(name: key, value: value)
                urlComponents.queryItems?.append(queryItem)
            }
            request.url = urlComponents.url
            Logger.D(tag: APP_NAME,"Query Url: \(String(describing: request.url))")
        }
        
        // If the request has to extend the session (usually all are)
        if isExtendSession == true {
            sessionConfig.timeoutIntervalForRequest = TimeInterval(sessionTimeout)
            sessionConfig.timeoutIntervalForResource = TimeInterval(sessionTimeout)
            session = URLSession(configuration: sessionConfig)
        }

        Logger.D(tag: APP_NAME,"Request URL: \(method.description) \(url)")
        Logger.D(tag: APP_NAME,"Request Payload: \n\t\(String(describing: parameters))")

        if self.isNetworkAvailable() == false {
            completionHandler(nil, nil, NetworkError.noInternetConnectionError)
            return
        }
        session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.async {
                // If we receive an error here - it means that it's a transport error.
                if let error = error {
                    completionHandler(nil, nil, NetworkError.networkError(error))
                    return
                }
                                
                // Handle the normal cases, here error is nil.
                // Try to get data from the request - if not throw noResponseReceivedError.
                let httpResponse = response as! HTTPURLResponse
                Logger.D(tag: APP_NAME, "Status Code: \(String(describing: httpResponse.statusCode))")
                guard let data = data else {
                    completionHandler(httpResponse.statusCode, nil, NetworkError.noResponseReceivedError)
                    return
                }
                
                // If we cannot parse the response.. throw malformedResponseError.
                if let jsonResult = String(data: data, encoding: .utf8) {
                    Logger.D(tag: APP_NAME, "JSON Result: \n\t" + jsonResult)
                } else {
                    completionHandler(httpResponse.statusCode, nil, NetworkError.malformedResponseError)
                    return
                }
                
                // If there is data in the response - see if it's an error or not.
                guard (200...299).contains(httpResponse.statusCode) else {
                    // Try to handle a generic Server Error.
                    var errorMatchesIBFormat = true
                    var logoutUser = false
                    let decoder = JSONDecoder()
                    decoder.keyDecodingStrategy = .convertFromSnakeCase
                    if let serverError = try? decoder.decode(ServerError.self, from: data) {
                        completionHandler(httpResponse.statusCode, nil, serverError)
                        
                        if httpResponse.statusCode == 401 && serverError.error.target == "sessionExpired" {
                            logoutUser = true
                        }
                    } else if let serverError = try? decoder.decode(ServerFailierError.self, from: data) {
                        completionHandler(httpResponse.statusCode, nil, serverError)
                    } else {
                        errorMatchesIBFormat = false
                        completionHandler(httpResponse.statusCode, nil, UnhadeledError())
                    }
                    
                    if (httpResponse.statusCode == 401 && errorMatchesIBFormat == false) || logoutUser {
                        LoginViewModel().logoutState()
                    }
                    return
                }
                
                // Finally proceed with the happy case - let the ViewModels parse the data.
                completionHandler(httpResponse.statusCode, data, nil)
            }
        }.resume()
    }
    
    struct LoginRequestBody:Codable {
        let username:String
        let password:String
    }
    
    func networkStatusChanged(isConnected: Bool) {
        print("NetworkStatusChanged called - \(isConnected)")
    }
}



extension BasicRequest {
    
    final func tryFinishActiveRequests() {
        self.session.getAllTasks { [weak self] (tasks) in
            tasks.forEach { (task) in
                let application = UIApplication.shared
                let bundleID = Bundle.main.bundleIdentifier ?? ""
                let name = "\(bundleID).\(UUID().uuidString)"
                var taskIdentifier: UIBackgroundTaskIdentifier?
                taskIdentifier = application.beginBackgroundTask(withName: name, expirationHandler: { [weak self] in
                    self?.endBackgroundTask(task)
                })
                self?.backgroundTasks[taskIdentifier!] = task
            }
        }
    }
    
    private func endBackgroundTask(_ task: URLSessionTask) {
        guard let identifier = backgroundTasks.first(where: { task.isEqual($0.value) })?.key
        else { return }
        let application = UIApplication.shared
        application.endBackgroundTask(identifier)
        backgroundTasks[identifier] = nil
    }
}

struct EmptyBody: Codable {
    
}
