    //
    //  TransferView.swift
    //  laboratory0 (iOS)
    //
    //  Created by Evgeniy Raev on 6.10.21.
    //

import SwiftUI
import Combine

protocol AccountDetailsProtocol {
    var name: String { get set }
    var account: String { get set }
    var currency: String { get set }
}

struct ManualAccountDetails:AccountDetailsProtocol {
    var name: String
    var account: String
    var currency: String
}

extension Account: AccountDetailsProtocol {
    var account: String {
        get {
            self.accountNumber
        }
        set { }
    }
    var name: String {
        get {
            self.beneficiary.name
        }
        set { }
    }
    
    var currency: String {
        get {
            self.currencyName
        }
        set { }
    }
}

struct TransferView: View {
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    //@EnvironmentObject var accountsModel: AccountsViewModel
    
    @StateObject var transferModel: TransferViewModel
    
    @State var isShownValidationScreen: Bool = false
    @State var transfer: Transfer?
    @State var showAllert:Bool = false
    @EnvironmentObject var transfersDashboard: TransferDashboardViewModel
    
    let autoselectSource:AccountPicker.Autoselect
    let autoselectDestination:AccountPicker.Autoselect
    
    init(transferType: TransferType) {
        self._transferModel = StateObject(
            wrappedValue: TransferViewModel(type: transferType)
        )
        
        autoselectSource = .auto
        autoselectDestination = .auto
    }
    
    init(template: TransferTemplate) {
        self._transferModel = StateObject(
            wrappedValue: TransferViewModel(template: template)
        )
        
        if let sourceAccount = template.sourceAccount {
            autoselectSource = .iban(sourceAccount)
        } else {
            autoselectSource = .auto
        }
        if let destinationAccount = template.destinationAccount {
            autoselectDestination = .iban(destinationAccount)
        } else {
            autoselectDestination = .auto
        }
        
    }
    
    func getTitle() -> LocalizedStringKey {
        switch transferModel.transferType {
        case .intrabank:
            return "transfer_type_intrabank"
        case .interbank:
            return "transfer_type_interbank"
        case .betweenacc:
            return "transfer_type_betweenacc"
        case .international:
            return "transfer_type_international"
        case .utilityBills:
            return "transfer_type_utility_bills"
        case .insirance:
            return "transfer_type_insirance"
        case .currencyExchange:
            return "transfer_type_currency_exchange"
        }
    }
    
    func getProperCurrency() -> String {
        var currencyText: String = transferModel.sourceAccount?.currency ?? "BGN"
        if (transferModel.transferType == .currencyExchange) {
            if(transferModel.orderType == .buy) {
                currencyText = transferModel.destinationAccount?.currency ?? "BGN"
            }
        }
        return currencyText
    }
    
