//
//  KeyboardModifier.swift
//  MobileBanking
//
//  Created by Kavitha Sambandam on 17/02/23.
//

import Combine
import SwiftUI

extension View {
    func ignoreKeyboardSafeArea() -> some View {
        return modifier(IgnoreOnKeyboardEvent())
    }
}

struct IgnoreOnKeyboardEvent: ViewModifier, KeyboardReadable {
    @State var keyboardVisible = false
    func body(content: Content) -> some View {
        content.onReceive(keyboardPublisher) { value in
            keyboardVisible = value
        }
        .ignoresSafeArea(keyboardVisible ? [.container] : [.container, .keyboard], edges: .bottom)
    }
}
