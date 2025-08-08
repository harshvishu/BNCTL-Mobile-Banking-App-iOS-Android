//
//  AddToWalletButton.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 24.01.23.
//

import SwiftUI
import PassKit
import UIKit

struct AddToWalletButton: UIViewRepresentable {
    
    func makeUIView(context: Context) -> PKAddPassButton {
        let addPassButton = PKAddPassButton(addPassButtonStyle: PKAddPassButtonStyle.black)
        addPassButton.frame = CGRect(
            x:0,
            y: 0,
            width: 128,
            height: 40
        )
        
        return addPassButton
    }
    
    func updateUIView(
        _ uiView: PKAddPassButton,
        context: UIViewRepresentableContext<AddToWalletButton>
    ) {
        
    }
    
    func makeCoordinator() -> AddToWalletButton.Coordinator {
       return Coordinator()
    }
    
    public class Coordinator: NSObject {
        
    }
}

struct AddToWalletButton_Previews: PreviewProvider {
    static var previews: some View {
        AddToWalletButton()
            .frame(width: 235,
                   height: 235)
        
        VStack {
            AddToWalletButton()
                .frame(width: 235,
                       height: 235)
            AddToWalletButton()
                .frame(width: 236,
                       height: 235)
            
            ZStack {
                AddToWalletButton()
                    .frame(
                        width: 150,
                           height: 40
                    )
                
                Rectangle()
                    .foregroundColor(.orange)
                    .frame(width: 150,
                           height: 40)
                
                
            }
            
        }
    }
}
