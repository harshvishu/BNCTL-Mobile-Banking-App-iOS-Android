//
//  ActivityIndiatorViewModifier.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 18/04/23.
//

import SwiftUI

struct ActivityIndiatorViewModifier: ViewModifier {
    @Binding var isLoading: Bool
    var label: String? = nil
    var compact: Bool = true

    func body(content: Content) -> some View {
        ZStack {
            content
                .disabled(isLoading)
                .blur(radius: isLoading ? 3 : 0)
            
            if isLoading {
                if let label = label {
                    if compact {
                        HStack {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle())
                            Text(LocalizedStringKey(label))
                                .multilineTextAlignment(.center)
                                .font(.system(size: Dimen.TextSize.info))
                                .padding(.init(top: 0, leading: Dimen.Spacing.short, bottom: 0, trailing: 0))
                                .foregroundColor(Color("SecondaryTextColor"))
                        }
                    } else {
                        ProgressView {
                            Text(LocalizedStringKey(label))
                                .multilineTextAlignment(.center)
                                .font(.system(size: Dimen.TextSize.info))
                        }
                    }
                } else {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                }
            }
        }
    }
}

struct ActivityIndiatorViewModifier_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            Text("Hello!")
                .loading(isLoading: .constant(true), label: "operation_common_waiting_simple_label_message")
        }
    }
}

extension View {
    func loading(isLoading: Binding<Bool>, label: String? = nil, compact: Bool = true) -> some View {
        self.modifier(ActivityIndiatorViewModifier(isLoading: isLoading, label: label, compact: compact))
    }
}
