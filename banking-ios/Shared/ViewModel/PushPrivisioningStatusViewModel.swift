//
//  PushPrivisioningStatus.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 9.03.23.
//

import Foundation

class PushPrivisioningStatusViewModel: ObservableObject {
    
    @Published var state:PushPrivisioningState?
    
    
    func checkStatus() async {
        do {
            let state = try await VersionCheckService().checkState()
            
            await MainActor.run {
                self.state = state
                /*
                self.state = .init(
                    isEnabled: true,
                    launchDate: Date(
                        timeIntervalSince1970: Date()
                            .timeIntervalSince1970 - 20
                    )
                )
                 */
            }
        } catch {
            
        }
    }
}
