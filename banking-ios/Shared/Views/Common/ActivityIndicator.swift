//
//  ActivityIndicator.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 30.12.21.
//

import SwiftUI

struct ActivityIndicator: View {

    var label: String? = nil
    var compact: Bool = true
    
    init(label: String? = nil) {
        self.label = label
        self.compact = false
    }
    
    init(label: String? = nil, compact: Bool) {
        self.label = label
        self.compact = compact
    }
    
    var body: some View {
        if #available(iOS 14.0, *) {
            if let label = label {
                if compact {
                    HStack {
                        ProgressView()
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
            }
            
        } else {
            Text(LocalizedStringKey(label ?? "common_label_loading"))
                .multilineTextAlignment(.center)
                .font(.system(size: Dimen.TextSize.info))
        }
    }
}

struct ActivityIndicator_Previews: PreviewProvider {
    static var previews: some View {
        ActivityIndicator(label: "Loading...")
    }
}
