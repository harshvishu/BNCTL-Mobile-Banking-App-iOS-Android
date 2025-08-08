//
//  FullScreenModifier.swift
//  Allianz
//
//  Created by Evgeniy Raev on 4.11.21.
//

import SwiftUI

extension View {
    func compatibleFullScreen<Content: View>(
        isPresented: Binding<Bool>,
        onDismiss: (() -> Void)? = nil,
        @ViewBuilder content: @escaping () -> Content) -> some View {
            self.modifier(FullScreenModifier(
                isPresented: isPresented,
                onDismiss: onDismiss,
                builder: content)
            )
        }
    
    func compatibleFullScreen<Item:Identifiable, Content:View>(
        item:Binding<Item?>,
        onDismiss: (() -> Void)? = nil,
        @ViewBuilder content: @escaping (Item) -> Content) -> some View {
            self.modifier(FullScreenItemModifier(
                isPresented: item,
                onDismiss: onDismiss,
                builder: content))
        }
    
    func compatibleFullScreen<Item:Identifiable, Content:View>(
        item: Binding<Item?>,
        @ViewBuilder content: @escaping (Item) -> Content) -> some View {
            self.modifier(FullScreenItemModifier(
                isPresented: item,
                onDismiss: nil,
                builder: content))
        }
    
}

struct FullScreenModifier<V: View>: ViewModifier {
    let isPresented: Binding<Bool>
    let onDismiss: (() -> Void)?
    let builder: () -> V

    @ViewBuilder
    func body(content: Content) -> some View {
        if #available(iOS 14.0, *) {
            content.fullScreenCover(
                isPresented: isPresented,
                onDismiss: onDismiss,
                content: builder)
        } else {
            content.sheet(
                isPresented: isPresented,
                onDismiss: onDismiss,
                content: builder)
        }
    }
}

struct FullScreenItemModifier<Item: Identifiable, V: View>: ViewModifier {
    let isPresented: Binding<Item?>
    let onDismiss: (() -> Void)?
    let builder: (Item) -> V

    @ViewBuilder
    func body(content: Content) -> some View {
        if #available(iOS 14.0, *) {
            content.fullScreenCover(
                item: isPresented,
                onDismiss: onDismiss,
                content: builder)
        } else {
            content.sheet(
                item: isPresented,
                onDismiss: onDismiss,
                content: builder)
        }
    }
}

struct FullScreenModifier_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper: View {
        @State var isPresented = false
        
        var body: some View {
            Button {
                isPresented = true
            } label: {
                Text("Show Full Screen View")
            }
            .compatibleFullScreen(isPresented: $isPresented) {
                Text("Hello! I am a Full Screen View.")
            }

        }
    }
    
}
