//
//  BackgroundBlurView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 28.01.22.
//

import SwiftUI

struct BackgroundBlurView: UIViewRepresentable {

    func makeUIView(context: Context) -> UIView {
        let view = UIVisualEffectView(effect: UIBlurEffect(style: .regular))
        DispatchQueue.main.async {
            view.superview?.superview?.backgroundColor = .clear
        }
        return view
    }

    func updateUIView(_ uiView: UIView, context: Context) {}
}

struct BackgroundBlurView_Previews: PreviewProvider {
    static var previews: some View {
        BackgroundBlurView()
    }
}
