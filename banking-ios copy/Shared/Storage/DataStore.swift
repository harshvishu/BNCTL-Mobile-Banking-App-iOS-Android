//
//  DataStore.swift
//  Allianz (iOS)
//
//  Created by Prem's on 02/11/21.
//

import Foundation
import SwiftUI

class DataStore {
    
    private static let USER_SUITE = "userSuite"

    static let instance = DataStore()
    private let userSuite: UserDefaults
    private var token:String?
    
    private init() {
        userSuite = UserDefaults(suiteName: DataStore.USER_SUITE)!
    }
    
    func setLoginToken(token: String?)  {
        self.token = token
    }
    
    func getLoginToken() -> String?  {
        return self.token
    }

    func setLastActiveTime() {
        userSuite.set(Date(), forKey: LAST_ACTIVE_TIME)
    }

    func getLastActiveTime() -> Date? {
        return userSuite.value(forKey: LAST_ACTIVE_TIME) as? Date
    }

    func removeLastActiveTime() {
        userSuite.removeObject(forKey: LAST_ACTIVE_TIME)
    }

    func clearDataStore(){
        self.token = nil
        
        let dictionary = userSuite.dictionaryRepresentation()
        for key in dictionary.keys {
            userSuite.removeObject(forKey: key)
        }
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
    }
}
