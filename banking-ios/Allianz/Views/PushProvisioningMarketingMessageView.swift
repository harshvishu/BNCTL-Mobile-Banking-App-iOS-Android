//
//  PushProvisioningMarketingMessageView.swift
//  Allianz
//
//  Created by Evgeniy Raev on 9.03.23.
//

import SwiftUI

struct PushProvisioningMarketingMessageView: View {
    let close:() -> ()
    let navigate:() -> ()
    
    var body: some View {
        VStack {
            HStack {
                Spacer()
                
                Button {
                    close()
                } label: {
                    Image(systemName: "xmark")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 20)
                        .foregroundColor(.white)
                        .padding(15)
                }
            }
            Image("Marketing messaging/Allianz - aPay")
                .resizable()
                .scaledToFit()
                .frame(
                    maxWidth: 240,
                    maxHeight: 60
                )
            
            Spacer()
            
            Text("push_provisioning_marketing_message")
                .multilineTextAlignment(.center)
                .foregroundColor(.white)
                .font(.custom("Averta", size: 22, relativeTo: .title))
                .lineSpacing(9)
                .padding(.vertical, 20)

            Spacer()
            
            Image("Marketing messaging/iPhone")
                .resizable()
                .scaledToFit()
                .frame(maxWidth: 206, maxHeight: 416)
            
            Spacer()
            
            Button {
                navigate()
            } label: {
                AddToWalletButton()
                    .frame(width: 128, height: 44)
            }
            .padding(30)
        }
        .padding(.horizontal)
        .background(
            Image("Marketing messaging/background")
                .resizable()
                .scaledToFill()
        )
    }
}

struct PushProvisioningMarketingMessageView_Previews: PreviewProvider {
    static var previews: some View {
        
        PreviewWrapper()
            .previewDisplayName("Final")
        
        PushProvisioningMarketingMessageView {
            
        } navigate: {
            
        }
        .previewDisplayName("Desing")
        
        TestView()
            .previewDisplayName("Size")

    }
    struct PreviewWrapper:View {
        @State var visible = true
        var body: some View {
            Text("hello")
                .sheet(isPresented: $visible) {
                    PushProvisioningMarketingMessageView {
                        
                    } navigate: {
                        
                    }
                }
        }
    }
    
    struct TestView:View {
        var body: some View {
            GeometryReader { size in
                
                VStack {
                    Text("\(size.size.width)")
                    Text("\(size.size.height)")
                }
                .padding(100)
            }.ignoresSafeArea()
        }
    }
}
