//
//  CreditCardStatementViewModel.swift
//  Allianz (iOS)
//
//  Created by Dimitar Stoyanov Chukov on 1.04.22.
//

import Foundation

class CreditCardStatementViewModel: ObservableObject, StatementsFilterable {
    
    @Published var statements: [CreditCardStatement] = []
    @Published var statementsFilter: StatementsFilter = StatementsFilter.initial
    @Published var downloadManager: DownloadManagerService = DownloadManagerService.instance
    private var lastRequest:StatementsFilter? = nil
    
    let card: Card
    
    init(card: Card, _ local: Bool = false) {
        self.card = card
        if (local) {
            statements = CreditCardStatement.list
        }
    }
    
    func fetchStatements() {
        if lastRequest == statementsFilter {
            return
        }
        lastRequest = statementsFilter
        
        CardsService()
            .fetchCreditCardStatements(
                cardNumber: card.cardNumber,
                fromDate: statementsFilter.startDate,
                toDate: statementsFilter.endDate
            ) { error, statementsResponse in
            self.statements = statementsResponse ?? []
        }
    }
    
    func downloadStatement(fileName: String) {
        CardsService()
            .downloadCreditCardStatement(fileName: fileName)
    }
    
}
