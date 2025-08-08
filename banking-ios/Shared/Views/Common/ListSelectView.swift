//
//  BranchSelectView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 14.01.22.
//

import SwiftUI

struct ListSelectView<T: SelectableItem>: View {
    
    var label: String = "Select an option"
    var items: [T] = []
    var itemsLocalized: Bool = true
    
    @Binding var selected: T?
    @State var isActive: Bool = false
    
    var body: some View {
        ZStack {
            // Black magic - for iOS 14 only.
            // This dummy NavigationLink prevents
            // the Navigation to pop back when opened.
            NavigationLink(destination: EmptyView()) {
                EmptyView()
            }
            NavigationLink(isActive: $isActive) {
                List {
                    ForEach(items, id: \.id) { item in
                        Button (action: {
                            selected = item
                            isActive = false
                        }, label: {
                            HStack {
                                if (itemsLocalized) {
                                    Text(LocalizedStringKey(item.name))
                                        .foregroundColor(Color("PrimaryTextColor"))
                                        .fontWeight(.regular)
                                } else {
                                    Text(item.name)
                                        .foregroundColor(Color("PrimaryTextColor"))
                                        .fontWeight(.regular)
                                }
                                if let selected = selected {
                                    if selected.id == item.id {
                                        Spacer()
                                        Text("✓")
                                            .foregroundColor(.accentColor)
                                            .fontWeight(.bold)
                                    }
                                }
                            }
                        })
                    }
                }
                .navigationBarTitle(LocalizedStringKey(label), displayMode: .inline)
                
            } label: {
                HStack {
                    Text(LocalizedStringKey(label))
                        .fontWeight(.regular)
                    Spacer()
                    if let selected = selected {
                        Text(selected.name)
                            .foregroundColor(Color("PrimaryTextColor"))
                            .fontWeight(.regular)
                    }
                }
            }
        }
    }
}

struct ListSelectViewPreviews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            PreviewWrapper()
        }
    }
        
    struct PreviewWrapper:View {
        @State var selected: Branch? = nil
        
        var body: some View {
            ListSelectView(items: [
                Branch(id: "0", name: "Option 1"),
                Branch(id: "1", name: "Option 2"),
                Branch(id: "2", name: "Option 3")
            ], selected: $selected)
        }
    }
}
