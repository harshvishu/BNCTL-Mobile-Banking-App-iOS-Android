//
//  ContactsViewModel.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 12.12.22.
//

import Foundation

class ContactsViewModel: ObservableObject {
    
    @Published var contacts: Contacts?
    @Published var isLoading: Bool = false
    
    func fetchBranches() {
        isLoading = true
        InformationService().getContacts { error, contacts in
            self.isLoading = false
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let contacts = contacts {
                self.contacts = contacts
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
}
