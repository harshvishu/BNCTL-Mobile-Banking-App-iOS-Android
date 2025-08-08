//
//  TransferViewModel.swift
//  Allianz
//
//  Created by harsh vishwakarma on 07/04/23.
//

import Foundation
import Combine
import SwiftUI

enum TransferCreationError:Error {
    case something
}

class TransferViewModel:ObservableObject {
    
    let transferType:TransferType
    @Published var formErrors: [String: String] = [:]
    
    init(type: TransferType) {
        self.transferType = type
        self.orderType = .sell
        self.preferentialRatePin = ""
        
        addSubscribers()
    }
    
    init(template: TransferTemplate) {
        self.transferType = template.transferType ?? .interbank
        
        /*
        self.sourceAccount = ManualAccountDetails(
            name: template.sourceAccountHolder ?? "",
            account: template.sourceAccount ?? "",
            currency: template.sourceAccountCurrency ?? ""
        )
         */
        
        self.amount = template.amount ?? 0
        self.reason = template.description ?? ""
        self.additionalReason = template.additionalDescription ?? ""
        self.transactionType = template.transactionType ?? .standard
        
        if (transferType == .betweenacc) {
            /*
            self.destinationAccount = ManualAccountDetails(
                name: template.destinationAccountHolder ?? "",
                account: template.destinationAccount ?? "",
                currency: template.destinationAccountCurrency ?? ""
            )
            */
        } else {
            self.beneficiary = template.destinationAccountHolder ?? ""
            self.iban = template.destinationAccount ?? ""
            self.currency = Currency(rawValue: template.destinationAccountCurrency ?? template.sourceAccountCurrency ?? "BGN")
        }
        self.orderType = OrderType(rawValue: template.additionalDetails?.type ?? "sell") ?? .sell
        self.preferentialRatePin = template.additionalDetails?.preferentialRatesPin ?? ""
        
        addSubscribers()
    }
    
    // Transfer Data - Source Account
    @Published var selectingSourceAccount = false
    @Published var sourceAccount: Account? {
        didSet {
            if let acc = sourceAccount, let destAcc = destinationAccount {
                if acc.account == destAcc.account {
                    self.destinationAccount = nil
                    self.beneficiary = ""
                    self.iban = ""
                }
            }
        }
    }
    
    // Transfer Data - Destination Account
    @Published var selectingDestinationAccount = false
    @Published var destinationAccount: Account? {
        didSet {
            if let acc = destinationAccount {
                beneficiary = acc.name
                iban = acc.account
                currency = Currency(rawValue: acc.currency)
            }
        }
    }
    @Published var beneficiary: String = ""
    @Published var iban: String = ""
    
    // Transfer Data - Details
    @Published var amount: Double?
    @Published var reason: String = ""
    @Published var additionalReason: String = ""
    @Published var transactionType: TransactionType = .standard
    @Published var requiredTransactionType:TransactionType? = nil
    @Published var currency:Currency?
    
    
    @Published var orderType: OrderType
    @Published var preferentialRatePin: String
    private var cansaibles:[AnyCancellable] = []
    
    
    func addSubscribers() {
        
        $requiredTransactionType
            .filter { $0 != nil }
            .map({ requred in
                if let requred = requred {
                    return requred
                } else {
                    return self.transactionType
                }
            })
            .assign(to: &$transactionType)
        
        //Atomatic swifching of the transaction system
        /*
         $iban.map { iban in
         if(self.testSameBank(iban: iban)) {
         return nil
         } else {
         return .standard
         }
         }.assign(to: &$requiredTransactionType)
         */
        
        $reason.sink { reason in
            let _ = self.testValidReason(field: "reason", reason: reason)
        }.store(in: &cansaibles)
        $additionalReason.sink { reason in
            let _ = self.testValidReason(field: "additionalReason", reason: reason)
        }.store(in: &cansaibles)
        $beneficiary.sink { beneficiary in
            let _ = self.testValidReason(field: "beneficiary", reason: beneficiary)
        }.store(in: &cansaibles)
        
        
        $sourceAccount
            .combineLatest($destinationAccount)
            .filter({ (source, destination) in
                if let source = source,
                   let destination = destination {
                    return source == destination
                } else {
                    return false
                }
            })
            .map { _ in
                return nil
            }
            .assign(to: &$destinationAccount)
        
    }
    
