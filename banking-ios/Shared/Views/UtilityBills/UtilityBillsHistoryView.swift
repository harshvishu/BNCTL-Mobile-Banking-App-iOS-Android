//
//  UtilityBillsHistory.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 27.02.22.
//

import SwiftUI

struct UtilityBillsHistoryView: View {
    
    @StateObject var model: UtilityHistoryViewModel
    @State var showFilter = false
    
    init(_ isLocal: Bool = false) {
        _model = StateObject(wrappedValue: UtilityHistoryViewModel(isLocal))
    }
    
    var body: some View {
        let filterStartDate = model.statementsFilter.startDate.formatToLocalDate()
        let filterEndDate = model.statementsFilter.endDate.formatToLocalDate()

        VStack {
            TitleViewWithBack(title: "utility_bills_history_title")
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
                            model: model,
                            isPresented: $showFilter
                        )
                    }
                }
                .padding()
                VStack {
                    if (model.utilityHistoryArray.isEmpty) {
                        VStack {
                            Spacer()
                            if (model.isLoading) {
                                ActivityIndicator(label: "utility_bills_history_label_loading")
                            } else {
                                Text("utility_bills_label_no_unpaid_utility_bills")
                                    .foregroundColor(Color("SecondaryTextColor"))
                                    .font(.system(size: Dimen.TextSize.info))
                                    .multilineTextAlignment(.center)
                                    .padding()
                            }
                            Spacer()
                        }
                    } else {
                        ZStack {
                            DataList(data: model.utilityHistoryArray) { utilityHistory in
                                PaymentListItem(
                                    payment: utilityHistory,
                                    infoText: utilityHistory.billerName ?? utilityHistory.provider
                                )
                            }
                            if (model.isLoading) {
                                ActivityIndicatorOverlay(label: "utility_bills_history_label_loading")
                            }
                        }
                    }
                }
            }
            .background(Color(.white).cornerRadius(Dimen.CornerRadius.regular))
            .padding(.horizontal)
        }
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
        .background(Color("background").edgesIgnoringSafeArea(.all))
        .onAppear {
            model.fetchStatements()
        }
    }
}

struct UtilityBillsHistoryView_Previews: PreviewProvider {
    static var previews: some View {
        UtilityBillsHistoryView(true)
    }
}
