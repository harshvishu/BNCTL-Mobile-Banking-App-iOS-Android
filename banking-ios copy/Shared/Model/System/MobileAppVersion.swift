//
//  MobileAppVersion.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 10.01.23.
//

import Foundation

struct MobileAppVersion:Decodable {
  
    let latest:Version
    let minimum:Version
    
    struct Version:Decodable {
        let versionName:String
        let productVersion:Double
    }
}
