//
//  Language.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 9.12.22.
//

import Foundation

enum Language: CaseIterable, Identifiable, Equatable {
    
    case bg
    case en
    
    var code:String {
        switch self {
        case .bg:
            return "bg_BG"
        case .en:
            return "en_EN"
        }
    }
    
    var id:String {
        self.code
    }
    
    var backend:String {
        switch self {
        case .bg:
            return "bg"
        case .en:
            return "en"
        }
    }
    
    var name:String {
        switch self {
        case .bg:
            return "Български"
        case .en:
            return "English"
        }
    }
    
    init(rawValue: String) {
        switch rawValue {
        case "bg", "bg_BG":
            self = .bg
        case "en", "en_EN":
            self = .en
        default:
            self = .bg
        }
        
    }
    
    static let `default`:Language = .bg
}
