    //
    //  RequestCardViewModel.swift
    //  Allianz (iOS)
    //
    //  Created by Peter Velchovski on 18.01.22.
    //

import Foundation
import SwiftUI

class RequestCardViewModel: ObservableObject {
    
    let receiveStatementsBranch = StatementsReceiveLocation(id: "branch", name: "new_debit_card_statements_location_radio_option_at_branch")
    let receiveStatementsEmail = StatementsReceiveLocation(id: "email", name: "new_debit_card_statements_location_radio_option_by_email")
    
    @Published var branches: [Branch] = []
    @Published var cardProducts: [CardProduct] = []
    @Published var receiveStatementsLocations: [StatementsReceiveLocation] = []
    
    @Published var pickupBranch: Branch? = nil {
        didSet {
            formErrors["pickupBranch"] = nil
        }
    }
    @Published var cardProduct: CardProduct? = nil
    @Published var receiveStatementsAt: StatementsReceiveLocation? = nil {
        didSet {
            formErrors["receiveStatementsAt"] = nil
        }
    }
    
    @Published var linkedAccount: Account?
    @Published var embossName: String = ""
    @Published var email: String = ""
    @Published var termsAccepted: Bool = false
    
    @Published var cardRequestStatus: CardRequestStatus? = nil
    
    @Published var formErrors: [String: String] = [:]
    
    init() {
        receiveStatementsLocations.append(
            contentsOf: [receiveStatementsBranch, receiveStatementsEmail])
    }
    
    func fetchCardProducts() {
        CardsService().fetchCardProducts() { error, cardProctusResult in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let cardProctus = cardProctusResult {
                self.cardProducts = cardProctus
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
    
    func fetchBranches() {
        NomenclatureService().fetchBranches() { error, branchesResult in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let branches = branchesResult {
                self.branches = branches
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
    
    func formValidation() -> [String:String] {
        
        if linkedAccount == nil {
            formErrors["linkedAccount"] = "new_debit_card_error_select_account"
        } else {
            formErrors["linkedAccount"] = nil
        }
        
        if cardProduct == nil {
            formErrors["cardProduct"] = "new_debit_card_error_select_card_type"
        } else {
            formErrors["cardProduct"] = nil
        }
        
        if embossName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            formErrors["embossName"] = "new_debit_card_error_enter_name"
        } else if embossName.range(of: "^(([a-zA-Z]?)([a-zA-Z]*[ ]?[.]?)+([a-zA-Z]?))$", options: .regularExpression) == nil {
            formErrors["embossName"] = "new_debit_card_error_enter_name_latin_only"
        } else if embossName.count > 21 {
            formErrors["embossName"] = "new_debit_card_error_enter_name_character_limit"
        } else {
            formErrors["embossName"] = nil
        }
        
        if pickupBranch == nil {
            formErrors["pickupBranch"] = "new_debit_card_error_select_location"
        } else {
            formErrors["pickupBranch"] = nil
        }
        
        if receiveStatementsAt == nil {
            formErrors["receiveStatementsAt"] = "common_error_field_required"
        } else {
            formErrors["receiveStatementsAt"] = nil
            if receiveStatementsAt == receiveStatementsEmail
                && (email.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
                    || email.range(of: "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}", options: .regularExpression) == nil) {
                formErrors["email"] = "new_debit_card_error_invalid_email"
            } else {
                formErrors["email"] = nil
            }
        }
        
        if termsAccepted == false {
            formErrors["termsAccepted"] = "new_debit_card_error_please_agree_to_terms"
        } else {
            formErrors["termsAccepted"] = nil
        }
        
        return formErrors
    }
    
    func requestCard() {
        cardRequestStatus = .isLoading
        
        let isStatementOnDemand = receiveStatementsAt?.id == receiveStatementsBranch.id
        let isStatementOnEmail = receiveStatementsAt?.id == receiveStatementsEmail.id
        let selectedLinkedAccount = linkedAccount as! Account
        
        let cardRequest: CardRequest = CardRequest(
            accountIban: selectedLinkedAccount.iban,
            locationId: pickupBranch!.id,
            embossName: embossName,
            cardProductCode: cardProduct!.id,
            cardProcutName: cardProduct!.name,
            statementOnDemand: isStatementOnDemand,
            statementOnEmail: isStatementOnEmail
        )
        
        CardsService().requestCard(params: cardRequest) { error, isSuccessful in
            if let error = error {
                self.cardRequestStatus = .failre
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let isSuccessful = isSuccessful {
                    if isSuccessful {
                        self.cardRequestStatus = .success
                    } else {
                        self.cardRequestStatus = .failre
                    }
                } else {
                    self.cardRequestStatus = .failre
                }
            }
        }
    }
    
    enum CardRequestStatus: String, Identifiable {
        case failre
        case success
        case isLoading
        
        var id: String { self.rawValue }
    }
    
}

struct StatementsReceiveLocation: SelectableItem, Equatable {
    var id: String
    var name: String
}
