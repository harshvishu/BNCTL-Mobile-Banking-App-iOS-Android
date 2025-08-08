//
//  EditPayeeViewModel.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 15/04/23.
//

import Combine
import SwiftUI

class EditPayeeViewModel: ObservableObject {
    
    let editingMode: EditPayeeView.EditingMode
    @Published var formErrors: [String: String] = [:]
    
    // Payee Data - Details
    @Published var payeeId: String?
    @Published var transferType: TransferType?
    @Published var name: String = ""
    @Published var email: String = ""
    @Published var accountNumber: String = ""
    @Published var currency:Currency?
    @Published var banks: [Bank] = []
    @Published var supportedCurrencyTypes = [Currency.USD]              // Currently only `USD` is supported
    @Published var supportedTransferTypes = [TransferType.intrabank]    // Currently only `intrabank` is supported
    
    @Published var isLoading: Bool = false
    @Published var payeeDeleted: Bool = false
    
    private var cancellable:[AnyCancellable] = []
    
    init(editingMode: EditPayeeView.EditingMode) {
        self.editingMode = editingMode
        bindData()
        addSubscribers()
    }
    
    /// Private methods
    private func bindData() {
        switch editingMode {
        case .create:
            self.payeeId = ""
            self.name = ""
            self.accountNumber = ""
            self.email = ""
            self.currency = supportedCurrencyTypes.first
            self.transferType = .intrabank
        case .edit(let payee):
            self.payeeId = payee.payeeId
            self.name = payee.name
            self.payeeId = payee.payeeId
            self.accountNumber = payee.accountNumber
            self.email = payee.email ?? ""
            self.currency = Currency(rawValue: payee.currency)  // TODO: Check for safe operations
            if payee.type == .bank {
                // TODO: Needs a mapping function
                self.transferType = .intrabank
            }
        }
    }
    
    private func addSubscribers() {
        PayeeService().getBanks {[self] error, banks in
            if let error = error {
                self.formErrors["bank_api"] = "error_payees_no_banks_available"
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let banks = banks {
                self.banks = banks
                if banks.isEmpty {
                    self.formErrors["bank_api"] = "error_payees_no_banks_available"
                }
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
    
    /// Form validations
    private func validateName() {
        if name.isEmpty {
            formErrors["name"] = String(format: "error_payees_validation_field_is_required", arguments: [])
        } else {
            formErrors["name"] = nil
        }
    }
    
    private func validateTransferType() {
        if transferType == nil {
            formErrors["transferType"] = String(format: "error_payees_validation_field_is_required", arguments: [])
        } else {
            formErrors["transferType"] = nil
        }
    }
    
    private func validateAccountNumner() {
        if accountNumber.isEmpty {
            formErrors["accountNumber"] = String(format: "error_payees_validation_field_is_required", arguments: [])
        } else {
            formErrors["accountNumber"] = nil
        }
    }
    
    func formValidation() -> Bool {
        formErrors.removeAll()
        validateName()
        validateTransferType()
        validateAccountNumner()
        return formErrors.isEmpty
    }
    
    /// API layer calls
    func createPayee() {
        guard let transferTypeId = transferType?.id else {return}
        guard let selectedBank = banks.first else {return}
        
        isLoading = true
        
        let payee = CreatePayeeRequest(
            accountNumber: accountNumber,
            accountTypeId: transferTypeId,
            bank: selectedBank.name,
            currency: "USD",
            email: email,
            name: name,
            swift: selectedBank.swift
        )
        
        PayeeService().createPayee(payee: payee)
        {[self] error, result in
            defer {
                self.isLoading = false
            }
            if let _ = result {
                formErrors["success"] = "success_payee_created"
            } else {
                formErrors["alert"] = "error_payee_create"
            }
        }
    }
    
    func updatePayee() {
        guard let transferTypeId = transferType?.id else {return}
        guard let selectedBank = banks.first else {return}
        guard let payeeId = payeeId else {return}
        
        isLoading = true
        
        let payee = UpdatePayeeRequest(
            payeeId: payeeId,
            accountNumber: accountNumber,
            accountTypeId: transferTypeId,
            bank: selectedBank.name,
            currency: "USD",
            email: email,
            name: name,
            swift: selectedBank.swift
        )
        
        PayeeService().updatePayee(payee: payee)
        {[self] error, result in
                defer {
                    self.isLoading = false
                }
                if let _ = result {
                    formErrors["success"] = "success_payee_edited"
                } else {
                    formErrors["alert"] = "error_payee_editing"
                }
            }
    }
    
    func deletePayee() {
        guard let payeeId = payeeId else {return}
        isLoading = true
        PayeeService().deletePayee(payeeId: payeeId)
        {[self] error, result in
            defer {
                self.isLoading = false
            }
            
            if let _ = result {
                payeeDeleted = true
            } else {
                formErrors["alert"] = "error_something_went_wrong"
            }
        }
    }
}
