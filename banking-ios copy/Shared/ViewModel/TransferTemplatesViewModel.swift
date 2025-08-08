//
//  TransferTemplatesViewModel.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 5.12.21.
//

import Foundation

class TransferTemplatesViewModel: ObservableObject {
    
    @Published var isLoading: Bool = false
    @Published var templates: [TransferTemplate] = []
    
    init(isLocal: Bool = false) {
        if (isLocal) {
            templates = TransferTemplate.previewList
        }
    }
    
    func getTemplates() {
        isLoading = true
        TransferTemplatesService().getTemplates { error, templatesResponse in
            self.isLoading = false
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let result = templatesResponse {
                    self.templates = result
                }else{
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }
}
