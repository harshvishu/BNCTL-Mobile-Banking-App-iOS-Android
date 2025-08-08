//
//  CardStatementViewModel.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 5.01.22.
//

import Foundation

class CardStatementViewModel: ObservableObject, StatementsFilterable {
    
    @Published var statements: [CardStatement] = []
    @Published var statementsFilter = StatementsFilter.initial
    @Published var isLoadingStatements: Bool = false
    private var lastRequest:StatementsFilter? = nil
    
    let card: Card
    
    init(card: Card) {
        self.card = card
    }
    
    func fetchStatements() {
        if lastRequest == statementsFilter {
            return
        }
        lastRequest = statementsFilter
        
        isLoadingStatements = true
        CardsService().fetchCardStatements(
            cardNumber: "\(card.accountId)-\(card.cardNumber)",
            fromDate: statementsFilter.startDate,
            toDate: statementsFilter.endDate
        ) { error, statementsResponse in
            self.isLoadingStatements = false
            if let receivedStatements = statementsResponse {
                self.statements = receivedStatements
            }
        }
    }
}
