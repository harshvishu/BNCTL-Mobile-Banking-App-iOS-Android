//
//  CashWithdrawalViewModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 11/01/22.
//

import Foundation

class CashWithdrawalViewModel: ObservableObject {
    
    @Published var branches: [Branch] = []
    
    @Published var amount: Double?
    @Published var branch: Branch? = nil
    @Published var executionDate: Date = Date()
    @Published var description: String = ""
    @Published var currency: Currency?
    
    @Published var cashWithdrawalResponse: CashWithdrawalModel?
    
    @Published var operationStatus: OperationStatus? = nil
    @Published var operationStatusError: OperationStatusInfo? = nil
    
    @Published var formErrors: [String: String] = [:]
    
    func fetchBranches() {
        NomenclatureService().fetchBranches() { error, result in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let branches = result {
                self.branches = branches
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }

    func validateAmount() {
        if let amount = amount, amount > 0 {
            formErrors["amount"] = nil
        } else {
            formErrors["amount"] = "error_transfer_validation_amount_greater_than_zero"
        }
    }

    func formValidation() -> Bool {
        validateAmount()

        if (branch == nil) {
            formErrors["branch"] = "error_transfer_validation_amount_greater_than_zero"
        } else {
            formErrors["branch"] = nil
        }
        
        if (currency == nil) {
            formErrors["currency"] = "common_error_field_required"
        } else {
            formErrors["currency"] = nil
        }
        // Description is not required by the API
        return formErrors.isEmpty
    }

    func requestWithdrawal() {
        self.operationStatus = .waiting
        
        let request = CashWithdrawalParams(
            amount: String(format: "%.2f", amount ?? 0),
            description: description,
            currency: currency?.rawValue ?? "",
            executionDate: executionDate.formatTo(format: "yyyy-MM-dd"),
            branch: branch!.id
        )
        
        CashWithdrawalService().requestCashWithdrawal(params: request) { error, result in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                if let serverError = error as? ServerError {
                    if CashWithdrawalError(rawValue: serverError.error.target ?? "") != nil {
                        self.operationStatusError = OperationStatusInfo(
                            title: "operation_common_failed_label_title", // use the generic title
                            message: "error_cash_withdrawal_date") // custom error message
                    }
                }
                self.operationStatus = .failed(nil)
            }
            if let cashWithdrawalResult = result {
                self.cashWithdrawalResponse = cashWithdrawalResult
                self.operationStatus = .success
            } else {
                self.operationStatus = .failed(nil)
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }

}

enum CashWithdrawalError: String {
    case cashWithdrawalDate
}
