//
//  CardRequest.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 21.01.22.
//

import Foundation

struct CardRequest: Codable {
    
    let accountIban: String
    let locationId: String
    let embossName: String
    let cardProductCode: String
    let cardProcutName: String
    let statementOnDemand: Bool
    let statementOnEmail: Bool
    
}
