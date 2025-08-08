//
//  Profile.swift
//  Allianz (iOS)
//
//  Created by Dimitar Stoyanov Chukov on 11.03.22.
//

import Foundation

//TODO: Move this structure to the request
struct ProfileModel: Codable {
    
    let result: [Profile]
    
    //TODO: Move this to Preview File
    static var listPreview: [Profile]? {
        let rawData = Data("""
        [
            {
                "userId": "750306",
                "accountOwnerFullName": "Michelangelo di Lodovico Buonarroti Simoni",
                "customerNumber": "640218",
                "currentlySelected": true
            },
            {
                "userId": "850331",
                "accountOwnerFullName": "Johann Sebastian Bach",
                "customerNumber": "500728",
                "currentlySelected": false
            },
            {
                "userId": "850332",
                "accountOwnerFullName": "Madona",
                "customerNumber": "710728",
                "currentlySelected": false
            },
            {
                "userId": "850333",
                "accountOwnerFullName": " ",
                "customerNumber": "720728",
                "currentlySelected": false
            }
        ]
        """.utf8)
        guard let parsedData = try? JSONDecoder().decode([Profile].self, from: rawData) else {
            return nil
        }
        return parsedData
    }
}

struct Profile: Codable, Identifiable {
    let userId: String?
    let accountOwnerFullName: String
    let customerNumber: String
    let currentlySelected: Bool
    
    var id: String { self.customerNumber }
}
