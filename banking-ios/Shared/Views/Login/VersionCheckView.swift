//
//  VersionCheckView.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 10.01.23.
//

import SwiftUI

struct VersionCheckViewModifier: ViewModifier {
    @StateObject var model = VersionViewModel()
    
    func body(content: Content) -> some View {
        content
            .onAppear {
                Task {
                    await model.checkVersion()
                }
            }
            .fullScreenCover(isPresented: $model.force) {
                AppNeedToBeUpdatedView()
            }
            .compatibleAllert(
                showAlert: $model.recomend,
                titleKey: "update_minor \(model.verion?.latest.versionName ?? "")",
                defailtLabel: "update_minor_open_appstore",
                cancelLabel: "update_minor_later") {
                    openUpdateUrl()
                } cancelAction: {
                    model.viewed()
                }
    }
}

struct AppNeedToBeUpdatedView: View {
    var body: some View {
        VStack {
            Text("update_force_main")
                .fontWeight(.bold)
            Text("lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ")
                .foregroundColor(Color("SecondaryTextColor"))
                .multilineTextAlignment(.center)
                .padding(.top, 2)
            Button {
                openUpdateUrl()
            } label: {
                Text("update_force_open_appstore")
                    .commonButtonStyle()
            }
            .padding(.top, 30)
            .padding(.horizontal, 50.0)
        }
        .padding()
    }
}

fileprivate func openUpdateUrl() {
    if let url = URL(string: UPDATE_URL) {
        if(UIApplication.shared.canOpenURL(url)){
            UIApplication.shared.open(url)
        }
    }
}

struct VersionCheckView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
            .modifier(VersionCheckViewModifier())
        
        AppNeedToBeUpdatedView()
        
        RessetView()
            .previewDisplayName("Reset")
    }
    
    struct RessetView: View {
        @AppStorage("latestKnownVersion") var latestKnownVersion:Int = 0
        
        var body: some View {
            VStack {
                Button {
                    latestKnownVersion = 0
                } label: {
                    Text("resset last viewd")
                }
            }
        }
    }
}
