//
//  AuthenticateView.swift
//  laboratory0
//
//  Created by Evgeniy Raev on 25.10.21.
//

import SwiftUI

struct OperationWaitingView: View {
    
    let type: WaitingType
    
    let titleKey: LocalizedStringKey
    let messageKey: LocalizedStringKey
    
    init(type: WaitingType, titleKey: LocalizedStringKey, messageKey: LocalizedStringKey) {
        self.type = type
        self.titleKey = titleKey
        self.messageKey = messageKey
    }
    
    init(type: WaitingType, localizedStringKeyPrefix: String = "common") {
        self.type = type
        self.titleKey = LocalizedStringKey(stringLiteral: "operation_\(localizedStringKeyPrefix)_waiting_\(type.rawValue)_label_title")
        self.messageKey = LocalizedStringKey(stringLiteral: "operation_\(localizedStringKeyPrefix)_waiting_\(type.rawValue)_label_message")
    }
    
    var body: some View {
        VStack(spacing: Dimen.Spacing.regular) {
            if (type == .sca) {
                Image("IconAuthenticate")
                    .resizable()
                    .frame(width: 103, height: 103)
            }
            HStack (spacing: Dimen.Spacing.short) {
                ProgressView()
                Text(titleKey)
                    .fontWeight(.bold)
            }
            Text(messageKey)
                .multilineTextAlignment(.center)
                .foregroundColor(Color("SecondaryTextColor"))
                .font(.subheadline)
        }
        .padding()
    }
    
    enum WaitingType: String {
        case simple
        case sca
    }
}

struct AuthenticateView_Previews: PreviewProvider {
    static var previews: some View {
        OperationWaitingView(type: .sca)
            .previewDisplayName("sca")
        
        OperationWaitingView(type: .simple)
            .previewDisplayName("simple")
    }
}
