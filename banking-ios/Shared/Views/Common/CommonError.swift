//
//  CommonError.swift
//  Allianz (iOS)
//
//  Created by Dimitar Stoyanov Chukov on 29.03.22.
//

import SwiftUI

private struct ShowErrorKey: EnvironmentKey {
    static let defaultValue: Bool = false
}

extension EnvironmentValues {
    var showError: Bool {
        get { self[ShowErrorKey.self] }
        set { self[ShowErrorKey.self] = newValue }
    }
}

extension View {
    func indicateError(_ indicate: Bool) -> some View {
        return self
            .modifier(CommonError())
            .environment(\.showError, indicate)
    }
}

struct CommonError: ViewModifier {
    @Environment(\.showError) private var showError: Bool
    
    func body(content: Content) -> some View {
        content
            .foregroundColor(showError ? Color.red : nil)
    }
}

struct CommonError_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            Text("Label")
                .indicateError(false)
                .padding()
            Text("Label")
                .indicateError(true)
                .padding()
        }
    }
}
