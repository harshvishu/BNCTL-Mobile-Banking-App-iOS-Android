//
//  DetailsView.swift
//  Allianz (iOS)
//
//  Created by Kavitha Sambandam on 14/10/22.
//

import SwiftUI

struct PaymentDetailsView: View {
    var displayData: PaymentDetails

    var body: some View {
        ZStack {
            Color("background").edgesIgnoringSafeArea(.bottom)
            VStack(spacing: 0) {
                TitleViewWithBack(title: "statements_details_title")
                    .background(Color.white)
                DetailsHeaderView(displayData: displayData)
                ScrollView {
                    VStack(spacing: 0) {
                        Group {
                            if let utilityBill = displayData as? UtilityBillsHistory {
                                DetailRowView(
                                    title: "statements_details_label_provider",
                                    detail: utilityBill.provider,
                                    hasIcon: false
                                )
                            }
                            if let description = displayData.description {
                                DetailRowView(
                                    title: "statements_details_label_reason",
                                    detail: description,
                                    hasIcon: false
                                )
                            }
                            if let additionalDescription = displayData.additionalDescription {
                                if (!additionalDescription.isEmpty) {
                                    DetailRowView(
                                        title: "statements_details_label_additional_reason",
                                        detail: additionalDescription,
                                        hasIcon: false
                                    )
                                }
                            }
                            if let destinationAccount = displayData.destinationAccount {
                                DetailRowView(
                                    title: "statements_details_label_to_account",
                                    detail: destinationAccount,
                                    hasIcon: true
                                )
                            }
                            if let sourceAccount = displayData.sourceAccount {
                                DetailRowView(
                                    title: "statements_details_label_from_account",
                                    detail: sourceAccount,
                                    hasIcon: true
                                )
                            }
                        }
                        .padding(.top, Dimen.Spacing.regular)
                        .padding(.horizontal)
                    }
                }
            }
        }
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
    }
}

struct DetailsHeaderView: View {
    var displayData: PaymentDetails

    var body: some View {
        VStack(spacing: Dimen.Spacing.regular) {
            // Amount and Beneficiary
            VStack(spacing: Dimen.Spacing.tiny) {
                HStack {
                    Spacer()
                    Group {
                        Text(displayData.amount.toCurrencyFormatter(showCurrencyCode: false))
                            .fontWeight(.bold)
                        Text(displayData.currency)
                            .foregroundColor(Color("TertiaryColor"))
                    }
                    .font(.system(size: Dimen.TextSize.amountLarge))
                    Spacer()
                }
                if let beneficiary = displayData.beneficiary {
                    HStack {
                        Group {
                            if(displayData.amount < 0) {
                                Text("statements_details_label_to_beneficiary")
                            } else {
                                Text("statements_details_label_from_beneficiary")
                            }
                        }
                        .foregroundColor(Color("SecondaryTextColor"))
                        Text(beneficiary)
                    }
                }
            }.padding(.top, Dimen.Spacing.regular)
            VStack {
                Text(displayData.transferDate.localeDate())
                    .font(.system(size: Dimen.TextSize.info))
                if (!displayData.status.isEmpty && displayData.status.lowercased() != "completed") {
                    Text(LocalizedStringKey("statements_details_status_label_" + displayData.status))
                        .font(.system(size: Dimen.TextSize.info))
                        .foregroundColor(Color("PrimaryColor"))
                        .padding(.horizontal, 15)
                        .padding(.vertical, 5)
                        .font(.caption)
                        .overlay(
                            RoundedRectangle(cornerRadius: .infinity)
                                .stroke(Color("PrimaryColor"), lineWidth:1)
                        )
                }
            }
        }
        .padding(.bottom, Dimen.Spacing.large)
        .background(Color.white)
    }
}

struct DetailRowView: View {
    let title: String
    let detail: String
    let hasIcon: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: Dimen.Spacing.tiny){
            HStack() {
                Text(LocalizedStringKey(title))
                    .foregroundColor(Color("SecondaryTextColor"))
                    .font(.system(size: Dimen.TextSize.infoSmall))
                Spacer()
            }
            HStack(alignment: .center) {
                if hasIcon {
                    ZStack {
                        Circle()
                            .strokeBorder(Color("SecondaryColor"), lineWidth: 1)
                            .frame(width: 32, height: 32)
                        Image("IconAccount")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 24.0, height: 24.0)
                    }
                }
                Text(detail)
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(10)
    }
}

struct DetailsView_Previews: PreviewProvider {
    static var previews: some View {
        PaymentDetailsView(
            displayData: DocumentsHistoryData.previewList.last!
        )
    }
}
