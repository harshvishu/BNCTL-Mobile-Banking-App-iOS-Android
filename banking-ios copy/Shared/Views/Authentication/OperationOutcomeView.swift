//
//  TransactionStatus.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 19.11.21.
//

import SwiftUI

struct OperationOutcomeView: View {
    
    let imageKey: String
    let titleKey: LocalizedStringKey
    let messageKey: LocalizedStringKey
    
    let action: () -> Void
    
    // Custom error initializer
    init(imageKey: String, titleKey: LocalizedStringKey, messageKey: LocalizedStringKey,  action: @escaping () -> Void) {
        self.imageKey = imageKey
        self.titleKey = titleKey
        self.messageKey = messageKey
        self.action = action
    }
    
    // Standard error initializer
    init(status: OperationStatus, localizedStringKeyPrefix: String = "common",  action: @escaping () -> Void) {
        self.imageKey = status == .success
            ? "OperationStatusSuccess"
            : "OperationStatusError"
        self.titleKey = LocalizedStringKey(stringLiteral:
            "operation_\(localizedStringKeyPrefix)_\(status.rawValue)_label_title")
        self.messageKey = LocalizedStringKey(stringLiteral:
            "operation_\(localizedStringKeyPrefix)_\(status.rawValue)_label_message")
        self.action = action
    }
    
    var body: some View {
        VStack {
            Spacer()
            VStack(spacing: Dimen.Spacing.regular) {
                Image(imageKey)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 72, height: 72)
                Group {
                    Text(titleKey)
                        .font(.system(size: Dimen.TextSize.title, weight: .bold, design: .default))
                    Text(messageKey)
                        .font(.system(size: Dimen.TextSize.info, weight: .regular, design: .default))
                        .foregroundColor(Color("SecondaryTextColor"))
                }.multilineTextAlignment(.center)
            }.padding(.horizontal)
            Spacer()
            Button(action: action) {
                Text("operation_outcome_button_done")
                    .commonButtonStyle()
            }
            .padding(.horizontal)
        }
        .padding()
    }
}

struct OperationOutcomeView_Previews: PreviewProvider {
    static var previews: some View {
        OperationOutcomeView(status: .failed("insufficient_funds"), localizedStringKeyPrefix: "transfer") {
            print("Bla!")
        }
        .previewDisplayName("EN")
        
        OperationOutcomeView(status: .failed("insufficient_funds"), localizedStringKeyPrefix: "transfer") {
            print("Bla!")
        }
        .environment(\.locale, Locale(identifier: "bg"))
        .previewDisplayName("BG")
    }
}
