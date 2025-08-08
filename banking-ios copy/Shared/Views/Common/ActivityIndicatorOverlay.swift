//
//  ActivityIndicatorOverlay.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 16.10.22.
//

import SwiftUI

struct ActivityIndicatorOverlay: View {
    
    var label: String? = nil
    var compact: Bool = false
    
    init(label: String? = nil) {
        self.label = label
        self.compact = false
    }
    
    init(label: String? = nil, compact: Bool) {
        self.label = label
        self.compact = compact
    }
    
    var body: some View {
        HStack {
            Spacer()
            VStack {
                Spacer()
                ActivityIndicator(label: label, compact: compact)
                Spacer()
            }
            Spacer()
        }
        .background(Color(.white).opacity(0.9))
    }
}

struct ActivityIndicatorOverlay_Previews: PreviewProvider {
    static var previews: some View {
        ActivityIndicatorOverlay(label: "Loading...")
    }
}
