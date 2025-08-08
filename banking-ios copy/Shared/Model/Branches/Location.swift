//
//  Location.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 26.01.22.
//

import Foundation

struct Location: Codable, Identifiable {

    var id: String
    var name: String
    var latitude: Double
    var longitude: Double
    var address: String?
}
