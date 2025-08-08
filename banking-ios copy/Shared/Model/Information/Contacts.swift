//
//  Contacts.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 12.12.22.
//

import Foundation

struct Contacts:Decodable {
    let address:String
    let swiftCode:String
    let phone:String
    let email:String
    let website:String
    let socialMedia:[SocialMedia]
    
    struct SocialMedia:Decodable, Identifiable {
        let platformKey:String
        let url:String
        
        var id:String {
            self.platformKey
        }
    }
}
