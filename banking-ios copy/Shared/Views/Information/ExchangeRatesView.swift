//
//  ExchangeRatesView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 12.12.21.
//

import SwiftUI

struct ExchangeRatesView: View {
    
    @StateObject var model = ExchangeRatesViewModel()
    @State var currnecy:Currency? = nil

    var body: some View {
        VStack {
            TitleViewWithBackAndRItem(title: "exchange_rates_title") {
                Button {
                    model.fetchExchangeRates()
                } label: {
                    Image(systemName: "arrow.counterclockwise")
                }
                .padding(.trailing)
            }
            ScrollView {
                LazyVStack(pinnedViews: .sectionHeaders) {
                    VStack {
                        HStack {
                            if(model.date == nil) {
                                Text("exchange_rates_label_date")
                            }
                            DateTextField(
                                date: $model.date,
                                in:...Date(),
                                mode: .date,
                                allowEmptyDate: true)
                        }
                        /*
                        DatePicker(
                            selection: $model.date,
                            in: ...Date(),
                            displayedComponents: [.date],
                            label: {
                                Text("exchange_rates_label_date")
                            }
                        )
                         */
                        Divider()
                        HStack {
                            Text("exchange_rates_label_select_currency")
                            Spacer()
                            CurrencyPicker(
                                selected: $currnecy,
                                autoselect: false
                            ) { ex in ex != .BGN }
                        }
                    }
                    .padding()
                    .background(
                        Color.white
                            .cornerRadius(16)
                    )
                    .padding(.horizontal)
                    
                    Section {
                        if (model.exchangeRates.isEmpty) {
                            Spacer()
                            Text("exchange_rates_label_no_exchange_rates_for_given_date")
                                .multilineTextAlignment(.center)
                                .padding()
                            Spacer()
                        } else {
                            ForEach(model.exchangeRates.filter({ exchange in
                                if let currnecy = currnecy {
                                    return currnecy.rawValue == exchange.currencyCode
                                } else {
                                    return true
                                }
                            })) { exchangeRate in
                                VStack {
                                    HStack {
                                        Image("Flag\(exchangeRate.currencyCode)")
                                            .resizable()
                                            .frame(width: 20, height: 20)
                                            .padding(.trailing, 8)
                                        if let fixedRate = exchangeRate.fixedRate {
                                            Text("\(exchangeRate.currencyUnits) \(exchangeRate.currencyCode) - \(fixedRate)")
                                                .fontWeight(.semibold)
                                            Text("exchange_rates_fixed_rate")
                                        } else {
                                            Text("\(exchangeRate.currencyUnits) \(exchangeRate.currencyCode)")
                                                .fontWeight(.semibold)
                                        }
                                        Spacer()
                                    }.padding(.bottom, 8)
                                    rates(
                                        rateLabel: "exchange_rates_label_cash",
                                        buyRateOpt: exchangeRate.cashBuyRate,
                                        sellRateOpt: exchangeRate.cashSellRate)
                                    rates(
                                        rateLabel: "exchange_rates_label_cashless",
                                        buyRateOpt: exchangeRate.cashlessBuyRate,
                                        sellRateOpt: exchangeRate.cashlessSellRate)
                                }
                                .padding()
                                .background(Color.white)
                                .cornerRadius(16)
                                .padding(.horizontal)
                                .padding(.bottom, 16)
                            }
                        }
                    } header: {
                        HStack {
                            Group {
                                Text("exchange_rates_label_currency")
                                Text("exchange_rates_label_buy")
                                Text("exchange_rates_label_sell")
                            }
                            .foregroundColor(Color("SecondaryTextColor"))
                            .frame(
                                maxWidth: .infinity,
                                alignment: Alignment(horizontal: .leading, vertical: .center)
                            )
                        }
                        .padding()
                        .padding(.horizontal)
                        .background(Color("background"))
                        
                    }
                    
                }
            }
        }
        .background(
            Color("background")
                .ignoresSafeArea(.all)
        )
        .hiddenNavigationBarStyle()
        .onAppear {
            model.fetchExchangeRates()
        }
        .hiddenTabBar()
    }
    
    @ViewBuilder func rates(rateLabel: LocalizedStringKey, buyRateOpt: Double?, sellRateOpt: Double?) -> some View {
        if let buyRate = buyRateOpt,
           let sellRate = sellRateOpt
        {
            HStack {
                Group {
                    Text(rateLabel)
                        .foregroundColor(Color.gray)
                    Text(buyRate.toCurrencyFormatter())
                    Text(sellRate.toCurrencyFormatter())
                }
                .frame(
                    maxWidth:.infinity,
                    alignment:Alignment(horizontal: .leading, vertical: .center)
                )
            }
        } else {
            EmptyView()
        }
    }
}

#if DEBUG
struct ExchangeRatesView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            ExchangeRatesView(forPreview: true)
        }
    }
}

extension ExchangeRatesView {
    init(forPreview:Bool = true) {
        self.init()
        
        self._model = StateObject(
            wrappedValue:ExchangeRatesViewModel(forPreview: true)
        )
    }
}

extension ExchangeRatesViewModel{
   convenience init(forPreview: Bool = true) {
       self.init()
       //Hard code your mock data for the preview here
       
       self.exchangeRates = ExchangeRateModel.listPreview
   }
}
#endif
