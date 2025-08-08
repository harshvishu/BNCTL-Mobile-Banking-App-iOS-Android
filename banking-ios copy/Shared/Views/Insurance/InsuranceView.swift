//
//  InsuranceView.swift
//  Allianz (iOS)
//
//  Created by Prem's on 08/12/21.
//

import SwiftUI

struct InsuranceView: View {
    
    @ObservedObject var model: InsurancePaymentViewModel
    
    init (isLocal: Bool = false) {
        model = InsurancePaymentViewModel(isLocal)
    }
    
    var body: some View {
        VStack {
            TitleViewWithBack(title: "insurances_title")
            if (model.insurances.isEmpty) {
                VStack {
                    Spacer()
                    if (model.isLoading) {
                        ActivityIndicator(label: "insurances_label_loading")
                    } else {
                        Text("insurances_label_no_insurances")
                    }
                    Spacer()
                }
            } else {
                VStack {
                    ScrollView {
                        VStack(spacing: Dimen.Spacing.regular) {
                            ForEach(model.insurances) { insuranceObj in
                                VStack {
                                    HStack {
                                        Text(insuranceObj.insurer ?? "")
                                            .frame(alignment: .leading)
                                            .font(.system(size: Dimen.TextSize.info, weight: .bold, design: .default))
                                        Spacer()
                                    }.padding(.bottom)
                                    VStack {
                                        InsuranceDetailsView(insuranceObj: insuranceObj)
                                    }.padding(.bottom)
                                    NavigationLink {
                                        InsurancePayment(insurance: insuranceObj)
                                            .environmentObject(model)
                                    } label: {
                                        Text("insurance_button_pay")
                                            .commonButtonStyle()
                                            .frame(maxWidth: 150)
                                    }
                                    .isDetailLink(false)
                                }
                                .padding()
                                .background(Color.white.cornerRadius(Dimen.CornerRadius.regular))
                                .padding(.horizontal)
                            }
                        }.padding(.bottom)
                    }
                }
            }
        }
        .background(Color("background").edgesIgnoringSafeArea(.all))
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .onAppear {
            model.fetchInsurances()
        }
    }
}

struct InsuranceDetailsView: View {
    
    @State var insuranceObj: InsuranceData?
    
    var body: some View {
        if let amountBGN = insuranceObj?.amountBgn {
            row(label: "insurance_list_item_label_amount_bgn", value: "\(amountBGN) BGN" )
        }
        if let amount = insuranceObj?.amount, insuranceObj?.currency != "BGN" {
            row(label:"Amount \(insuranceObj?.currency ?? "")", value:"\(amount) \(insuranceObj?.currency ?? "")")
        }
        row(label: "insurance_list_item_label_date", value: insuranceObj?.dueDate)
        row(label: "insurance_list_item_label_policy_num", value: insuranceObj?.policy)
        row(label: "insurance_list_item_label_insurer", value: insuranceObj?.insuranceAgencyName)
        row(label: "insurance_list_item_label_account_bgn", value: insuranceObj?.ibanBgn)
        row(label: "Account \(insuranceObj?.currency ?? "")", value: insuranceObj?.iban)
        row(label: "Installment number", value: insuranceObj?.billNumber)
    }
    
    @ViewBuilder
    func row(label:LocalizedStringKey, value:String?) -> some View {
        if let value = value {
            HStack{
                Text(label)
                    .font(.system(size: Dimen.TextSize.info, weight: .regular, design: .default))
                    .foregroundColor(Color("SecondaryTextColor"))
                Spacer()
                Text(value)
                    .font(.system(size: Dimen.TextSize.info, weight: .regular, design: .default))
                    .foregroundColor(Color("PrimaryTextColor"))
            }
        } else {
            EmptyView()
        }
    }
}

struct InsuranceView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            InsuranceView(isLocal: true)
        }
        .environmentObject(AccountsViewModel.preview)
    }
}
