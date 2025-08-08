//
//  CardStatements.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 5.01.22.
//

import SwiftUI

struct CardStatements: View {
    
    @StateObject var cardStatementsModel: CardStatementViewModel
    @State var showFilter = false
    
    let card: Card
    
    init(card: Card) {
        self.card = card
        self._cardStatementsModel = StateObject(
            wrappedValue: CardStatementViewModel(card: card)
        )
    }
    
    var body: some View {
        let filterStartDate = cardStatementsModel.statementsFilter.startDate.formatToLocalDate()
        let filterEndDate = cardStatementsModel.statementsFilter.endDate.formatToLocalDate()
        ZStack {
            Color("background").edgesIgnoringSafeArea(.all)
            VStack {
                TitleViewWithBack(title: "card_transaction_history_title")
                VStack() {
                    VStack(spacing: 0) {
                        // Filter Header
                        HStack {
                            Text("statements_label").fontWeight(.bold)
                            Text("\(filterStartDate) - \(filterEndDate)")
                            Spacer()
                            Button {
                                showFilter = true
                            } label: {
                                FilledIcon(image: "IconAccountStatementsFilter")
                            }.popover(isPresented: $showFilter) {
                                StatementsFilterView(
                                    model: cardStatementsModel,
                                    isPresented: $showFilter)
                            }
                        }.padding()
                        VStack {
                            if (cardStatementsModel.statements.isEmpty) {
                                Spacer()
                                if (cardStatementsModel.isLoadingStatements) {
                                    ActivityIndicator(label: "card_transaction_history_label_loading")
                                } else {
                                    Text("card_transaction_history_no_items")
                                        .foregroundColor(Color("SecondaryTextColor"))
                                        .font(.system(size: Dimen.TextSize.info))
                                        .multilineTextAlignment(.center)
                                        .padding()
                                }
                                Spacer()
                            } else {
                                ZStack {
                                    DataList(data: cardStatementsModel.statements) { statement in
                                        PaymentListItem(
                                            payment: statement,
                                            infoField: .transactionType
                                        )
                                    }
                                    if (cardStatementsModel.isLoadingStatements) {
                                        ActivityIndicatorOverlay(label: "card_transaction_history_label_loading")
                                    }
                                }
                                
                            }
                        }.padding(0)
                    }
                }
                .background(Color.white)
                .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
                .padding()
                .hiddenNavigationBarStyle()
                .hiddenTabBar()
                .onAppear {
                    cardStatementsModel.fetchStatements()
                }
            }
        }
    }
}

#if DEBUG
struct CardStatements_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            CardStatements(forPreview: true)
        }
        
        VStack {
            ForEach(CardStatement.list) { statement in
                PaymentListItem(
                    payment: statement,
                    infoField: .transactionType
                )
            }
        }
    }
}

extension CardStatements {
    init(forPreview:Bool = true) {
        //self.init()
        self.card = Card.preview1!
        
        self._cardStatementsModel =
            StateObject(
                wrappedValue: CardStatementViewModel(forPreview: true)
            )
    }
}

extension CardStatementViewModel {
   convenience init(forPreview: Bool = true) {
       self.init(card:Card.preview1!)
       
       self.isLoadingStatements = false
       self.statements = CardStatement.list
   }
}
#endif