    func testSameBank(iban:String) -> Bool {
        if let test = try? NSRegularExpression(pattern: "^[a-zA-Z0-9]{4}BUIN") {
            let range = NSRange(location: 0, length:iban.utf16.count)
            if (test.firstMatch(in: iban, options: [], range: range) == nil) {
                return false
            } else {
                return true
            }
        } else {
            return false
        }
    }
    
    func testValidReason(field:String, reason:String) -> Bool {
        if let test = try? NSRegularExpression(pattern: "[^ A-Za-zА-Яа-я0-9()\\r\\n\\f!\"#%&\\'*+,-/.:;<=>?@{}]") {
            let range = NSRange(location: 0, length:reason.utf16.count)
            let matches = test.matches(in: reason, range: range)
            let ns = (reason as NSString)
            if(matches.isEmpty) {
                formErrors[field] = nil
                return true
            } else {
                let problematic = matches.reduce(into: [String]()) { partialResult, match in
                    let subString = ns.substring(with: match.range)
                    
                    if partialResult.firstIndex(of: subString) == nil {
                        partialResult.append(subString)
                    }
                }.joined(separator: ", ")
                
                formErrors[field] = "error_transfer_validation_reason_invalid_characters %@/\(problematic)"
                
                return false
            }
        } else {
            return false
        }
    }
    
    func validateSourceAccount() {
        if let _ = sourceAccount {
            formErrors["sourceAccount"] = nil
        } else {
            formErrors["sourceAccount"] = String(format: "error_transfer_validation_source_account_missing", arguments: [])
        }
    }
    
    func validateAmount() {
        if let amount = amount, amount > 0 {
            formErrors["amount"] = nil
        } else {
            formErrors["amount"] = "error_transfer_validation_amount_greater_than_zero"
        }
    }
    
    func validateDestinationAccount() {
        if let _ = destinationAccount {
            formErrors["destinationAccount"] = nil
        } else {
            formErrors["destinationAccount"] = "Destination account is required"
        }
    }
    
    func validateBeneficiary() {
        if (testValidReason(field: "beneficiary", reason: beneficiary) == false) {
            
        } else if (beneficiary.isEmpty) {
            formErrors["beneficiary"] = "error_transfer_validation_required_beneficiary"
        } else {
            formErrors["beneficiary"] = nil
        }
    }
    
    func validateAccountsForBettweenAccoutsSameCurrnecy() {
        if let source = sourceAccount?.currency,
           let destination = destinationAccount?.currency,
           source != destination {
           formErrors["alert"] = "error_transfer_validation_same_currency"
        } else {
            formErrors["alert"] = nil
        }
    }
    
    func validateAccountsForCurrencyExchange() {
        if let source = sourceAccount?.currency,
           let destination = destinationAccount?.currency,
           source == destination {
            formErrors["alert"] = "error_currency_transfer_validation_same_currency"
        } else {
            formErrors["alert"] = nil
        }
    }
    
    private func ibanValidation() -> Bool {
        let symbols = iban.trimmingCharacters(in: .whitespacesAndNewlines)
        guard symbols.isEmpty == false else {
            return false
        }
        guard (symbols.count > 15 && symbols.count < 34 && ((symbols.range(of: "^[0-9A-Z]*$", options: .regularExpression) == nil) == false)) else {
            return false
        }
        let swapped = "\(symbols.dropFirst(4))\(symbols.prefix(4))"
        return swapped.reduce(0) { (previousMod, char) in
            let value = Int(String(char), radix: 36)!
            let factor = value < 10 ? 10 : 100
            return (factor * previousMod + value) % 97
        } == 1
    }
    
    func validateIban() {
        if (iban.isEmpty || ibanValidation() == false) {
            formErrors["iban"] = "error_transfer_validation_invalid_iban"
        } else if (iban == sourceAccount?.account) {
            formErrors["iban"] = "error_transfer_validation_same_source_and_destination"
        } else if (transferType == .interbank && iban.starts(with: "BG") == false) {
            formErrors["iban"] = "error_transfer_validation_invalid_bulgarian_iban"
        } else {
            formErrors["iban"] = nil
        }
    }
    
