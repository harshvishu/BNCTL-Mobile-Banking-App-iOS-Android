//
//  StatementsFilter.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 5.01.22.
//

import Foundation

struct StatementsFilter {
    var startDate: Date {
        selStartDate ?? Date()
    }
    var endDate: Date {
        selEndDate ?? Date()
    }
    var selStartDate: Date?
    var selEndDate: Date?
    
    static var initial: StatementsFilter {
        return self.init(
            selStartDate: Date(),
            selEndDate: Date()
        )
    }
}

protocol StatementsFilterable {
    var statementsFilter: StatementsFilter { get set }
    func fetchStatements() -> Void
}
