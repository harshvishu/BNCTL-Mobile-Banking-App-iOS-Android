//
//  ManagePayeesViewModel.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 12/04/23.
//

import Foundation

@MainActor
class ManagePayeesViewModel: ObservableObject {
    
    @Published var isLoading: Bool = false
    
    @Published var payees: [Payee] = []
    
    @Published var error: String?
    @Published var showAllert: Bool = false
    
    @Published var showAddPayee: Bool = false
    
    func fetchPayees() {
        isLoading = true
        PayeeService().getPayees { error, response in
            self.isLoading = false
            if let error = error {
                self.error = "error_loading_accounts"
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let payeesResult = response {
                    self.payees = payeesResult
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }
}
