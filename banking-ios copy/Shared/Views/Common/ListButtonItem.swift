//
//  ListButtonItem.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 2.02.22.
//

import SwiftUI

struct ListButtonItem: View {
    var label: LocalizedStringKey
    var image: String
    var rightIcon: String?
    var handler: () -> Void
    
    
    var body: some View {
        Button(action: handler) {
            Text(label)
                .listNavigation(image: image, rightIcon:rightIcon)
        }.padding(.vertical, 5)
    }
}

struct ListButtonItem_Previews: PreviewProvider {
    static var previews: some View {
        ListButtonItem(
            label: "Button item",
            image: "IconLogout"
        ){
            
        }
    }
}
