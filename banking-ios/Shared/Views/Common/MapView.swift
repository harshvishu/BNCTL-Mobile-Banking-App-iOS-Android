//
//  MapView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 25.01.22.
//

import SwiftUI
import MapKit

final class LandmarkAnnotation: NSObject, MKAnnotation {
    let id: String
    let title: String?
    let coordinate: CLLocationCoordinate2D
    let subtitle: String?

    init(location: Location) {
        self.id = location.id
        self.title = location.name
        self.coordinate = CLLocationCoordinate2D(
            latitude: location.latitude,
            longitude: location.longitude
        )
        self.subtitle = location.address
    }
}

enum MapViewType: String {
    case office
    case atm
}

struct MapView: UIViewRepresentable {

    @Binding var locations: [Location]
    @Binding var region: MKCoordinateRegion?
    @State var mapViewType: MapViewType?
    
    func makeUIView(context: UIViewRepresentableContext<MapView>) -> MKMapView {
        let mapView = MKMapView()
        mapView.showsUserLocation = true
        mapView.delegate = context.coordinator
        return mapView
    }

    func updateUIView(_ uiView: MKMapView, context: UIViewRepresentableContext<MapView>) {
        if let region = region {
            uiView.setRegion(region, animated: true)
        }
        // TODO: Implement smarter annotations adding/removing
        uiView.removeAnnotations(uiView.annotations)
        uiView.addAnnotations(locations.map {
            LandmarkAnnotation(location: $0)
        })
    }
    
    func makeCoordinator() -> MapView.Coordinator {
        Coordinator(self, self.mapViewType ?? .office)
    }

    final class Coordinator: NSObject, MKMapViewDelegate {
        private let mapView: MapView
        @State var mapViewType: MapViewType?
        
        init (_ mapView: MapView, _ mapViewType: MapViewType) {
            self.mapView = mapView
            self.mapViewType = mapViewType
        }
        
        // TODO: Override one of the delegate methods to change the pin style
        
        
        func mapView(_ mapView: MKMapView, viewFor
                     annotation: MKAnnotation) -> MKAnnotationView?{

            guard !annotation.isKind(of: MKUserLocation.self) else {
               // Make a fast exit if the annotation is the `MKUserLocation`, as it's not an annotation view we wish to customize.
               return nil
            }

            //Custom View for Annotation
            let annotationView = MKAnnotationView(annotation: annotation, reuseIdentifier: "customView")
            annotationView.canShowCallout = true
            //Your custom image icon
            if self.mapViewType == .office{
                annotationView.image = UIImage(named: "IconOfficeLocation")
            }else{
                annotationView.image = UIImage(named: "IconAtmLocation")
            }
            let annotationHeight = annotationView.frame.size.height
            annotationView.centerOffset = CGPointMake(0.0, -annotationHeight/2);
            let detailLabel = UILabel()
            detailLabel.numberOfLines = 0
            detailLabel.font = detailLabel.font.withSize(12)
            detailLabel.text = annotation.subtitle ?? ""
            annotationView.detailCalloutAccessoryView = detailLabel
            return annotationView
        }
        
    }
}

struct MapView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            PreviewWrapper()
        }
    }

    struct PreviewWrapper: View {
        @State var locations: [Location] = [
            Location(id: "1", name: "Община Златица", latitude: 42.71485, longitude: 24.13896, address: "Бургас, жк. Меден рудник, Зона Д, ул. Отец Матей Миткалото 45, Бургас - Меден рудник (5700)"),
            Location(id: "2", name: "Административна сграда - Читалище - Криводол", latitude: 43.36851, longitude: 23.47653, address: "Бургас, жк. Меден рудник, Зона Д, ул. Отец Матей Миткалото 45, Бургас - Меден рудник (5700)"),
            Location(id: "2", name: "Военна база - Военен полигон Ново село", latitude: 42.7136, longitude: 26.62946, address: "Бургас, жк. Меден рудник, Зона Д, ул. Отец Матей Миткалото 45, Бургас - Меден рудник (5700)"),
            Location(id: "4", name: "БЦ Фритьоф Нансен", latitude: 42.684628, longitude: 23.321041, address: "Бургас, жк. Меден рудник, Зона Д, ул. Отец Матей Миткалото 45, Бургас - Меден рудник (5700)")
        ]

        @State var region: MKCoordinateRegion? = MKCoordinateRegion(
            center: CLLocationCoordinate2D(
                latitude: 43.60,
                longitude: 23.30),
            span: MKCoordinateSpan(latitudeDelta: 2, longitudeDelta: 2))

        var body: some View {
            MapView(locations: $locations, region: $region, mapViewType: .office)
        }
    }
}
