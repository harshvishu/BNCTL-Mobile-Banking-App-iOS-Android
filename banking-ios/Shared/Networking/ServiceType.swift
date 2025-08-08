//
//  ServiceType.swift
//  Allianz
//
//  Created by Prem's on 23/11/21.
//

import Foundation

enum ServiceType {
 
    case config(Config)
    case login
    case logout
    case loginFallback
    case loginFallbackConfirm
    case account
    case accountStatment(String)
    case transferValidate
    case transferExcecute
    case transferCreate
    case tranferConfirm
    case insurance
    case pendingTransfers
    case pendingTransfersApprove
    case pendingTransfersReject
    case exchangeRates
    case cards
    case cardsStatements
    case cardProducts
    case fetchCreditCardStatements
    case downloadCreditCardStatement
    case fetchUtilityBills
    case approveUtilityBills
    case news
    case branches
    case locationsBranches
    case locationsATMs
    case cashWithdrawal
    case documentsHistory
    case transferTemplates
    case fetchUtilityBillsHistory
    case profile
    case currentUser
    case checkEoD
    case contacts
    case wait
    case nowait
    case payeesBanks
    case payees
    case payee(String)
    
    var description: String {
        switch self {
        case .login:
            return "auth/login"
        case .logout:
            return "auth/logout"
        case .loginFallback:
            return "auth/fallback/sendSms"
        case .loginFallbackConfirm:
            return "auth/fallback/confirm"
        case .account:
            return "accounts"
        case .accountStatment(let accountId):
            return "accounts/\(accountId)/statement"
        case .transferValidate:
            return "transfers/validate"
        case .transferExcecute:
            return "transfers/execute"
        case .transferCreate:
            return "transfers/create"
        case .tranferConfirm:
            return "transfers/confirm"
        case .insurance:
            return "insurance"
        case .pendingTransfers:
            return "transfers/pending"
        case .pendingTransfersApprove:
            return "transfers/approve"
        case .pendingTransfersReject:
            return "transfers/reject"
        case .exchangeRates:
            return "exchange-rates"
        case .cards:
            return "cards"
        case .cardsStatements:
            return "cards/statement"
        case .cardProducts:
            return "cards/products"
        case .fetchCreditCardStatements:
            return "cards/creditCardStatement"
        case .downloadCreditCardStatement:
            return "cards/creditCardStatement/download"
        case .news:
            return "news"
        case .fetchUtilityBills:
            return "bill/my-billers"
        case .approveUtilityBills:
            return "bill/utility/approve"
        case .branches:
            return "branches"
        case .locationsBranches:
            return "locations/branches"
        case .locationsATMs:
            return "locations/atms"
        case .cashWithdrawal:
            return "cash-withdrawal"
        case .documentsHistory:
            return "transfers/internet-banking"
        case .transferTemplates:
            return "templates"
        case .fetchUtilityBillsHistory:
            return "bill/utility/fetch"
        case .profile:
            return "accounts/groups"
        case .currentUser:
            return "users/current"
        case .checkEoD:
            return "checkEoD"
        case .contacts:
            return "information/contacts"
        case .wait:
            return "wait"
        case .nowait:
            return "nowait"
        case .config(let config):
            switch config {
            case .pushProvisioning:
                return "config/mobile/ios?key=pushProvisioning"
            case .version:
                return "config/mobile/ios?key=version"
            case .all:
                return "config/mobile/ios"
            }
        case .payeesBanks:
            return "payees/banks"
        case .payees:
            return "payees"
        case .payee(let payeeId):
            return "payees/\(payeeId)"
        }
        
    }
    
    enum Config {
        case version
        case pushProvisioning
        case all
    }
}

enum HTTPMethod: String {
    
    case get
    case post
    case put
    case patch
    case delete
    
    var description: String {
        return self.rawValue.capitalized
    }
}

enum HTTPEncoding {
    case urlFormEncoded
    case jsonEncoded
}
