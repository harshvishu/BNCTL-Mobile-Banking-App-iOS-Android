    //
    //  BranchesView.swift
    //  Allianz (iOS)
    //
    //  Created by Peter Velchovski on 24.01.22.
    //

import SwiftUI
import MapKit

struct BranchesView: View {
    
    @StateObject var locationViewModel = LocationViewModel()
    @StateObject var branchesViewModel = BranchesViewModel()
    
    var body: some View {
        VStack {
            TitleViewWithBack(title: "offices_and_atms_offices_title")
            ZStack {
                MapView(
                    locations: $branchesViewModel.branches,
                    region: $locationViewModel.lastSeenCoordinateRegion, mapViewType: .office
                )
                if ($branchesViewModel.isLoading.wrappedValue) {
                    VStack {
                        Spacer()
                        HStack {
                            ActivityIndicator()
                            Text("offices_and_atms_offices_label_loading")
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
            .onReceive(branchesViewModel.$isLoading, perform: { newIsLoading in
                    // Request user's location only after all pins have been loaded and added to the map.
                    // This avoids animations being interupted when user's location is resolved first and camera is already moving.
                    // At this poiint adding pins to the map stops the animation.
                locationViewModel.requestLocation()
            })
            .onAppear {
                branchesViewModel.fetchBranches()
                let locationAuthorizationStatus = locationViewModel.locationAuthorizationStatus()
                if (locationAuthorizationStatus == .notDetermined) {
                    self.locationViewModel.requestPermission()
                }
            }
        }
    }
}

struct BranchesView_Previews: PreviewProvider {
    static var previews: some View {
        BranchesView()
    }
}
