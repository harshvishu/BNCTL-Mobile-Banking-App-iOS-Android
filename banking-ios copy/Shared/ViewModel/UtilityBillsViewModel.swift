//
//  UtilityBillsViewModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 22/12/21.
//

import Foundation
import SwiftUI

class UtilityBillsViewModel: ObservableObject {
    
    @Published var isLoading: Bool = false
 
    @Published var selected: Set<UtilityBillPayment> = []
    @Published var utilityBills: [UtilityBillPayment] = []
    
    @Published var status: SCAStatus? = nil
    
    init(_ isLocal: Bool = false) {
        if (isLocal) {
            utilityBills = UtilityBillPayment.listPreview
            selected.insert(utilityBills[0])
            selected.insert(utilityBills[2])
        }
    }
    
    // Fetch
    
    func fetchUtilityBills() {
        isLoading = true
        UtilityBillsService().fectchUtilityBills() { error, utilityBillsResponse in
            self.isLoading = false
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let utilityBillsResult = utilityBillsResponse {
                self.utilityBills = utilityBillsResult
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
    
    // List
    
    func toggleUtilityBill(utilityBill: UtilityBillPayment) -> Void {
        if (selected.contains(utilityBill)) {
            selected.remove(utilityBill)
        } else {
            selected.insert(utilityBill)
        }
    }
    
    func isEndOfDay() async throws -> Bool {
        return try await EndOfDayService().checkEndOfDay()
    }
    
    func actionButtonDisabled() -> Bool {
        return selected.isEmpty
    }
    
    func selectAllState() -> CheckableState {
        if (utilityBills.isEmpty) {
            return .unchecked
        }
        let unpaidUtilityBillsCount = utilityBills.reduce(into: Int(0)) { acc, utilityBill in
            if !utilityBill.isPaid {
                acc = acc + 1
            }
        }
        if (unpaidUtilityBillsCount == 0) {
            return .unchecked
        }
        let unpaidSelectedUtilityBillsCount = selected.reduce(into: Int(0)) { acc, utilityBill in
            if !utilityBill.isPaid {
                acc = acc + 1
            }
        }
        return (unpaidUtilityBillsCount != unpaidSelectedUtilityBillsCount)
            ? .unchecked : .checked
    }
    
    func selectAll() {
        if (selectAllState() == .checked) {
            selected.removeAll()
        } else {
            utilityBills.forEach { utilityBill in
                if (!utilityBill.isPaid && !selected.contains(utilityBill)) {
                    selected.insert(utilityBill)
                }
            }
        }
    }
    
    // Bill Payment
    var sumSelected: Dictionary<String, Double> {
        return selected.reduce(into: Dictionary<String, Double>()) { partialResult, bill in
            partialResult[bill.currencyName] =
                (partialResult[bill.currencyName] ?? 0) + bill.billAmount
        }
    }
    
    func getIds() -> [String] {
        return selected.reduce(into: [String]()) { partialResult, bill in
            partialResult.append(bill.id)
        }
        
    }
}
