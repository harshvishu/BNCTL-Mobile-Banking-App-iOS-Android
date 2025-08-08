//
//  CardsViewModel.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 23.12.21.
//

import Foundation

class CardsViewModel: ObservableObject {
    
    @Published var isLoading: Bool = false
    @Published var cards: [Card] = []
    @Published var currentCard: Card? = nil
//    @Published var cards: [Card] = [
//        Card.preview1!,
//        Card.preview2!,
//        Card.preview3!
//    ]
    
    func fetchCards() {
        isLoading = true
        CardsService().fetchCards() { error, cardsResult in
            self.isLoading = false
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let cards = cardsResult {
                self.cards = cards
                if (!cards.isEmpty) {
                    self.currentCard = self.cards[0]
                }
            } else {
                self.cards = []
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
    
    static var preview: CardsViewModel {
        let cardsVm = CardsViewModel()
        cardsVm.cards.append(Card.preview1!)
        cardsVm.cards.append(Card.preview2!)
        cardsVm.cards.append(Card.preview3!)
        return cardsVm
    }
    
}
