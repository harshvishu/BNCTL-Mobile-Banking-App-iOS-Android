//
//  AddToWalletView.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 25.01.23.
//

import SwiftUI
import PassKit
import MeaPushProvisioning


struct AddToWalletView: UIViewControllerRepresentable {
    
    let tokenizationResponseData: MppInitializeOemTokenizationResponseData
    let didFinishAdding: (_ pass:PKPaymentPass?, _ error: Error?) -> Void
    
    init(
        tokenizationResponseData: MppInitializeOemTokenizationResponseData,
        didFinishAdding: @escaping (_ pass:PKPaymentPass?, _: Error?) -> Void
    ) {
        self.tokenizationResponseData = tokenizationResponseData
        self.didFinishAdding = didFinishAdding
    }
    
    func makeUIViewController(context: Context) -> PKAddPaymentPassViewController {
        
        let configuration = tokenizationResponseData.addPaymentPassRequestConfiguration!
        
        let paymentPassController = PKAddPaymentPassViewController.init(
            requestConfiguration: configuration,
            delegate: context.coordinator)
        
        return paymentPassController!
        
    }
    
    func updateUIViewController(
        _ uiViewController: PKAddPaymentPassViewController,
        context: Context
    ) {
        
    }
    
    public func makeCoordinator() -> AddToWalletView.Coordinator {
        return Coordinator(self)
    }
    
    public class Coordinator: NSObject, PKAddPaymentPassViewControllerDelegate {
        
        let parent:AddToWalletView
        
        init(_ parent:AddToWalletView) {
            self.parent = parent
        }
        
        public func addPaymentPassViewController(
            _ controller: PKAddPaymentPassViewController,
            generateRequestWithCertificateChain certificates: [Data],
            nonce: Data,
            nonceSignature: Data,
            completionHandler handler: @escaping (PKAddPaymentPassRequest) -> Void
        ) {
            let tokenizationData = MppCompleteOemTokenizationData(
                tokenizationReceipt: self.parent.tokenizationResponseData.tokenizationReceipt,
                certificates: certificates,
                nonce: nonce,
                nonceSignature: nonceSignature)
            
            MeaPushProvisioning.completeOemTokenization(tokenizationData){ (responseData, error) in
                
                if  (responseData?.isValid())! {
                    handler((responseData?.addPaymentPassRequest)!)
                }
            }
            
        }
        
        public func addPaymentPassViewController(
            _ controller: PKAddPaymentPassViewController,
            didFinishAdding pass: PKPaymentPass?,
            error: Error?
        ) {
            self.parent.didFinishAdding(pass, error)
            //self.presentedViewController?.dismiss(animated: true, completion: nil)
        }
        
    }
    
    
}

/*
 struct AddToWalletView_Previews: PreviewProvider {
 static var previews: some View {
 AddToWalletView()
 }
 }
 */
