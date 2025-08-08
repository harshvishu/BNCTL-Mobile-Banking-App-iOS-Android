//
//  DocumentsHistoryViewModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 02/02/22.
//

import SwiftUI

class DocumentsHistoryViewModel: ObservableObject, StatementsFilterable {
    
    @Published var documentHistoryArray: [DocumentsHistoryData] = []
    @Published var statementsFilter = StatementsFilter.initial
    @Published var isLoadingStatements: Bool = false
    private var lastRequest: StatementsFilter? = nil
    
    init(_ isLocal:Bool = false){
        if( isLocal ) {
            documentHistoryArray = DocumentsHistoryData.previewList
        }
    }
    
    func fetchStatements() {
        if lastRequest == statementsFilter {
            return
        }
        lastRequest = statementsFilter
        
        isLoadingStatements = true
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        let queryItems = [
            "fromDate": "\(dateFormatter.string(from: statementsFilter.startDate))",
            "toDate": "\(dateFormatter.string(from: statementsFilter.endDate))"]
        DocumentsHistoryService().getDocumentsHistory(
            queryItems: queryItems
        ) { error, documentResponse in
            self.isLoadingStatements = false
            if let documentResult = documentResponse {
                self.documentHistoryArray = documentResult
            }
        }
    }

}
