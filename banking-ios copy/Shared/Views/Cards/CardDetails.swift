//
//  CardDetails.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 30.12.21.
//

import SwiftUI

struct CardDetails: View {
    let card: Card
    
    let cardStatusColorMap = [
        "active": Color.green,
        "produced_not_received": Color.orange,
        "new": Color.green,
        "blocked": Color.red,
        "deactivated": Color.red,
        "unknown": Color.gray
    ]
    
    private var cardStatusColor: Color {
        var cardStatusColor: Color
        if let color = cardStatusColorMap[card.cardStatus] {
            cardStatusColor = color
        } else {
            cardStatusColor = Color.gray
        }
        return cardStatusColor
    }
    
    var body: some View {
        ZStack {
            Color("background").edgesIgnoringSafeArea(.all)
            VStack {
                TitleViewWithBack(title: "card_details_title")
                ScrollView {
                    VStack(spacing: 20) {
                        VStack(alignment: .leading, spacing: 10) {
                            HStack {
                                Text("cards_label_status")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                Spacer()
                                let cardStatus = "card_status_" + card.cardStatus.lowercased()
                                Text(LocalizedStringKey(cardStatus))
                                    .foregroundColor(cardStatusColor)
                                    .multilineTextAlignment(.trailing)
                            }
                            HStack {
                                Text("cards_label_card_number")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                Spacer()
                                Text(card.cardNumber.toCardNumberFormat())
                                    .multilineTextAlignment(.trailing)
                            }
                            HStack {
                                Text("cards_label_card_holder")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                Spacer()
                                Text(card.cardPrintName)
                                    .multilineTextAlignment(.trailing)
                            }
                            HStack {
                                Text("cards_label_card_owner")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                Spacer()
                                Text(card.cardOwner)
                                    .multilineTextAlignment(.trailing)
                            }
                            HStack {
                                Text("cards_label_valid_thru")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                Spacer()
                                Text(card.expiryDate)
                                    .multilineTextAlignment(.trailing)
                            }
                            HStack {
                                Text("cards_label_linked_to")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                Spacer()
                                Text(card.cardAccountNumber)
                                    .multilineTextAlignment(.trailing)
                            }
                            HStack {
                                Text("cards_label_currency")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                Spacer()
                                Text(card.currency)
                                    .multilineTextAlignment(.trailing)
                            }
                            HStack {
                                Text("cards_label_card_type")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                Spacer()
                                Text(card.cardProductLabel)
                                    .multilineTextAlignment(.trailing)
                            }
                        }
                        .padding()
                        .background(Color(.white))
                        .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
                        VStack(alignment: .leading, spacing: 10) {
                            HStack {
                                Text("cards_label_available_amount")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                Spacer()
                                Text(card.availableBalance.toCurrencyFormatter())
                            }
                            HStack {
                                if(card.cardType == "debit") {
                                    Text("cards_label_approved_overdraft")
                                        .foregroundColor(Color("SecondaryTextColor"))
                                } else  {
                                    Text("cards_label_credit_limit")
                                        .foregroundColor(Color("SecondaryTextColor"))
                                    
                                }
                                    Spacer()
                                    Text(card.approvedOverdraft.toCurrencyFormatter())
                            }
                        }
                        .padding()
                        .background(Color(.white))
                        .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
                    }.padding()
                    
                }
            }
        }
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
    }
        
}

struct CardDetails_Previews: PreviewProvider {
    static var previews: some View {
        CardDetails(card: Card.preview3!)
    }
}
