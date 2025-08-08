//
//  DateUtils.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 16.12.21.
//

import Foundation

extension Date {
    
    func formatTo(format: String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: self)
    }
    
    func formatToLocalDateTime() -> String {
//        let currentLocale = Locale.current.languageCode
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .short
        dateFormatter.timeStyle = .short
        return dateFormatter.string(from: self)
    }
    
    func formatToLocalDate() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .short
        dateFormatter.timeStyle = .none
        return dateFormatter.string(from: self)
    }
    
    func formatToIsoDate() -> String {
        return formatTo(format: "yyyy-MM-dd")
    }

}
