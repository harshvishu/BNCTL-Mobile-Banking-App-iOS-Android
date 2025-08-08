//
//  CheckableView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 18.02.22.
//

import SwiftUI

struct Checkable<Content: View, Label: View>: View {
    @Environment(\.isEnabled) var isEnabled: Bool

    let state: CheckableState
    let action: () -> Void
    let label: (() -> Label)
    let content: (() -> Content)?
    
    init(
        state: CheckableState,
        action: @escaping () -> Void,
        @ViewBuilder label: @escaping () -> Label,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.state = state
        self.action = action
        self.label = label
        self.content = content
    }

    var body: some View {
        Button(action: action) {
            VStack(alignment:.leading) {
                checkableIcon(isEnabled: isEnabled)
                if let c = content {
                    c()
                }
            }
        }.disabled(isEnabled == false)
    }
    
    func checkableIcon(isEnabled:Bool) -> some View {
        HStack {
            Group {
                switch state {
                case .unchecked:
                    Image("Checkbox\(isEnabled ? "" : "Disabled")")
                case .indeterminate:
                    Image("CheckboxPartial")
                case .checked:
                    Image("Checkbox\(isEnabled ? "" : "Disabled")Checked")
                }
            }
            .padding(EdgeInsets(top: -10, leading: -10, bottom: -10, trailing: -10))
            label()
            Spacer()
        }
    }
    
}

enum CheckableState {
    case unchecked
    case indeterminate
    case checked
    
}

extension Checkable where Content == EmptyView {
    init(state: CheckableState, action: @escaping () -> Void, @ViewBuilder label: @escaping () -> Label) {
        self.state = state
        self.label = label
        self.action = action
        self.content = nil
    }
}

struct Checkable_Previews: PreviewProvider {
    static var previews: some View {
        Checkable(state: .checked) {
            // Do nothing
        } label: {
            Text("Label")
        } content: {
            Text("Checkable Content")
        }
        .disabled(true)
        
        Checkable(state: .checked) {
            // Do nothing
        } label: {
            Text("Label")
        } content: {
            Text("Checkable Content")
        }
        .disabled(false)
//        Checkable(state: .all, label: "Select me") {
//            // Do nothing
//        } content: {
//            Text("I am checkable")
//        }
    }
}
