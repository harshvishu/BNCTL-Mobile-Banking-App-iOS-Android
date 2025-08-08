//
//  RequestCardProduct.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 20.01.22.
//

import Foundation
import SwiftUI

struct CardProduct: Codable, Identifiable, SelectableItem {
    
    var id: String
    var name: String
    
}
