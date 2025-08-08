//
//  Dimen.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 18.02.22.
//

import SwiftUI

class Dimen {
    
    class Spacing {
        static let tiny: CGFloat = 5
        static let short: CGFloat = 10
        static let regular: CGFloat = 15
        static let large: CGFloat = 20
        static let huge: CGFloat = 40
        static let titleItem: CGFloat = 25 // Used for offsetting title and TitleView left and right items
    }
    
    class CornerRadius {
        static let regular: CGFloat = 15
    }
    
    class TextSize {
        static let sectionLabel: CGFloat = 12
        static let title: CGFloat = 20
        static let info: CGFloat = 16
        static let infoSmall: CGFloat = 14
        static let amountInfo: CGFloat = 20 // Used in lists of actionable operations
        static let amountSummary: CGFloat = 24
        static let amountLarge: CGFloat = 40 // Used in Payment Details view
        static let balanceLarge: CGFloat = 28 // Used in Dashboard and Cards balance display
        static let currencyLarge: CGFloat = 24 // Used in Dashboard and Cards balance display
    }
    
    class TitleView {
        static let height: CGFloat = 44
        static let paddingHorizontal: CGFloat = 5
        static let paddingVertical: CGFloat = 20
    }
    
}

