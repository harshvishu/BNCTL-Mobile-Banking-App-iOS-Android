    //
    //  RequestCardView.swift
    //  Allianz (iOS)
    //
    //  Created by Peter Velchovski on 6.01.22.
    //

import SwiftUI

struct RequestCardView: View {
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
                
    @StateObject var model = RequestCardViewModel()
    
    var body: some View {
        GeometryReader { geometry in
            VStack {
                TitleViewWithBack(title: "new_debit_card_title")
                switch model.cardRequestStatus {
                case .isLoading:
                    RequestCardIsLoading()
                case .success:
                    RequestCardSuccess() {
                        self.presentationMode.wrappedValue.dismiss()
                    }
                case .failre:
                    RequestCardFailure() {
                        self.presentationMode.wrappedValue.dismiss()
                    }
                case .none:
                    CardRequestForm(model: model)
                }
            }
            .frame(minHeight: geometry.size.height)
            .background(
                Color("background")
                    .edgesIgnoringSafeArea(.all)
            )
            .onAppear(perform: {
                model.fetchCardProducts()
                model.fetchBranches()
            })
        }
        .hiddenTabBar()
        .hiddenNavigationBarStyle()
    }
}

fileprivate struct CardRequestForm:View {
    @ObservedObject var model:RequestCardViewModel
    @State var isSelectingLinkedAccount = false
    
    var body: some View {
        ScrollViewReader { proxi in
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    FormSection(title: "new_debit_card_linked_to_title") {
                        AccountPicker(
                            selected: $model.linkedAccount,
                            pickingAccount: $isSelectingLinkedAccount,
                            label: "new_debit_card_linked_to_picker_label",
                            title: "new_debit_card_linked_to_title",
                            filterAccounts: nil,
                            prmisionFor: .newDebitCard,
                            direction: .debit
                        )
                        .indicateError(model.formErrors["linkedAccount"] != nil)
                    }
                    .id("linkedAccount")
                    
                    FormSection(title: "new_debit_card_label_card_type") {
                        RadioGroup<CardProduct>(
                            items: model.cardProducts,
                            selected: $model.cardProduct)
                        .indicateError(model.formErrors["cardProduct"] != nil)
                    }
                    .id("cardProduct")
                    
                    FormSection(title: "new_debit_card_label_name_and_statements") {
                        VStack(alignment: .leading, spacing: 20) {
                            
                            // Card Owner name
                            Text("new_debit_card_label_name_description")
                                .foregroundColor(Color("SecondaryTextColor"))
                                .font(.footnote)
                                .fixedSize(horizontal: false, vertical: true)
                            FloatingLabelTextField(
                                label: Text("new_debit_card_placeholder_emboss_name"),
                                text: $model.embossName,
                                error: model.formErrors["embossName"]
                            )
                            .font(.system(size: 16, weight: .regular, design: .default))
                            .textContentType(.name)
                            .autocapitalization(.none)
                            .disableAutocorrection(true)
                            .id("embossName")
                            
                            // Pick up location
                            ListSelectView(
                                label: "new_debit_card_placeholder_pickup_location",
                                items: model.branches,
                                itemsLocalized: false,
                                selected: $model.pickupBranch)
                            .indicateError(model.formErrors["pickupBranch"] != nil)
                            .id("pickupBranch")

                            Divider()
                                .background(Color("TertiaryColor"))
                            
                            // Statements
                            Text("new_debit_card_label_monthly_statements_location")
                                .foregroundColor(Color("SecondaryTextColor"))
                                .font(.footnote)
                                .fixedSize(horizontal: false, vertical: true)
                            RadioGroup(
                                items: model.receiveStatementsLocations,
                                selected: $model.receiveStatementsAt)
                            .indicateError(model.formErrors["receiveStatementsAt"] != nil)
                            .id("receiveStatementsAt")
                            
                            // Statements email
                            if(model.receiveStatementsAt == model.receiveStatementsEmail) {
                                FloatingLabelTextField(
                                    label: Text("new_debit_card_placeholder_email_for_statements"),
                                    text: $model.email,
                                    error: model.formErrors["email"]
                                )
                                .font(
                                    .system(size: 16, weight: .regular, design: .default)
                                )
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                                .disableAutocorrection(true)
                                .disabled(model.receiveStatementsAt != model.receiveStatementsEmail)
                                .id("email")
                            }
                        }
                    }
                    HStack(alignment: .top) {
                        CheckboxView(isChecked: $model.termsAccepted)
                            .indicateError(model.formErrors["termsAccepted"] != nil)
                        Text("new_debit_card_text_terms")
                            .font(.system(size: 16, weight: .regular))
                            .fontWeight(.regular)
                    }
                    Button {
                        let errors = model.formValidation()
                        
                        if (errors.isEmpty) {
                            model.requestCard()
                        } else {
                            withAnimation {
                                let key = [
                                    "linkedAccount",
                                    "cardProduct",
                                    "embossName",
                                    "pickupBranch",
                                    "receiveStatementsAt",
                                    "email"
                                ].first { key in
                                    return errors[key] != nil
                                }
                                proxi.scrollTo(
                                    key,
                                    anchor: .top
                                )
                                
                            }
                        }
                    } label: {
                        Text("new_debit_card_button_confirm")
                            .commonButtonStyle()
                    }
                    .padding(.horizontal)
                }
                .padding([.horizontal, .bottom])
            }
        } 
        .ignoreKeyboardSafeArea()
    }
}

struct RequestCardIsLoading: View {
    var body: some View {
        ActivityIndicator(label: "new_debit_card_label_processing_request")
    }
}

struct RequestCardSuccess: View {
    let onComplete: () -> Void
    
    var body: some View {
        VStack {
            Spacer()
            VStack {
                Image("IconOperationSuccess")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 72, height: 72)
                    .padding(.bottom, 20)
                Group {
                    Text("new_debit_card_success_title").bold()
                }.multilineTextAlignment(.center)
            }
            Spacer()
            Button {
                onComplete()
            } label: {
                Text("new_debit_card_success_button_ok").commonButtonStyle()
            }.padding(.horizontal)
        }
        .padding()
    }
}

struct RequestCardFailure: View {
    let onComplete: () -> Void
    
    var body: some View {
        VStack {
            Spacer()
            VStack {
                Image("IconOperationRejected")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 72, height: 72)
                    .padding(.bottom, 20)
                Group {
                    Text("new_debit_card_error_unknown").bold()
                }.multilineTextAlignment(.center)
            }
            Spacer()
            Button {
                onComplete()
            } label: {
                    // TODO: Change translation
                Text("new_debit_card_success_button_ok").commonButtonStyle()
            }.padding(.horizontal)
        }
        .padding()
    }
}

struct RequestCardView_Previews: PreviewProvider {
    static var previews: some View {
        RequestCardView()
    }
}
