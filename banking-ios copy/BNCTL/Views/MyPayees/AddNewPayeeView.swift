//
//  AddNewPayeeView.swift
//  BNCTL
//
//  Created by Rahul B on 22/02/23.
//

import SwiftUI

struct AddNewPayeeView: View {
    @State var accountType:String = ""
    @State var payeeName:String = ""
    @State var payeeEmail:String = ""
    @State var payeeAdress:String = ""
    @State var payeeCity:String = ""
    @State var payeeCountry:String = ""
    @State var iban:String = ""
    @State var payeeCurrency:String = ""
    @State var bank:String = ""

    var body: some View {
        VStack(alignment:.leading, spacing: 16) {
            TitleViewWithBack(title: "add_new_payee_title")
                .padding(-40)
            ScrollView {
                Group {
                    let accountTypeLabel = "add_account_type_title"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral: accountTypeLabel)),
                        text: $accountType)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.numberPad)

                    let payeeNameLabel = "add_payee_name_title"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral: payeeNameLabel)),
                        text: $payeeName)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.default)

                    let payeeEmailLabel = "add_payee_email_title"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral: payeeEmailLabel)),
                        text: $payeeEmail)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.default)

                    let payeeAdressLabel = "add_payee_address_title"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral: payeeAdressLabel)),
                        text: $payeeAdress)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.default)

                    let payeeCityLabel = "add_payee_city_title"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral: payeeCityLabel)),
                        text: $payeeCity)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.default)

                    let payeeCountryLabel = "add_payee_country_title"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral: payeeCountryLabel)),
                        text: $payeeCountry)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.default)

                    let ibanLabel = "add_iban_title"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral: ibanLabel)),
                        text: $iban)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.default)

                    let payeeCurrencyLabel = "add_payee_currency_title"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral: payeeCurrencyLabel)),
                        text: $payeeCurrency)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.default)

                    let bankLabel = "add_bank_title"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral: bankLabel)),
                        text: $bank)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.default)

                }
                .padding(.top, 20)
                .padding(.bottom, 5)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                Spacer()
                Button {
                } label: {
                    Text(
                        LocalizedStringKey(stringLiteral: "common_button_next")
                    )
                    .commonButtonStyle()
                }
                .hiddenNavigationBarStyle()
                .hiddenTabBar()
            }
        }
        .padding(40)
    }
}

struct AddNewPayeeView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            AddNewPayeeView()
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}
