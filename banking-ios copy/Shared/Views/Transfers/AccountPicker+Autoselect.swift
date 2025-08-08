//
//  AccountPicker+Autoselect.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 20.01.23.
//

import SwiftUI

private struct AccountPickerAutoselectKey: EnvironmentKey {
    static let defaultValue:AccountPicker.Autoselect = .auto
}


extension EnvironmentValues {
    var acountPickerAutoselect: AccountPicker.Autoselect {
        get { self[AccountPickerAutoselectKey.self] }
        set { self[AccountPickerAutoselectKey.self] = newValue }
    }
}


extension AccountPicker {
    func autoSelect(_ type:Autoselect) -> some View {
        return self
            .environment(\.acountPickerAutoselect, type)
    }
}