    var body: some View {
        VStack {
            TitleViewWithBackAndRItem(title: getTitle()) {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconClose")
                }
            }
            
            ScrollViewReader { proxi in
                ScrollView {
                    VStack(spacing: Dimen.Spacing.regular) {
                        TransferFormSection(title: "transfer_form_label_from") {
                            AccountPicker(
                                selected: $transferModel.sourceAccount,
                                pickingAccount: $transferModel.selectingSourceAccount,
                                label: "transfer_select_account_list_label_text",
                                title: "transfer_form_label_from",
                                filterAccounts: filter(
                                    transferType: transferModel.transferType,
                                    sourceAccount: transferModel.destinationAccount
                                ),
                                prmisionFor: transferModel.transferType.premisionCode,
                                direction: .debit
                            )
                            .autoSelect(autoselectSource)
                            .indicateError((transferModel.formErrors["sourceAccount"] == nil) == false)
                        }
                        .id("sourceAccount")
                        
                        
                        TransferFormSection(title: "transfer_form_label_to") {
                            switch transferModel.transferType {
                            case .betweenacc, .currencyExchange:
                                AccountPicker(
                                    selected: $transferModel.destinationAccount,
                                    pickingAccount: $transferModel.selectingDestinationAccount,
                                    label: "transfer_select_account_list_label_text",
                                    title: "transfer_form_label_to",
                                    filterAccounts: filter(
                                        transferType: transferModel.transferType,
                                        sourceAccount: transferModel.sourceAccount
                                    ),
                                    prmisionFor: transferModel.transferType.premisionCode,
                                    direction: .credit
                                )
                                .autoSelect(autoselectDestination)
                                .indicateError((transferModel.formErrors["destinationAccount"] == nil) == false)
                                .id("destinationAccount")
                                
                            default:
                                NavigationLink(isActive: $transferModel.selectingDestinationAccount) {
                                    EmptyView()
                                } label: {
                                    EmptyView()
                                }
                                Group {
                                    FloatingLabelLimitTextField(
                                        label: Text("transfer_form_placeholder_beneficiary_name"),
                                        text: $transferModel.beneficiary,
                                        error: transferModel.formErrors["beneficiary"],
                                        limit: 36
                                    )
                                    .id("beneficiary")
                                    
                                    let ibanCased = Binding<String>(get: {
                                        transferModel.iban
                                    }, set: {
                                        transferModel.iban = $0.uppercased()
                                    })
                                    FloatingLabelTextField(
                                        label: Text("transfer_form_placeholder_iban"),
                                        text: ibanCased,
                                        error: transferModel.formErrors["iban"]
                                    )
                                    .keyboardType(.asciiCapable)
                                }
                                .padding(.vertical)
                            }
                        }
                        
                        details
                        
                        Button {
                            if transferModel.formValidation() {
                                transferModel.formErrors["amount"] = nil
                                // TODO: FIXME: Need a loading indicator 
                                if let tr = try? transferModel.getTransfer() {
                                    Task {
                                        do {
                                            let transferResponse = try await transfersDashboard.validateTransfer(transfer: tr)
                                            
                                            switch transferResponse.status {
                                            case "insufficientFunds":
                                                transferModel.formErrors["amount"] = "error_transfer_validation_insufficient_funds"
                                            default:
                                                transfer = tr
                                                isShownValidationScreen = true
                                            }
                                        } catch {
                                            transferModel.formErrors["alert"] = "error_something_went_wrong"
                                        }
                                    }
                                } else {
                                    transfer = nil
                                    isShownValidationScreen = false
                                }
                            } else {
                                withAnimation {
                                    let key = [
                                        "sourceAccount",
                                        "destinationAccount",
                                        "beneficiary",
                                        "amount",
                                        "preferentialRatePin",
                                        "additimnalReason"
                                    ].first { key in
                                        return transferModel.formErrors[key] != nil
                                    }
                                    proxi.scrollTo(
                                        key,
                                        anchor: .top
                                    )
                                }
                            }
                        } label: {
                            Text("common_button_next").commonButtonStyle()
                        }.padding(.horizontal)
                        
                        NavigationLink(isActive: $isShownValidationScreen) {
                            if let transfer = transfer {
                                TransferSummary(transfer: transfer)
                                    //.environmentObject(accountsModel)
                            }
                        } label: {
                            EmptyView()
                        }
                    }.padding(.horizontal)
                }
            }
            .ignoreKeyboardSafeArea()
        }
        .background(
            Color("background")
            .edgesIgnoringSafeArea(.all)
        )
        .onReceive(transferModel.$formErrors, perform: { errors in
            if let _ = errors["alert"] {
                showAllert = true
            } else if let _ = errors["confirm"] {
                showAllert = true
            }
        })
        .compatibleAllert(
            showAlert: $showAllert,
            titleKey: LocalizedStringKey(transferModel.formErrors["alert"]
                                         ??
                                         transferModel.formErrors["confirm"]
                                         ?? "error_something_went_wrong"),
            defailtLabel: transferModel.formErrors["confirm"] == nil ? "error_transfer_alert_close" : "error_transfer_alert_ok",
            cancelLabel: transferModel.formErrors["confirm"] == nil ? nil : "error_transfer_alert_cancel",
            defaultAction: transferModel.formErrors["confirm"] == nil ? nil : {
                //TODO: I know is hardcodded byt what else you gonna confirm?
                if let _ = transfer {
                    isShownValidationScreen = true
                }
                transferModel.formErrors["confirm"] = nil
            }
        )
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
    }
    
    var details:some View {
        
        TransferFormSection(title: "transfer_form_label_details") {
            VStack {
                switch transferModel.transferType {
                case .intrabank:
                    HStack(alignment: .bottom) {
                        FloatingLabelAmountTextField(
                            label: Text("transfer_form_placeholder_amount"),
                            value: $transferModel.amount,
                            error: transferModel.formErrors["amount"])
                        .id("amount")
                        VStack {
                            CurrencyPicker(selected: $transferModel.currency)
                                .padding(.leading)
                            Divider()
                                .background(Color("TertiaryColor"))
                        }
                        .fixedSize()
                    }
                default:
                    FloatingLabelCurrencyTextField(
                        label: Text("transfer_form_placeholder_amount"),
                        value: $transferModel.amount, currency: getProperCurrency(),
                        error: transferModel.formErrors["amount"]
                    )
                }
            }
            .padding(.vertical)
            if (transferModel.transferType == .currencyExchange) {
                FloatingLabelTextField(
                    label: Text("currency_exchange_form_placeholder_preferential_rates_pin"),
                    text: $transferModel.preferentialRatePin,
                    error: transferModel.formErrors["preferentialRatePin"]
                )
                .id("preferentialRatePin")

            } else {
                FloatingLabelLimitTextField(
                    label: Text("transfer_form_placeholder_reason"),
                    text: $transferModel.reason,
                    error: transferModel.formErrors["reason"],
                    limit: 36
                ).padding(.vertical)
                
                if (transferModel.transferType != .betweenacc) {
                    FloatingLabelLimitTextField(
                        label: Text("transfer_form_placeholder_additional_reason"),
                        text: $transferModel.additionalReason,
                        error: transferModel.formErrors["additimnalReason"],
                        limit: 36
                    ).padding(.vertical)
                        .id("additimnalReason")
                }
            }
            
            switch transferModel.transferType {
            case .interbank:
                VStack(alignment: .leading) {
                    Text("transfer_form_label_settlement_type")
                        .foregroundColor(Color("SecondaryTextColor"))
                        .font(.system(size: Dimen.TextSize.sectionLabel, weight: .regular, design: .default))
                    VStack(alignment: .leading, spacing: 30) {
                        if(transferModel.requiredTransactionType == .rings) {
                            HStack {
                                Text("transfers_note_over_standard_limit")
                                    .animation(.default)
                                    .transition(.move(edge: .top))
                            }
                            .clipped()
                        }
                        Button {
                            transferModel.transactionType = .standard
                        } label: {
                            HStack {
                                Image(transferModel.transactionType == .standard ? "RadioButtonSelected" : "RadioButton")
                                
                                Text("transfers_label_type_standard")
                                    .foregroundColor(
                                        transferModel.requiredTransactionType == .rings ?
                                        Color(.gray) :
                                        Color("PrimaryTextColor")
                                    )
                            }
                        }
                        .disabled(transferModel.requiredTransactionType == .rings)
                        
                        Button {
                            transferModel.transactionType = .rings
                        } label: {
                            HStack {
                                Image(transferModel.transactionType == .rings ? "RadioButtonSelected" : "RadioButton")
                                
                                Text("transfers_label_type_rings")
                                    .foregroundColor(
                                        transferModel.requiredTransactionType == .standard ?
                                        Color(.gray) :
                                        Color("PrimaryTextColor")
                                    )
                            }
                        }
                        .disabled(transferModel.requiredTransactionType == .standard)
                    }
                    .padding(.top, 5)
                    .padding(.bottom, 30)
                }
            case .currencyExchange:
                VStack(alignment: .leading) {
                    Text("currency_exchange_form_label_operation_type")
                        .foregroundColor(Color("SecondaryTextColor"))
                        .font(.system(size: Dimen.TextSize.sectionLabel, weight: .regular, design: .default))
                    HStack() {
                        Button {
                            transferModel.orderType = .sell
                        } label: {
                            HStack {
                                Image(transferModel.orderType == OrderType.sell ? "RadioButtonSelected" : "RadioButton")
                                Text("currency_exchange_label_type_sell")
                                    .foregroundColor(Color("PrimaryTextColor"))
                            }
                        }
                        Spacer()
                        Button {
                            transferModel.orderType = .buy
                        } label: {
                            HStack {
                                Image(transferModel.orderType == OrderType.buy ? "RadioButtonSelected" : "RadioButton")
                                Text("currency_exchange_label_type_buy")
                                    .foregroundColor(Color("PrimaryTextColor"))
                            }
                        }
                        Spacer()
                    }.padding()
                }
        default:
            EmptyView()
            }
        }
        .animation(.default)
        .transition(.move(edge: .top))
    }
    
    func filter(transferType:TransferType, sourceAccount:AccountDetailsProtocol?) -> ((Account) -> Bool) {
        if let sourceAccount = sourceAccount {
            return { account in
                switch transferType {
                case .betweenacc:
                    return account.iban != sourceAccount.account
                case .currencyExchange:
                    return account.currencyName != sourceAccount.currency
                default:
                    return true
                }
            }
        } else {
            return { _ in true }
        }
    }
}

