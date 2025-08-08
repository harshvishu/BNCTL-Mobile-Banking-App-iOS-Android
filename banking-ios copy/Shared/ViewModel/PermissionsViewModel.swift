    //
    //  PermissionsViewModel.swift
    //  Allianz (iOS)
    //
    //  Created by Dimitar Stoyanov Chukov on 15.03.22.
    //

import Foundation

class PermissionsViewModel: ObservableObject {
    
    @Published var permissions: [Permission] = []

    func hasPermission(neededPermission: String) -> Bool {
        permissions.contains(where: { permission in
            permission.actionId == neededPermission
        })
    }
    
    func getObjectIdForPermission(neededPermission: OperationType) -> String? {
        permissions.first { permission in
            permission.actionId == neededPermission.rawValue
        }?.objectId
    }
    
    func updateUserPermissions() {
        CurrentUserService().getCurrentUser() { error, currentUserResponse in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let currentUserResult = currentUserResponse {
                    LoginModel.shared.currentUser = currentUserResult.customer
                    
                    self.permissions = currentUserResult.auth.permissions
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }
}

enum OperationType: String {
    case internalCurrencyTransfer = "internal.currency.transfer"
    case transferInBgn = "transfer.in.bgn"
    case transferBetweenAccounts = "transfer.between.accounts"
    case transferInternational = "transfer.international"
    case utilityBills = "utility.bills"
    case insurancePayment = "insurance.payment"
    case foreignCurrencyExchange = "foreign.currency.exchange"
    case newDebitCard = "new.debit.card"
}

fileprivate extension OperationType {
    var requiredNumOfAccounts:Int {
        switch (self) {
        case .transferInBgn,
                .insurancePayment,
                .internalCurrencyTransfer,
                .utilityBills:
            return 1
        case .transferBetweenAccounts, .foreignCurrencyExchange:
            return 2
        default:
            return 0
        }
    }
}

class PermissionsModel {
    
    static let shared = PermissionsViewModel()
    
}
