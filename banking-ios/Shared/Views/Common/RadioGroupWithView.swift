//
//  RadioGroupNew.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 26.02.22.
//

import SwiftUI

struct RadioGroupWithView<T: Identifiable, RadioItemView: View>: View {
    
    var items: [T]
    @Binding var selected: T?
    var radioItemView: (T) -> RadioItemView
    
    init(
        items: [T],
        selected: Binding<T?>,
        @ViewBuilder radioItemView: @escaping (_ radioItem: T) -> RadioItemView
    ) {
        _selected = selected
        self.items = items
        self.radioItemView = radioItemView
        
    }
    
    var body: some View {
        VStack(alignment: .leading) {
            ForEach(items, id: \.id) { item in
                Button {
                    withAnimation {
                        selected = item
                    }
                } label: {
                    HStack {
                        if let selected = selected {
                            if selected.id == item.id {
                                Image("RadioButtonSelected")
                            } else {
                                Image("RadioButton")
                            }
                        } else {
                            Image("RadioButton")
                        }
                        radioItemView(item)
                        Spacer()
                    }
                }
            }
        }
    }
}

struct PreviewItem: Identifiable {
    let id: String
    let previewName: String
}

struct RadioGroupWithView_Previews: PreviewProvider {
    
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper: View {
        @State var selected: PreviewItem? = nil
        
        var body: some View {
            RadioGroupWithView(items: [
                PreviewItem(id: "one", previewName: "Edno"),
                PreviewItem(id: "two", previewName: "Dve")
            ], selected: $selected) { radioItem in
                Text(radioItem.previewName).foregroundColor(Color(.red))
            }
            
        }
    }
}



