//
//  UtilityHistoryViewModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 15/02/22.
//

import SwiftUI

class UtilityHistoryViewModel: ObservableObject, StatementsFilterable {
    
    @Published var utilityHistoryArray: [UtilityBillsHistory] = []
    @Published var statementsFilter = StatementsFilter.initial
    @Published var isLoading: Bool = false
    private var lastRequest:StatementsFilter? = nil
    
    var queryDateFormatter: DateFormatter = DateFormatter()
    
    init(_ isLocal:Bool = false) {
        queryDateFormatter.dateFormat = "yyyy-MM-dd"
        if( isLocal ) {
            utilityHistoryArray = UtilityBillsHistory.listPreview
        }
    }
    
    func fetchStatements() {
        if lastRequest == statementsFilter {
            return
        }
        lastRequest = statementsFilter
        
        isLoading = true
        let queryItems = [
            "fromDate": "\(queryDateFormatter.string(from: statementsFilter.startDate))",
            "toDate": "\(queryDateFormatter.string(from: statementsFilter.endDate))"
        ]
        UtilityBillsService().fetchUtilityBillsHistory(queryItems: queryItems) { error, utilityBillsHistoryResponse in
            self.isLoading = false
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let utilityResult = utilityBillsHistoryResponse {
                self.utilityHistoryArray = utilityResult
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
            
        }
    }
    
}
