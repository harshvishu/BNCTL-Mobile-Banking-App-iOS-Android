//
//  ATMsView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 24.01.22.
//

import SwiftUI

struct ATMsView: View {
    
    @StateObject var locationViewModel = LocationViewModel()
    @StateObject var atmsViewModel = ATMsViewModel()
   
    var body: some View {
        VStack {
            TitleViewWithBack(title: "offices_and_atms_atms_title")
            ZStack {
                MapView(
                    locations: $atmsViewModel.atms,
                    region: $locationViewModel.lastSeenCoordinateRegion, mapViewType: .atm
                )
                if ($atmsViewModel.isLoading.wrappedValue) {
                    VStack {
                        Spacer()
                        HStack {
                            ActivityIndicator()
                            Text("offices_and_atms_atms_label_loading")
                                .padding()
                        }
                        .frame(maxWidth: .infinity)
                        .background(Color("background").opacity(0.25))
                        Spacer()
                    }
                }
            }
            .hiddenNavigationBarStyle()
            .hiddenTabBar()
            .onReceive(atmsViewModel.$isLoading, perform: { newIsLoading in
                // Request user's location only after all pins have been loaded and added to the map.
                // This avoids animations being interupted when user's location is resolved first and camera is already moving.
                // At this poiint adding pins to the map stops the animation.
                locationViewModel.requestLocation()
            })
            .onAppear {
                atmsViewModel.fetchATMs()
                let locationAuthorizationStatus = locationViewModel.locationAuthorizationStatus()
                if (locationAuthorizationStatus == .notDetermined) {
                    self.locationViewModel.requestPermission()
                }
        }
        }
    }
}

struct ATMsView_Previews: PreviewProvider {
    static var previews: some View {
        ATMsView()
    }
}
