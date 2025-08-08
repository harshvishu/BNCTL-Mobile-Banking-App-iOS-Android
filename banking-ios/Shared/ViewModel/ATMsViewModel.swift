//
//  BranchesViewModel.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 26.01.22.
//

import Foundation

class ATMsViewModel: ObservableObject {
    
    @Published var atms: [Location] = []
    @Published var isLoading: Bool = false
    
    func fetchATMs() {
        isLoading = true
        LocationService().fetchATMs { error, atms in
            self.isLoading = false
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let atms = atms {
                self.atms = atms
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
    
}
