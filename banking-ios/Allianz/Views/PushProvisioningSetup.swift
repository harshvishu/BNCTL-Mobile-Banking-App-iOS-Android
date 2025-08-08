//
//  PushProvisioningSetup.swift
//  BNCTL
//
//  Created by Evgeniy Raev on 20.02.23.
//

import SwiftUI
import MeaPushProvisioning

struct PushProvisioningSetupModifier: ViewModifier {
    
    static func setup() {
        MeaPushProvisioning.loadConfig("Allianz_mea_config")
        
        MeaPushProvisioning.setDebugLoggingEnabled(true)
    }
    
    func body(content: Content) -> some View {
        content
    }
}
