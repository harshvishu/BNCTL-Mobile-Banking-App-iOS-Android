//
//  ListNavigationModifier.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 4.03.22.
//

import SwiftUI

struct ListNavigationModifier: ViewModifier {
    @Environment(\.isEnabled) private var isEnabled
    var image: String
    var rightIcon: String? = "IconArrowRight"

    @ViewBuilder
    func body(content: Content) -> some View {
        HStack(spacing: 15) {
            OutlinedIcon(image: image)
            content
                .multilineTextAlignment(.leading)
                .foregroundColor(isEnabled ? Color("PrimaryTextColor") : .gray)
            Spacer()
            if let rightIcon = rightIcon {
                Image(rightIcon)
            }
        }.padding(.vertical, 5)
    }
}

extension View {
    
    func listNavigationWithoutIcon(
        image:String) -> some View {
        self.modifier(ListNavigationModifier(image: image, rightIcon: nil))
    }

    func listNavigation(
        image:String) -> some View {
            self.modifier(ListNavigationModifier(image: image, rightIcon: "IconArrowRight"))
    }

    func listNavigation(
        image:String,
        rightIcon:String?
    ) -> some View {
        self.modifier(ListNavigationModifier(image: image, rightIcon: rightIcon))
    }
}

struct ListNavigationModifier_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            VStack {
                NavigationLink {
                    Text("inner view")
                } label: {
                    Text("With right icon")
                        .listNavigation(image: "IconNews")
                }
                NavigationLink {
                    Text("inner view")
                } label: {
                    Text("Without right Icon")
                        .listNavigationWithoutIcon(image: "IconNews")
                }
                NavigationLink {
                    Text("inner view")
                } label: {
                    Text("Custom right Icon")
                        .listNavigation(image: "IconNews", rightIcon:"IconNews")
                }
            }
        }
    }
}
