//
//  CardsDashboard.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 22.11.21.
//

import SwiftUI

struct CardsDashboard: View {
    
    @StateObject var model = CardsViewModel()
    @StateObject var accountPickerViewModel: AccountPickerViewModel = .init()
    
    @State var currentIndex: Int = 0
    @State var isDragging: Bool = false

    @GestureState private var translation: CGFloat = 0
        
    let cardAspectRatio: CGFloat = 8560 / 5398
    let cardPadding: CGFloat = 25
        
    var body: some View {
        NavigationView {
            if (model.cards.isEmpty) {
                ZStack {
                    Color("background").edgesIgnoringSafeArea(.all)
                    VStack {
                        TitleViewWithRItem(title: "nav_cards") {
                        }
                        Spacer()
                        if (model.isLoading) {
                            ActivityIndicator(label: "cards_label_loading")
                                .padding()
                        } else {
                            Text("cards_label_no_cards_registered")
                                .foregroundColor(Color("SecondaryTextColor"))
                                .font(.system(size: Dimen.TextSize.info))
                                .multilineTextAlignment(.center)
                                .padding()
                        }
                        Spacer()
                    }
                }
                .hiddenNavigationBarStyle()
            } else {
                 cardsView
            }
        } // NavigationView
        .onAppear(perform: {           
            model.fetchCards()
        })
        .onDisappear(perform: {
            accountPickerViewModel.clearData()
        })
        .onReceive(model.$cards) { cards in
            currentIndex = 0
        }
    }
    
    private var cardsView: some View {
        GeometryReader { geometry in
            
            let index = CGFloat(currentIndex)
            let totalPages = CGFloat(model.cards.count)

            let width: CGFloat = geometry.size.width - (5 * cardPadding)
            let height: CGFloat = width / cardAspectRatio

            let hStackWidth: CGFloat = totalPages * width
            let hStackOffset: CGFloat = (hStackWidth / 2) - (geometry.size.width / 2) + (2.5 * cardPadding) - index * (width + cardPadding) + index * cardPadding
            
            let currentCard = model.cards[currentIndex]

            ZStack {
                Color("background").edgesIgnoringSafeArea(.all)
                VStack {
                    TitleViewWithRItem(title: currentCard.cardType == "debit" ? "cards_debit_card" : "cards_credit_card") {
                    }
                    ScrollView {
                        VStack(spacing: 0) {

                            // Cards ViewPager dot indicators
                            HStack {
                                ForEach(Array(model.cards.enumerated()), id: \.offset) { cardIndex, _ in
                                    let color = cardIndex == currentIndex ? "PrimaryColor" : "TertiaryColor"
                                    Circle()
                                        .fill(Color(color))
                                        .frame(width: 6, height: 6)
                                }
                            }.padding(.top, 20)
                            
                            // Cards ViewPager content
                            HStack(alignment: .center, spacing: 0) {
                                HStack(spacing: 0) {
                                    ForEach(Array(model.cards.enumerated()), id: \.offset) { cardIndex, card in
                                        CardItem(card: card)
                                            .scaleEffect(cardIndex == currentIndex ? 1 : 0.8, anchor: .center)
                                            .frame(width: width, height: round(height))
                                            .gesture(cardIndex != currentIndex ? TapGesture(count: 1).onEnded {_ in
                                                currentIndex = cardIndex
                                            } : nil)
                                    }
                                }
                            }
                            .padding(.vertical)
                            .padding(.bottom, 20)
                            .offset(x: hStackOffset)
                            .offset(x: self.translation)
                            .animation(.interactiveSpring())
                            .gesture(
                                DragGesture().updating(self.$translation) { value, state, _ in
                                    state = value.translation.width
                                    isDragging = true
                                }.onEnded { value in
                                    isDragging = false
                                    let offset = value.translation.width / geometry.size.width
                                    let newIndex = (CGFloat(currentIndex) - offset).rounded()
                                    currentIndex = min(max(Int(newIndex), 0), self.model.cards.count - 1)
                                }
                            )
                            
                            // Card Info
                            VStack(alignment: .leading) {
                                HStack {
                                    Spacer()
                                    VStack(spacing: 10) {
                                        Text("cards_label_available_amount")
                                        HStack(alignment: .firstTextBaseline, spacing: 2) {
                                            Text(currentCard.availableBalance.toCurrencyFormatter())
                                                .fontWeight(.bold)
                                            Text(currentCard.currency)
                                                .fontWeight(.light)
                                                .foregroundColor(Color(.gray))
                                        }
                                        .font(.system(size: Dimen.TextSize.balanceLarge))
                                        
                                        HStack(spacing: 2) {
                                            if (currentCard.cardType == "credit") {
                                                Text("cards_label_credit_amount")
                                                Text(currentCard.approvedOverdraft.toCurrencyFormatter())
                                            } else {
                                                Text("cards_label_blocked_amount")
                                                Text(currentCard.blockedAmount.toCurrencyFormatter())
                                            }
                                            Text(currentCard.currency)
                                        }
                                        .foregroundColor(Color(.gray))
                                        .font(.footnote)
                                    }.padding()
                                    Spacer()
                                }
                            }
                            .background(Color(.white))
                            .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
                            .padding(.horizontal)
                            
                            // Card Actions
                            VStack(alignment: .leading) {
                                Group {
                                    NavigationLink {
                                        if !isDragging {
                                            CardStatements(card: currentCard)
                                        }
                                    } label: {
                                        Text("card_transaction_history")
                                            .listNavigationWithoutIcon(image: "IconCardHistory")
                                    }
                                    NavigationLink {
                                        CardDetails(card: currentCard)
                                    } label: {
                                        Text("card_action_details")
                                            .listNavigationWithoutIcon(image: "IconCardDetails")
                                    }
                                    if (currentCard.cardType == "credit") {
                                        NavigationLink {
                                            CreditCardStatements(card: currentCard)
                                        } label: {
                                            Text("cards_action_credit_card_statements")
                                                .listNavigationWithoutIcon(image: "IconCardStatements")
                                        }
                                    }
                                }
                            }
                            .padding(20)
                            .background(Color(.white))
                            .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
                            .padding()
                        }.frame(maxWidth: geometry.size.width)
                    }
                } // ScrollView
            } // ZStack
        } // Geometry Reader
        .hiddenNavigationBarStyle()
        .showTabBar()
    }
}

