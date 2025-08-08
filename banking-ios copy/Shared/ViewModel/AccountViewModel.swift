//
//  AccontViewModel.swift
//  Allianz
//
//  Created by Evgeniy Raev on 27.10.21.
//

import Foundation

class AccountViewModel: ObservableObject, StatementsFilterable {

    @Published var statements: [Statement] = []
    @Published var statementsFilter = StatementsFilter.initial
    @Published var isLoadingStatements: Bool = false
    private var lastRequest:StatementsFilter? = nil
    
    var queryDateFormatter: DateFormatter = DateFormatter()
    
    var account: Account
    
    init(account: Account, _ local: Bool = false) {
        self.account = account
        queryDateFormatter.dateFormat = "yyyy-MM-dd"
        if (local) {
            statements = Statement.list
//            statements = []
        }
    }
    
    func fetchStatements() {
        if lastRequest == statementsFilter {
            return
        }
        lastRequest = statementsFilter
        
        isLoadingStatements = true
        let queryItems = [
            "fromDate":"\(queryDateFormatter.string(from: statementsFilter.startDate))",
            "toDate":"\(queryDateFormatter.string(from: statementsFilter.endDate))"
        ]
        AccountService().getAccountStatement(
            accountId: account.accountId,
            queryItems: queryItems
        ) { error, statementResponse in
            self.isLoadingStatements = false
            if let receivedStatements = statementResponse {
                self.statements = receivedStatements
            }
        }
    }
}
    
