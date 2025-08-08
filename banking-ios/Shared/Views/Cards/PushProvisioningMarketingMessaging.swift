//
//  PushProvisioningMarketingMessaging.swift
//  Allianz
//
//  Created by Evgeniy Raev on 8.03.23.
//

import SwiftUI
import PassKit

struct PushProvisioningMarketingMessaging: ViewModifier {
    //TODO: Make config
    static let threeMonts = 3 * (30 * 24 * 60 * 60.0)
    
    //TODO: move the names in constant
    @AppStorage("lastSeenPushProvisioningMarketingMessage") var lastSeen:Double =
        Date()
        .addingTimeInterval(
            (PushProvisioningMarketingMessaging.threeMonts + 30) * -1.0
        ).timeIntervalSince1970
    @AppStorage("hasTokanisedCards") var hasTokanisedCards:Bool = false
    
    @StateObject var model = PushPrivisioningStatusViewModel()
    
    @State var present = false
    let navigateToProvisioningSceen: () -> ()
    
    func body(content: Content) -> some View {
        content
            .onAppear(perform: {
                
                Task {
                    await model.checkStatus()
                    
                    let isPassLibraryAvailable = PKPassLibrary.isPassLibraryAvailable()
                    let canAddPayments = PKAddPaymentPassViewController.canAddPaymentPass()
                    
                    if let state = model.state,
                       isPassLibraryAvailable,
                       canAddPayments,
                       hasTokanisedCards == false,
                       state.isEnabled,
                       Date() > state.launchDate
                    {
                        let lastSeen = Date(timeIntervalSince1970: lastSeen)
                        let lastSeenShouldBe = Date() - PushProvisioningMarketingMessaging.threeMonts
                        
                        if (lastSeen < lastSeenShouldBe) {
                            present = true
                            self.lastSeen = Date().timeIntervalSince1970
                        }
                    }
                }
            })
            .sheet(isPresented: $present) {
                present = false
            } content: {
                VStack {
                    PushProvisioningMarketingMessageView {
                        present = false
                    } navigate: {
                        navigateToProvisioningSceen()
                        present = false
                    }

                }
            }
            .environmentObject(model)

    }
}

struct PushProvisioningMarketingMessaging_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            Text("home view")
        }
        .modifier(PushProvisioningMarketingMessaging(navigateToProvisioningSceen: {
            
        }))
        
        RessetView()
            .previewDisplayName("Controlls")
    }
    
    struct RessetView:View {
        @AppStorage("lastSeenPushProvisioningMarketingMessage") var lastSeen:Double = Date().timeIntervalSince1970
        @AppStorage("hasTokanisedCards") var hasTokanisedCards:Bool = false
        
        var body: some View {
            
            VStack {
                Text("\(lastSeen)")
                Button {
                    UserDefaults
                        .standard
                        .removeObject(
                            forKey: "lastSeenPushProvisioningMarketingMessage"
                        )
                } label: {
                    Text("delete entry")
                }
                
                Toggle(isOn: $hasTokanisedCards) {
                   Text("has tokanised cards")
                }
                
                Button {
                    lastSeen = Date().timeIntervalSince1970
                } label: {
                    Text("today")
                }
            }
            .padding()
        }
    }
}
