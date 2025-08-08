//
//  Contact+ExampleData.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 13.12.22.
//

import Foundation
import Combine

extension Contacts {
    static var example:Contacts {
        let rawData = Data(
        """
        {
            "address": "Алианц Банк България АД,\nСофия 1407,\nул. Сребърна 16",
            "swiftCode": "BUINBGSF",
            "phone": "0700 13 014",
            "email": "support@bank.allianz.bg",
            "website": "https://allianz.bg",
            "socialMedia": [
                {
                    "platformKey": "facebook",
                    "url": "https://www.facebook.com/allianz.bg/"
                },
                {
                    "platformKey": "instagram",
                    "url": "https://www.instagram.com/allianz_bulgaria/"
                },
                {
                    "platformKey": "linkedin",
                    "url": "https://www.linkedin.com/company/allianz-bulgaria-holding/"
                },
                {
                    "platformKey": "youtube",
                    "url": "https://www.youtube.com/channel/UC0AXkF4SMvYoi93wYUfJ6gA"
                }
            ]
        }
        """.utf8)
        
        do {
            let parsedData = try JSONDecoder().decode(Contacts.self, from: rawData)
            return parsedData
        } catch DecodingError.valueNotFound(let type, let context) {
            print("Type '\(type)' mismatch:", context.debugDescription)
            print("codingPath:", context.codingPath)
        } catch DecodingError.typeMismatch(let type, let context) {
            print("Type '\(type)' mismatch:", context.debugDescription)
            print("codingPath:", context.codingPath)
        } catch DecodingError.keyNotFound(let key, let context){
            print("Key '\(key)' not found:", context.debugDescription)
            print("codingPath:", context.codingPath)
        } catch {
            print(error.localizedDescription)
        }
        return Contacts(
            address: "error parsing",
            swiftCode: "error",
            phone: "error",
            email: "error",
            website: "errors",
            socialMedia: [
                SocialMedia(
                    platformKey: "error",
                    url: "error"
                )
            ]
        )
    }
}
        
