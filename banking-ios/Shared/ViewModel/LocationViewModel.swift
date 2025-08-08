//
//  LocationViewModel.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 25.01.22.
//

import Foundation
import CoreLocation
import MapKit

class LocationViewModel: NSObject, ObservableObject, CLLocationManagerDelegate {
    
    @Published var place: CLPlacemark?
    
    @Published var lastSeenLocation: CLLocation?
    @Published var lastSeenCoordinate: CLLocationCoordinate2D?
    @Published var lastSeenCoordinateRegion: MKCoordinateRegion?
    
    private let locationManager: CLLocationManager
    
    override init () {
        locationManager = CLLocationManager()
        
        super.init()
        
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyKilometer
//        locationManager.requestLocation()
    }
    
    func requestLocation() {
        locationManager.requestLocation()
    }
    
    func requestPermission() {
        locationManager.requestWhenInUseAuthorization()
    }
    
    func locationAuthorizationStatus() -> CLAuthorizationStatus {
        var locationAuthorizationStatus : CLAuthorizationStatus
        if #available(iOS 14.0, *) {
            locationAuthorizationStatus =  locationManager.authorizationStatus
        } else {
            locationAuthorizationStatus = CLLocationManager.authorizationStatus()
        }
        return locationAuthorizationStatus
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let location = locations.last {
            lastSeenLocation = location
            lastSeenCoordinate = CLLocationCoordinate2D(
                latitude: location.coordinate.latitude,
                longitude: location.coordinate.longitude
            )
            lastSeenCoordinateRegion = MKCoordinateRegion(
                center: lastSeenCoordinate!,
                span: MKCoordinateSpan(latitudeDelta: 0.25, longitudeDelta: 0.25))
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        Logger.E(tag: APP_NAME, error.localizedDescription)
    }
    
    func fetchCountryAndCity(for location: CLLocation?) {
        guard let location = location else { return }
        let geocoder = CLGeocoder()
        geocoder.reverseGeocodeLocation(location) { (placemarks, error) in
            self.place = placemarks?.first
        }
    }
    
}