    func validateTransactionType() {
        if(testSameBank(iban: iban) && transactionType != .standard) {
            formErrors["alert"] = "error_transfer_interbank_wrong_transfer_system"
        } else {
            formErrors["alert"] = nil
        }
    }
    
    func validateReason() {
        if(testValidReason(field: "reason", reason: reason) == false) {
            
        } else if (reason.isEmpty) {
            formErrors["reason"] = "error_transfer_validation_required_reason"
        } else {
            formErrors["reason"] = nil
        }
    }
    
    func validateAdditionalReason() {
        let _ = testValidReason(field: "additionalReason", reason: additionalReason)
        /*
        if (false) {
            formErrors["additionalReason"] = "error_transfer_validation_required_reason"
        } else {
            formErrors["additionalReason"] = nil
        }
        */
    }
    
    func validatePreferentialRatePin() {
        if ((0...10).contains(preferentialRatePin.count)) {
            formErrors["preferentialRatePin"] = nil
        } else {
            formErrors["preferentialRatePin"] = "error_transfer_validation_preferential_rates_pin_length"
        }
    }
    
    func formValidation() -> Bool {
        formErrors.removeAll()
        validateSourceAccount()
        validateAmount()
        if (transferType == .betweenacc || transferType == .currencyExchange) {
            validateDestinationAccount()
        } else {
            validateBeneficiary()
            validateIban()
        }
        if(transferType == .interbank) {
            validateTransactionType()
        }
        validatePreferentialRatePin()
        if(transferType == .betweenacc) {
            validateAccountsForBettweenAccoutsSameCurrnecy()
        }
        if(transferType == .currencyExchange) {
            validateAccountsForCurrencyExchange()
        } else {
            validateReason()
            validateAdditionalReason()
        }
        return formErrors.isEmpty
    }
    
    func getTransfer() throws -> Transfer {
        var additionalDetails: TransferDetails? = nil
        guard let source = sourceAccount else {
            //TODO: add err
            throw TransferCreationError.something
        }
        guard let amount = amount, amount > 0 else {
            //TODO: add err
            throw TransferCreationError.something
        }
        
        guard beneficiary.isEmpty == false else {
            //TODO: add err
            throw TransferCreationError.something
        }

        guard iban.isEmpty == false else {
            //TODO: add err
            throw TransferCreationError.something
        }
        
        if (transferType == .currencyExchange) {
            guard (0...10).contains(preferentialRatePin.count) else {
                    //TODO: add err
                throw TransferCreationError.something
            }
            if (source.currency == destinationAccount?.currency) {
                throw TransferCreationError.something
            }
            
            additionalDetails = TransferDetails(
                transactionType: .currencyExchange,
                orderType: orderType,
                rateType: preferentialRatePin.isEmpty ? .standard : .custom,
                offerId: preferentialRatePin)
        } else {
            guard reason.isEmpty == false else {
                //TODO: add err
                throw TransferCreationError.something
            }
        }
        
        if(transactionType == .rings) {
            additionalDetails = TransferDetails(
                transactionType: .rings,
                orderType: nil,
                rateType: nil,
                offerId: nil)
        }
        
        return Transfer(
            amount:  String(format: "%.2f", amount),
            description: reason,
            additionalDescription: additionalReason.isEmpty ? nil : additionalReason,
            destinationAccount: iban.trimmingCharacters(in: .whitespacesAndNewlines),
            destinationAccountCurrency: currency?.rawValue ?? "BGN",
            recipientName: beneficiary,
            sourceAccount: source.account,
            sourceAccountId: source.accountId,
            sourceAccountCurrency: source.currency,
            transferType: transferType,
            executionType: "now",
            additionalDetails: additionalDetails,
            useFallback: nil,
            fallbackSmsCode: nil,
            fallbackPin: nil
        )
    }
    
    func fillFromTransfer(transfer:Transfer) {
        
    }
}
