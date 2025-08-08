//
//  PendingTransfersViewModel.swift
//  Allianz
//
//  Created by Evgeniy Raev on 1.12.21.
//

import Foundation
import Combine

class PendingTransfersViewModel:ObservableObject {
      
    @Published var selected: Set<PendingTransfer> = []
    @Published var transfers: [PendingTransfer] = []
    @Published var status: SCAStatus? = nil
    @Published var selectAllState:CheckableState = .unchecked
    @Published var disabledTypes:[PendingTransfer.PendingTransferType] = []
    
    @Published var isLoadingTransfers: Bool = false
    
    func actionButtonDisabled() -> Bool {
        return selected.isEmpty
    }
    
    init() {
        let all = $selected
            .map({ $0.count })
//            .map({ selected in
//                if(self.disabledTypes.contains(.mass)) {
//                    let count = selected.reduce(0) { partialResult, transfer in
//                        if(transfer.type == .individual) {
//                            return partialResult + 1
//                        }
//                        return partialResult
//                    }
//                    return count
//                } else {
//                    return selected.count
//                }
//            })
        let individual = $selected
            .map({ $0.reduce(0) { partialResult, transfer in
                if(transfer.type == .individual) {
                    return partialResult + 1
                } else {
                    return partialResult
                }
            }})
        let mass = $selected
            .map({ $0.reduce(0) { partialResult, transfer in
                if(transfer.type == .mass) {
                    return partialResult + 1
                } else {
                    return partialResult
                }
            }})
        
        let totalIndividual = $transfers
            .map({ $0.reduce(0) { partialResult, transfer in
                if(transfer.type == .individual) {
                    return partialResult + 1
                }
                return partialResult
            }})
        
        all
            .combineLatest(individual, mass, totalIndividual)
            .map({ (count, individual, mass, totalIndividual) in
                
                if(mass > 0 ) {
                    return CheckableState.checked
                }
                
                if(count == 0) {
                    return CheckableState.unchecked
                }
                
                if(individual == totalIndividual) {
                    return CheckableState.checked
                }
                
                return CheckableState.indeterminate
            })
            .assign(to: &$selectAllState)
        
        $selected
            .map({ selected in
                if (selected.count > 0) {
                    if( selected.contains(where: {$0.type == .individual })){
                        return [.mass]
                    } else {
                        return [.individual, .mass]
                    }
                } else {
                    return []
                }
            })
            .assign(to: &$disabledTypes)
    }
    
    func transferState(_ transfer:PendingTransfer) -> CheckableState {
        if(selected.contains(transfer)) {
            return .checked
        } else {
            return .unchecked
        }
    }
    
    func toggleTransfer(_ transfer:PendingTransfer) -> Void {
        if(selected.contains(transfer)) {
            selected.remove(transfer)
        } else {
            selected.insert(transfer)
        }
    }
    
    func selectAll() {
        if(selectAllState == .checked) {
            selected.removeAll()
        } else {
            transfers.forEach { transfer in
                if(transfer.type == .individual
                   && selected.contains(transfer) == false )
                {
                    selected.insert(transfer)
                }
            }
        }
    }
    
    func getPendingTransfers() -> Void {
        isLoadingTransfers = true
        PendingTransfersService().getTransfers { error, transfersResponse in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let result = transfersResponse {
                    self.transfers = result
                    self.selected.forEach { transfer in
                        if(result.contains(transfer) == false) {
                            self.selected.remove(transfer)
                        }
                    }
                }else{
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
            self.isLoadingTransfers = false
        }
    }
}

extension PendingTransfer {
    
    var type:PendingTransferType {
        if let _ = numberOfDocuments {
            return .mass
        } else {
            return .individual
        }
    }
    
    enum PendingTransferType {
        case individual, mass
    }
}

enum SCAStatus: String, Identifiable {
    case waiting
    case approved
    case canceled
    
    var id:String { self.rawValue }
}
