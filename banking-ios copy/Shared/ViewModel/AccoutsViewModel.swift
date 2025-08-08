//
//  AccoutsViewModel.swift
//  AccoutsViewModel
//
//  Created by Evgeniy Raev on 11.08.21.
//

import Foundation
import SwiftUI
import Combine

/**
 Accounts ViewModel holds array of all Accounts that the current client has access to.
 The Accounts ViewModel is scoped only for the Account Dashboard and Details screens.
 For the ViewModel for AccountPickers see AccountPickerViewModel.
 */
class AccountsViewModel: ObservableObject {
    
    @Published var isLoading: Bool = false
    
    @Published var accounts: [Account] = []
    @Published var selectedAccount: Account? = nil
    @Published var showAccountDetails: Bool = false
    
    @Published var error: String?
    @Published var showAllert: Bool = false
    
    var isLocal: Bool = false
    
    init(isLocal: Bool = false) {
        self.isLocal = isLocal
        addSubscribers()
    }
    
    func fetchAccounts() {
        isLoading = true
        if (isLocal) {
            self.accounts = [Account.preview!]
        }
        
        AccountService().getAccounts()
        { error, accountResponse in
            self.isLoading = false
            
            if let error = error {
                self.error = "error_loading_accounts"
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let accountsResult = accountResponse {
                    self.accounts = accountsResult
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }

    func clearData() {
        accounts = []
    }
    
    func addSubscribers() {
        // Account Details
        $selectedAccount.map { data in
            data != nil
        }
        .removeDuplicates()
        .assign(to: &$showAccountDetails)
        
        
        $showAccountDetails
            .filter({ $0 == false })
            .map({ _ in
                return nil as Account?
            })
            .assign(to: &$selectedAccount)
        
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
        
        
        $accounts.map({ _ in
            nil
        }).assign(to: &$selectedAccount)
    }
    
    func getAccount(manualAccount: ManualAccountDetails) -> Account? {
        return accounts.first(where: {
            $0.iban == manualAccount.account
        })
    }
    
    static var preview: AccountsViewModel {
        let avm = AccountsViewModel(isLocal: true)
        avm.accounts.append(Account.preview!)
        return avm
    }
}
