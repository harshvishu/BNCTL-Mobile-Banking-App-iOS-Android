//
//  ListNavItem.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 2.02.22.
//

import SwiftUI

struct ListNavItem<Content: View>: View {
    
    var label: LocalizedStringKey
    var image: String

    @ViewBuilder var content: Content
    
    var body: some View {
        NavigationLink(destination:{
            content
        }){
            Text(label)
                .listNavigation(image: image)
        } // .padding(.vertical, 5)
        .isDetailLink(false)
    }
}

struct ListNavItem_Previews: PreviewProvider {
    static var previews: some View {
        ListNavItem(
            label: "List Item",
            image: "IconNews"
        ) {
            Text("Some Item")
        }
    }
}
