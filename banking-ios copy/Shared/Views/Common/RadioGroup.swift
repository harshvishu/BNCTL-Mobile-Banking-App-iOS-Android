//
//  RadioGroup.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 12.01.22.
//

import SwiftUI

//struct SelectableItem: Identifiable {
//    var id: String
//    var label: String
//}

protocol SelectableItem: Identifiable {
    var id: String { get }
    var name: String { get }
}

struct RadioGroup<T: SelectableItem>: View {
    var items: [T] = []
    var itemsLocalized: Bool = true
    @Binding var selected: T?
    
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
                        if (itemsLocalized) {
                            Text(LocalizedStringKey(item.name))
                                .foregroundColor(Color("PrimaryTextColor"))
                                .fontWeight(.regular)
                        } else {
                            Text(item.name)
                                .foregroundColor(Color("PrimaryTextColor"))
                                .fontWeight(.regular)
                        }
                        Spacer()
                    }
                }
            }
        }
    }
}

struct RadioGroup_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            PreviewWrapper()
        }
    }
        
    struct PreviewWrapper:View {
        @State var selected: Branch? = nil
        
        var body: some View {
            RadioGroup<Branch>(items: [
                Branch(id: "0", name: "Branch 1"),
                Branch(id: "1", name: "Branch 2")
            ], selected: $selected)
        }
    }
}
