//
//  CommonButton.swift
//  laboratory0 (iOS)
//
//  Created by Evgeniy Raev on 22.10.21.
//

import SwiftUI

extension View {
    func commonButtonStyle() -> some View {
        self.modifier(CommonButton())
    }
}

struct CommonButton: ViewModifier {
    @Environment(\.isEnabled) private var isEnabled
    
    func body(content: Content) -> some View {
        content
            .frame(maxWidth: .infinity, minHeight: 44)
            .background(isEnabled ? Color("PrimaryButtonColor") : Color.gray)
            .cornerRadius(5)
            .foregroundColor(.white)
    }
}

struct CommonButton_Previews: PreviewProvider {
    static var previews: some View {
        Button {
            print("Something")
        } label: {
            Text("Label")
        }
        .commonButtonStyle()
        .padding()
    }
}
