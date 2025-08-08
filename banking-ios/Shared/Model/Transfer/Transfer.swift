//
//  Transfer.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 11.11.21.
//

import Foundation

struct Transfer:Encodable {
    let amount: String
    
    let description: String
    let additionalDescription:String?
    
    let destinationAccount: String
    let destinationAccountCurrency: String
    let recipientName: String?
    
    let sourceAccount: String
    let sourceAccountId: String
    let sourceAccountCurrency: String
    
    let transferType: TransferType
    let executionType: String
    let additionalDetails: TransferDetails?
    
    var useFallback:Bool?
    var fallbackSmsCode:String?
    var fallbackPin:String?
    
    static func transferPreview(type: TransferType) -> Transfer {
        return Transfer(
            amount: "1000",
            description: "Preview transfer",
            additionalDescription: nil,
            destinationAccount: "BG12345678902",
            destinationAccountCurrency: "BGN",
            recipientName: "Kolio Klienta",
            sourceAccount: "BG1245678901",
            sourceAccountId: "Source Account",
            sourceAccountCurrency: "BGN",
            transferType: type,
            executionType: "now",
            additionalDetails: nil,
            useFallback: nil,
            fallbackSmsCode: nil,
            fallbackPin: nil
        )
    }
}

struct TransferDetails:Codable {
    let transactionType:TransactionType
    let orderType: OrderType?
    let rateType: RateType?
    let offerId: String?
}

enum TransferType:String, Identifiable, Codable {
    case intrabank      // Same Bank
    case interbank      // National
    case betweenacc     // Between Own Accounts
    case international  // International
    
    case utilityBills
    case insirance
    case currencyExchange
    
    var id: String { self.rawValue }
    
    var premisionCode:OperationType {
        switch self {
        case .intrabank:
            return .internalCurrencyTransfer
        case .interbank:
            return .transferInBgn
        case .betweenacc:
            return .transferBetweenAccounts
        case .international:
            return .transferInternational
        case .utilityBills:
            return .utilityBills
        case .insirance:
            return .insurancePayment
        case .currencyExchange:
            return .foreignCurrencyExchange
        }
    }
    
    // TODO: Might need to localize
    var displayName: String? {
        switch self {
        case .intrabank: return "Same Bank"
        case .interbank: return "National"
        case .betweenacc: return "Between Own Accounts"
        case .international: return "International"
        default: return nil
        }
    }
    
    var transferTypeId: Int? {
        switch self {
        case .intrabank: return 0
        case .interbank: return 1
        case .international: return 2
        default: return nil
        }
    }
}

enum TransactionType:String, Codable {
    case standard = "BISERA"
    case rings = "RINGS"
    case currencyExchange = "currency.exchange"
    case intrabankCurrencyTransfer
    case foreignExchange
}

enum OrderType: String, Codable {
    case buy
    case sell
}

enum RateType: String, Codable {
    case standard
    case custom
}

extension TransactionType {
    
    enum CodingError: Error {
        case unknownValue
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        
        let rawValue = try? container.decode(String.self)
        
        if let r = TransactionType(rawValue: rawValue ?? "") {
            self = r
        } else {
            switch rawValue {
            case "BISERA":
                self = .standard
            case "RINGS":
                self = .rings
            default:
                throw CodingError.unknownValue
            }
        }
        
    }
}
