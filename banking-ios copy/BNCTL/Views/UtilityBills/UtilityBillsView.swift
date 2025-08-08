//
//  UtilityBillsView.swift
//  BNCTL
//
//  Created by Prem's on 09/02/23.
//

import SwiftUI

struct UtilityBillsView: View {

    @StateObject var model: UtilityBillsViewModel
    @State var isShowingHistoryView: Bool = false
    @State var isShwoingSummary: Bool = false
    @State var endOfDayAllert: Bool = false

    init(isLocal: Bool = false) {
        _model = StateObject(
            wrappedValue: UtilityBillsViewModel(isLocal)
        )
    }

    var body: some View {
        VStack(spacing: Dimen.Spacing.regular) {
            TitleViewWithBackAndRItem(title: "utility_bills_title") {
                NavigationLink(destination: {
                    UtilityBillsHistoryView()
                }) {
                    Image("IconUtilityHistory")
                }
            }
            if (model.utilityBills.isEmpty) {
                VStack {
                    Spacer()
                    if (model.isLoading) {
                        ActivityIndicator(label: "utility_bills_label_loading")
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
                VStack(spacing: Dimen.Spacing.regular) {
                    HStack {
                        Checkable(state: model.selectAllState()) {
                            model.selectAll()
                        } label: {
                            Text("utility_bills_check_box_label_select_all")
                                .foregroundColor(Color("PrimaryTextColor"))
                                .font(.system(size: Dimen.TextSize.info, weight: .bold, design: .default))
                        }
                    }
                    .padding()
                    .background(Color(.white).cornerRadius(Dimen.CornerRadius.regular))
                    .padding(.horizontal)

                    ScrollView {
                        VStack(spacing: Dimen.Spacing.short) {
                            ForEach(self.model.utilityBills) { utilityBill in
                                let utilityBillIsSelected = model.selected.contains(utilityBill)
                                let checkableState =  // is unpaid - can be checked
                                utilityBillIsSelected ? CheckableState.checked : CheckableState.unchecked

                                ZStack {
                                    Rectangle()
                                        .foregroundColor(.white)
                                        .cornerRadius(Dimen.CornerRadius.regular)
                                    Checkable(state: checkableState) {
                                        model.toggleUtilityBill(utilityBill: utilityBill)
                                    } label: {
                                        Text(utilityBill.name)
                                            .foregroundColor(Color("PrimaryTextColor"))
                                            .font(.system(size: Dimen.TextSize.info, weight: .bold, design: .default))
                                    } content: {
                                        UtilityBillPaymentView(utilityBill: utilityBill)
                                    }
                                    .disabled(utilityBill.billAmount == 0)
                                    .padding()
                                }
                            }
                        }
                        .padding(.horizontal)
                    }

                    Button {
                        Task {
                            if try await model.isEndOfDay() {
                                endOfDayAllert = true
                            } else {
                                isShwoingSummary = true
                            }
                        }
                    } label: {
                        Text("utility_bills_button_pay")
                            .commonButtonStyle()
                            .padding(.horizontal)
                    }
                    .disabled(model.selected.count < 1)
                    .padding(.horizontal)
                    .padding(.bottom)

                    NavigationLink(isActive:$isShwoingSummary) {
                        UtilityBillsPaymentSummary().environmentObject(model)
                    } label: {
                        EmptyView()
                    }
                    .isDetailLink(false)
                }
            }
        }
        .compatibleAllert(
            showAlert: $endOfDayAllert,
            titleKey: LocalizedStringKey("utility_bills_end_of_day"),
            defailtLabel: "error_transfer_alert_close")
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .background(Color("background").edgesIgnoringSafeArea(.all))
        .onAppear {
            model.fetchUtilityBills()
        }
    }
}

struct UtilityBillPaymentView: View {

    @State var utilityBill: UtilityBillPayment

    fileprivate func renderUtilityBillStatus() -> Text {
        guard let status = utilityBill.status else {
            return Text("utility_bills_paid")
                .foregroundColor(Color("GreenColor"))
        }
        switch (status) {
            case "errBillDueAmountRetrieval":
                return Text("error_utility_bills_checking_amount_due")
                    .foregroundColor(Color.red)
            case "errBillsGeneralError":
                return Text("error_utility_bills_checking_general_error")
                    .foregroundColor(Color.red)
            default:
                return Text("utility_bills_paid")
                    .foregroundColor(Color.red)
        }
    }

    var body: some View {
        ZStack{
            VStack{
                if let billerName = utilityBill.biller.name {
                    row(title: "utility_bills_label_provider", data: billerName)
                }
                if let referenceNo = utilityBill.clientReference {
                    row(title: "utility_bills_label_subscription_number", data: referenceNo)
                }
                Spacer(minLength: 12)
                HStack{
                    Spacer()
                    Group {
                        if (utilityBill.billAmount > 0) {
                            Text(utilityBill.billAmount.toCurrencyFormatter())
                            Text(utilityBill.currencyName)
                        } else {
                            Group {
                                renderUtilityBillStatus()
                            }.multilineTextAlignment(.trailing)
                        }
                    }
                    .font(.system(size: 18, weight: .semibold, design: .default))
                    .foregroundColor(Color("PrimaryTextColor"))
                }
            }
            .padding(.top)
        }
    }

    func row(title: LocalizedStringKey, data: String) -> some View {
        return HStack {
            Text(title)
                .font(.system(size: Dimen.TextSize.info, weight: .regular, design: .default))
                .foregroundColor(Color("SecondaryTextColor"))
            Spacer()
            Text(data)
                .font(.system(size: Dimen.TextSize.info, weight: .regular, design: .default))
                .foregroundColor(Color("PrimaryTextColor"))
                .lineLimit(1)
        }
    }
}

struct UtilityBillsView_Previews: PreviewProvider {
    static var previews: some View {
        UtilityBillsView()
    }
}
