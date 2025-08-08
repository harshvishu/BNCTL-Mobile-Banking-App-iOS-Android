//
//  TransferTemplate.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 4.12.21.
//

import Foundation

struct TransferTemplate:Codable, Identifiable, Hashable {
    let templateId:String
    let templateName:String
    let amount:Double?
    let description:String?
    let additionalDescription:String?
    
    let sourceAccount:String?
    let sourceAccountCurrency:String?
    let sourceAccountHolder:String?
    
    let destinationAccount:String?
    let destinationAccountCurrency:String?
    let destinationAccountHolder:String?
    
    let transactionType:TransactionType?
    let transferType:TransferType?
    
    let additionalDetails:AdditionalDetails?
    
    struct AdditionalDetails:Codable, Hashable {
        let type:String
        let preferentialRatesPin:String
    }
    
    var id:String { self.templateId }
    
    func toTransfer() -> Transfer {
        return Transfer(
            amount: String(format: "%.2f", amount ?? 0),
            description: description ?? "",
            additionalDescription: nil,
            destinationAccount: destinationAccount ?? "",
            destinationAccountCurrency: destinationAccountCurrency ?? "",
            recipientName: destinationAccountHolder ?? "",
            sourceAccount: sourceAccountHolder ?? "",
            sourceAccountId: sourceAccount ?? "",
            sourceAccountCurrency: sourceAccountCurrency ?? "",
            transferType: transferType ?? .interbank,
            executionType: "now",
            additionalDetails: nil,
            useFallback: nil,
            fallbackSmsCode: nil,
            fallbackPin: nil
        )
    }
    
    static func empty(type:TransferType) -> TransferTemplate {
        TransferTemplate(
            templateId: "-1",
            templateName: "new transfer",
            amount: nil,
            description: nil,
            additionalDescription:nil,
            sourceAccount: nil,
            sourceAccountCurrency: nil,
            sourceAccountHolder: nil,
            destinationAccount: nil,
            destinationAccountCurrency: nil,
            destinationAccountHolder: nil,
            transactionType: nil,
            transferType: type,
            additionalDetails: nil
        )
    }
}

