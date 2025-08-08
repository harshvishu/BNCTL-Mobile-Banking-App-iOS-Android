//
//  BranchesViewModel.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 26.01.22.
//

import Foundation

class BranchesViewModel: ObservableObject {
    
    @Published var branches: [Location] = []
    @Published var isLoading: Bool = false
    
    func fetchBranches() {
        isLoading = true
        LocationService().fetchBranches { error, branches in
            self.isLoading = false
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let branches = branches {
                self.branches = branches
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
    
}
