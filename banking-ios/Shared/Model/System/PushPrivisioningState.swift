//
//  PushPrivisioningState.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 9.03.23.
//

import Foundation

struct PushPrivisioningState:Decodable {
  
    let isEnabled:Bool
    let launchDate:Date
}

