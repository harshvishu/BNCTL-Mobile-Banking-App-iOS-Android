//
//  DocumentsHistoryView.swift
//  Allianz (iOS)
//
//  Created by Prem's on 02/02/22.
//

import SwiftUI

struct DocumentsHistoryView: View {
    
    @ObservedObject var viewModel: DocumentsHistoryViewModel
    @State var showFilter = false
    
    init(_ isLocal:Bool = false) {
        viewModel = DocumentsHistoryViewModel(isLocal)
        UITableView.appearance().separatorStyle = .none
    }
    
    var body: some View {
        let filterStartDate = viewModel.statementsFilter.startDate.formatToLocalDate()
        let filterEndDate = viewModel.statementsFilter.endDate.formatToLocalDate()
        VStack {
            TitleViewWithBack(title: "transfers_history_title")
            VStack(spacing: 0) {
                // Filter Header
                HStack{
                    Text("statements_label").fontWeight(.bold)
                    Text("\(filterStartDate) - \(filterEndDate)")
                    Spacer()
                    Button {
                        showFilter = true
                    } label: {
                        FilledIcon(image: "IconAccountStatementsFilter")
                    }.popover(isPresented: $showFilter) {
                        StatementsFilterView(
                            model: viewModel,
                            isPresented: $showFilter)
                    }
                }.padding()
                VStack {
                    if (viewModel.documentHistoryArray.isEmpty) {
                        Spacer()
                        if (viewModel.isLoadingStatements) {
                            ActivityIndicator(label: "transfers_history_label_loading")
                        } else {
                            Text("transfers_history_no_items")
                                .foregroundColor(Color("SecondaryTextColor"))
                                .font(.system(size: Dimen.TextSize.info))
                                .multilineTextAlignment(.center)
                                .padding()
                        }
                        Spacer()
                    } else {
                        ZStack {
                            DataList(data: viewModel.documentHistoryArray) { documentHistoryItem in
                                PaymentListItem(
                                    payment: documentHistoryItem,
                                    infoField: .beneficiary
                                )
                            }
                            if (viewModel.isLoadingStatements) {
                                ActivityIndicatorOverlay(label: "transfers_history_label_loading")
                            }
                        }
                    }
                }.padding(0)
            }
            .background(Color.white)
            .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
            .padding()
        }
        .background(
            Color("background")
                .edgesIgnoringSafeArea(.all)
        )
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .onAppear {
            viewModel.fetchStatements()
        }
    }
    
    var dateFormater:DateFormatter {
        let df = DateFormatter()
        df.dateFormat = "dd.MMM.yyyy, HH:mm"
        return df
    }
}

struct DocumentsHistoryView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView{
            DocumentsHistoryView(true)
        }
    }
}