struct CardItem: View {
    var card: Card
    var body: some View {
        ZStack {
            Image(uiImage:
                    UIImage(named: "Card\(card.cardProductCode)") ??
                    UIImage(named: "CardGeneric")!)
                .resizable()
                .aspectRatio(contentMode: .fit)
            VStack(alignment: .leading) {
                Spacer()
                Group {
                    HStack(alignment: .firstTextBaseline){
                        Text(card.cardNumber)
                            .font(.system(size: 20))
                            .fontWeight(.semibold)
                            .tracking(1.5)
                        Spacer()
                    }.padding(.bottom, 10)
                    HStack(alignment: .firstTextBaseline){
                        Text(card.cardPrintName)
                            .font(.system(size: 15))
                            .tracking(1)
                        Spacer()
                    }.padding(.bottom, 15)
                }
                .foregroundColor(.white)
                .padding(.leading, 15)
            }
            .frame(maxWidth: .infinity)
        }
        .cornerRadius(10)
        .opacity(card.cardStatus == "active" ? 1 : 0.15)
    }
    
}

struct CadrsDasboard_Previews: PreviewProvider {
    static var previews: some View {
        CardsDashboard(model: CardsViewModel.preview)
        
        CardItem(card: Card.preview1!)
            .aspectRatio(
                CGSize(width: 8560, height: 5398),
                contentMode: .fit)
            .previewLayout(.sizeThatFits)
            .previewDisplayName("New")
        
        CardItem(card: Card.preview3!)
            .aspectRatio(
                CGSize(width: 8560, height: 5398),
                contentMode: .fit)
            .previewDisplayName("Active")
            .previewLayout(.sizeThatFits)
    }
}
