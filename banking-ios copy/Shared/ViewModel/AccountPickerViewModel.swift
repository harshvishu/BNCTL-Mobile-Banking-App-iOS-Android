//
//  AccountPickerViewModel.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 19.01.23.
//

import Foundation

/**
 Account Picker ViewModel that holds array of accounts for debit and credit operations that the client has access to.
 This class is intended to be scoped only among Views on a navigation-level Dashboard level (e.g. TransferDashboard, CardsDashboard...)
 TransferDashboardView will have its own instance,
 CardsDashboardView will have its own instance and so on.
 */
class AccountPickerViewModel: ObservableObject {
    
    @Published var isLoading: Bool = false
    @Published var error: String?
    @Published var showAllert: Bool = false
    
    @Published var accounts: [OperationType:[OperationDirection:[Account]]] = [:]
    
    func fetchAccounts(permissionCode: OperationType) {
        if (self.accounts[permissionCode] == nil) {
            self.accounts[permissionCode] = [:]
        } else {
            return
        }
        
        isLoading = true
        
        // Fetch accounts for debit
        AccountService().getAccounts(
            permissionCode: permissionCode,
            debit: true)
        { error, accountResponse in
            self.isLoading = false
            if let error = error {
                self.error = "error_loading_accounts"
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let accountsResult = accountResponse {
                    self.accounts[permissionCode]![.debit] = accountsResult
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
        // Fetch accounts for credit
        AccountService().getAccounts(
            permissionCode: permissionCode,
            debit: false)
        { error, accountResponse in
            self.isLoading = false
            if let error = error {
                self.error = "error_loading_accounts"
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let accountsResult = accountResponse {
                    self.accounts[permissionCode]![.credit] = accountsResult
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }
    
    func getAccount(
        premisionFor:OperationType,
        direction: OperationDirection,
        accointID: String
    ) -> Account? {
        let account = accounts[premisionFor]?[direction]?
            .first(where: {
                $0.accountId == accointID
            })
        
        return account
    }
    
    func getAccount(
        premisionFor:OperationType,
        direction: OperationDirection,
        iban: String
    ) -> Account? {
        let account = accounts[premisionFor]?[direction]?
            .first(where: {
                $0.iban == iban
            })
        
        return account
    }
    
    func addSubscribers() {
        // Error handling
        $error.map { data in
            data != nil
        }
        .removeDuplicates()
        .assign(to: &$showAllert)
        $showAllert
            .filter({ $0 == false })
            .map({ _ in
                return nil as String?
            })
            .assign(to: &$error)
    }
    
    func clearData() {
        accounts = [:]
    }
    
}
                          
enum OperationDirection: String {
    case debit = "debit"
    case credit = "credit"
}