struct TransferFormSection<Content:View>: View {
    let title: LocalizedStringKey
    let content: Content
    
    init(title: LocalizedStringKey, @ViewBuilder content:() -> Content) {
        self.title = title
        self.content = content()
    }
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(title)
                .padding(.horizontal)
                .font(.footnote)
                .foregroundColor(.gray)
            VStack(alignment:.leading){
                content
            }
            .padding()
            .frame(maxWidth:.infinity, minHeight: 64, alignment: .leading)
            .background(Color.white.cornerRadius(16))
        }
    }
}

struct TransferView_Previews: PreviewProvider {
    static var previews: some View {
        ForEach(TransferType.allCases) { type in
            TransferView(transferType: type)
                .previewDisplayName(
                    type.rawValue.capitalized
                )
        }
        
        PreviewWrapper()
    }
    
    struct PreviewWrapper: View {
        @State var isPresenting = false
        @State var type:TransferType = .intrabank
        
        var body: some View {
            NavigationView {
                VStack {
                    NavigationLink() {
                        TransferView(transferType: TransferType.betweenacc)
                            .navigationBarTitle(
                                LocalizedStringKey(
                                    stringLiteral: "transfer.title.\(type.rawValue)"),
                                displayMode: .inline)
                    } label: {
                        Text("Between Account")
                    }
                    NavigationLink() {
                        TransferView(transferType: TransferType.intrabank)
                            .navigationBarTitle(
                                LocalizedStringKey(
                                    stringLiteral: "transfer.title.\(type.rawValue)"),
                                displayMode: .inline)
                    } label: {
                        Text("Intrabank")
                    }
                    NavigationLink() {
                        TransferView(transferType: TransferType.interbank)
                            .navigationBarTitle(
                                LocalizedStringKey(
                                    stringLiteral: "transfer.title.\(type.rawValue)"),
                                displayMode: .inline)
                    } label: {
                        Text("Interbank")
                    }
                    NavigationLink() {
                        TransferView(transferType: .currencyExchange)
                            .navigationBarTitle(
                                LocalizedStringKey(
                                    stringLiteral: "transfer.title.\(type.rawValue)"),
                                displayMode: .inline)
                    } label: {
                        Text("Currency Exchange")
                    }
                }
            }
            .environmentObject(AccountsViewModel.preview)
        }
    }
}

extension TransferType:CaseIterable {
    static var allCases: [TransferType] {
        return [
            .intrabank,
            .interbank,
            .betweenacc,
            .international,
            .utilityBills,
            .insirance,
            .currencyExchange
        ]
    }
    
    
}
