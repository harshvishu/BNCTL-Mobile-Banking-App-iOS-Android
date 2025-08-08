//
//  Payee+Examples.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 20/04/23.
//

import Foundation

#if DEBUG
extension Payee {
    static var preview: [Payee] = [
        Payee(walletAccountNumber: nil, accountNumber: "123456", type: PayeeType(rawValue: "bank")!, userId: "123456", walletProvider: nil, bank: "BNCTL", notificationLanguage: nil, accountTypeId: "Current", isDeleted: false, name: "Same-bank-Test", currency: "USD", payeeId: "12345678", email: nil, swift: "BNCTL"),
        Payee(walletAccountNumber: nil, accountNumber: "123456", type: PayeeType(rawValue: "bank")!, userId: "123456", walletProvider: nil, bank: "BNCTL", notificationLanguage: nil, accountTypeId: "Current", isDeleted: false, name: "Same-bank-Test", currency: "USD", payeeId: "12345678", email: nil, swift: "BNCTL"),
        Payee(walletAccountNumber: nil, accountNumber: "123456", type: PayeeType(rawValue: "bank")!, userId: "123456", walletProvider: nil, bank: "BNCTL", notificationLanguage: nil, accountTypeId: "Current", isDeleted: false, name: "Same-bank-Test", currency: "USD", payeeId: "12345678", email: nil, swift: "BNCTL")
    ]
}
#endif
