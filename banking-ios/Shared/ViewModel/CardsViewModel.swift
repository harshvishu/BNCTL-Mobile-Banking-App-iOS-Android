//
//  CardsViewModel.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 23.12.21.
//

import Foundation
import PassKit
import MeaPushProvisioning
import WatchConnectivity

class WatchSessionDelegate: NSObject, ObservableObject, WCSessionDelegate {
    @Published var isPaired:Bool = false
    @Published var watchSession:WatchSesion = .deactive
    
    func session(
        _ session: WCSession,
        activationDidCompleteWith activationState: WCSessionActivationState,
        error: Error?)
    {
        if activationState == .activated && session.isPaired {
            isPaired = true
            watchSession = .active
            
            
        }
        print("WD active")
    }
    
    func sessionDidBecomeInactive(_ session: WCSession) {
        watchSession = .inactive
        print("WD inactive")
    }
    
    func sessionDidDeactivate(_ session: WCSession) {
        watchSession = .deactive
        print("WD deactive")
    }
    
    enum WatchSesion {
        case active, inactive, deactive
    }
    
    
}

class CardsViewModel: ObservableObject {
    
    @Published var isLoading: Bool = false
    @Published var cards: [Card] = []
    @Published var currentCard: Card? = nil
    
    @Published var cardToTokenization:[String:CardTokenizationData] = [:]
    private let watchDelegate = WatchSessionDelegate()
    
    init() {
        addSubscriptions()
        
        if WCSession.isSupported() { // Check if the iPhone supports Watch Connectivity session handling.
            let session = WCSession.default
            session.delegate = watchDelegate
            session.activate() // Activate the session, asynchronous call to delegate method below.
        }
    }
    
    func addSubscriptions() {
        
        $cardToTokenization
            .debounce(for: 5, scheduler: DispatchQueue.main)
            .filter { data in
                let hasStarted = data.first { (_, card:CardTokenizationData) in
                    card.state == .processStarted
                }
                if hasStarted != nil {
                    print("SCC: has started")
                    return false
                } else {
                    print("SCC: has not")
                }
                
                let has = data.first { (key: String, value: CardTokenizationData) in
                    value.data != nil && value.state != .processStarted
                }
                
                print("SCC: end filter")
                return has != nil
            }
            .map { data in
                let r = data.map { (key: String, value: CardTokenizationData) in
                    return (
                        key,
                        value.deleteData()
                    )
                }
                
                print("SCC: \(r.count)")
                
                return Dictionary(uniqueKeysWithValues: r)
            }
            .assign(to: &$cardToTokenization)
    }
    
    func startTokenization(cardId forTokenization:String) {
        print("SCC: starting the process")
        let cards = self.cardToTokenization.map({ (key: String, value: CardTokenizationData) in
            if (key == forTokenization) {
                return (key, value.moveState(newState: .processStarted))
            } else {
                return (key, value)
            }
        })
        let dict =  Dictionary(
            uniqueKeysWithValues: cards
        )
        
        self.cardToTokenization = dict
    }
    
    func fetchCards(forTokenization:FetchTokanizationCards = .all) {
        isLoading = true
        
        CardsService().fetchCards() { [weak self] error, cardsResult in
            self?.isLoading = false
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let cards = cardsResult {
                self?.cards = cards
                if (cards.isEmpty == false) {
                    self?.currentCard = cards[0]
                    
                    switch forTokenization {
                    case .card(let cardId):
                        let newCard = cards.first { card in
                            return card.cardId == cardId
                        }
                        
                        if let newCard = newCard {
                            self?.prepareForTokenization(card: newCard, state: .processStarted)
                        }
                    case .all:
                        cards.forEach { card in
                            self?.prepareForTokenization(card: card)
                        }
                    case .none:
                        break
                    }
                }
            } else {
                self?.cards = []
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
    
    func prepareForTokenization(card:Card, state:CardTokenizationData.TokenizatioState = .ready) {
        
        let components = card.cardSecret.components(separatedBy: ":")
        if components.first?.count == 4
        {
            return
        }
        
        let isPassLibraryAvailable = PKPassLibrary.isPassLibraryAvailable()
        let canAddPayments = PKAddPaymentPassViewController.canAddPaymentPass()

        if (isPassLibraryAvailable && canAddPayments) {
            
            let cardParams = MppCardDataParameters.init(cardId: card.cardId, cardSecret: card.cardSecret)
            
            MeaPushProvisioning.initializeOemTokenization(cardParams) { [weak self] (responseData, error) in

                if let responseData = responseData,
                   responseData.isValid()
                {
                    // Field primaryAccountIdentifier is always empty for the very first tokenization of the card.
                    var canAddPaymentPassWithPAI = true
                    var canAddPaymentInWatch = false
                    
                    if let primaryAccountIdentifier = responseData.primaryAccountIdentifier,
                       primaryAccountIdentifier.isEmpty == false
                    {
                        canAddPaymentPassWithPAI = MeaPushProvisioning.canAddSecureElementPass(
                            withPrimaryAccountIdentifier: primaryAccountIdentifier
                        )
                        
                        if let isPaired = self?.watchDelegate.isPaired,
                           isPaired
                        {
                            canAddPaymentInWatch = MeaPushProvisioning.remoteSecureElementPassExists (
                                withPrimaryAccountIdentifier: primaryAccountIdentifier
                            ) == false

                            print("WE \(canAddPaymentInWatch)")
                        }
                    }

                    if (canAddPaymentPassWithPAI || canAddPaymentInWatch) {
                        self?.cardToTokenization[card.cardId] = CardTokenizationData(
                            state: state,
                            data: responseData,
                            inWatch: canAddPaymentInWatch
                        )
                    }
                }
            }
        }
    }
    
    struct CardTokenizationData {
        let state:TokenizatioState
        let data:MppInitializeOemTokenizationResponseData?
        let inWatch:Bool
        
        enum TokenizatioState {
            case ready, processStarted, added
        }
        
        func deleteData() -> CardTokenizationData{
            CardTokenizationData(
                state: self.state,
                data: nil,
                inWatch: self.inWatch
            )
        }
        
        func moveState(newState:TokenizatioState) -> CardTokenizationData {
            CardTokenizationData(
                state: newState,
                data: self.data,
                inWatch: self.inWatch
            )
        }
    }
    
    enum FetchTokanizationCards {
        case all
        case none
        case card(String)
    }
    
    static var preview: CardsViewModel {
        let cardsVm = CardsViewModel()
        let list = Card.list
        cardsVm.cards = list
        return cardsVm
    }
}
