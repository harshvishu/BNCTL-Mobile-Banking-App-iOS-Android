//
//  DateFormatter.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 5.03.22.
//

import Foundation

extension Date {
    public func toServerString() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        return dateFormatter.string(from: self)
    }
}
