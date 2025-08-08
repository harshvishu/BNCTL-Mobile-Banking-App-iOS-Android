//
//  PaymentListItem.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 16.10.22.
//

import SwiftUI

struct PaymentListItem: View {
    
    let payment: PaymentDetails
    var infoField: PaymentListItemInfoField? = nil
    var infoText: String? = nil
    
    var infoTextToDisplay: String {
        get {
            if let infoField = self.infoField {
                switch infoField {
                case .description:
                    return payment.description ?? ""
                case .transactionType:
                    // Fallback to a field, that's always there
                    return payment.transactionType ?? ""
                case .beneficiary:
                    // Fallback to a field, that's always there
                    return payment.beneficiary ?? ""
                }
            } else {
                return infoText!
            }
        }
    }
    
    // Initialize and set
    init(
        payment: PaymentDetails,
        infoField: PaymentListItemInfoField = .description
    ) {
        self.payment = payment
        self.infoField = infoField
    }
    
    init(
        payment: PaymentDetails,
        infoText: String
    ) {
        self.payment = payment
        self.infoText = infoText
    }
    
    var body: some View {
        NavigationLink {
            PaymentDetailsView(displayData: payment)
        } label: {
            HStack {
                VStack (alignment: .leading, spacing: 5){
                    Text(infoTextToDisplay)
                        .foregroundColor(Color("PrimaryTextColor"))
                        .multilineTextAlignment(.leading)
                    Text(payment.transferDate.localeDate())
                        .foregroundColor(Color("SecondaryTextColor"))
                        .font(.subheadline)
                }
                Spacer(minLength: Dimen.Spacing.regular)
                Text(payment.amount.toCurrencyFormatter(showCurrencyCode: true, currencyCode: payment.currency))
                    .foregroundColor(payment.amount < 0 ? Color.red : Color.green)
            }
        }
    }
    
    enum PaymentListItemInfoField: String, Codable {
        case description
        case beneficiary
        case transactionType
    }
}

struct PaymentListItem_Previews: PreviewProvider {
    static var previews: some View {
        PaymentListItem(payment: DocumentsHistoryData.previewList.last!)
    }
}
