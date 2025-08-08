//
//  ExchangeRatesViewModel.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 13.12.21.
//

import Foundation

class ExchangeRatesViewModel: ObservableObject {
    
    private var isPicked = false
    @Published var exchangeRates: [ExchangeRateModel] = []
    @Published var date: Date? = nil {
        didSet {
            fetchExchangeRates()
            if(isPicked == false) {
                isPicked = true
            }
        }
    }
    
    func fetchExchangeRates() {
        ExchangeRateService().fetchExchangeRates(forDate: isPicked ? date : nil ) { error, exchangeRatesResponse in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let exchangeRatesResult = exchangeRatesResponse {
                self.exchangeRates = exchangeRatesResult.sorted(by: {
                    $0.currencyCode < $1.currencyCode
                })
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
            
        }
    }
    
}
