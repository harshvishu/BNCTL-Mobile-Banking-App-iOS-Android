//
//  CheckboxView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 17.01.22.
//

import SwiftUI

struct CheckboxView: View {
    @Binding var isChecked: Bool
    var body: some View {
        let imageName = isChecked ? "CheckboxChecked" : "Checkbox"
        Button{
            self.isChecked.toggle()
        } label: {
            Image(imageName)
                .padding(EdgeInsets(top: -10, leading: -10, bottom: -10, trailing: -10))
        }
    }
}

struct CheckboxView_Previews: PreviewProvider {
    struct CheckboxPreview: View {
        @State var isChecked = false
        var body: some View {
            CheckboxView(isChecked: $isChecked)
        }
    }
    
    static var previews: some View {
        CheckboxPreview()
    }
}
