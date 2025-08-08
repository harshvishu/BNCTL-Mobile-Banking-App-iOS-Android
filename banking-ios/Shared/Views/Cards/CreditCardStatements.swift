//
//  CreditCardStatements.swift
//  Allianz (iOS)
//
//  Created by Dimitar Stoyanov Chukov on 1.04.22.
//

import SwiftUI

struct CreditCardStatements: View {

    let card: Card
    
    @State var showFilter = false
    @State var isDocumentPickerPresented: Bool = false
    @State var showAlert: Bool = false
    @ObservedObject var creditCardStatementsModel: CreditCardStatementViewModel
    
    @State var alertMessage: String = ""
    
    init(card: Card, _ local: Bool = false) {
        self.card = card
        self.creditCardStatementsModel = CreditCardStatementViewModel(card: card, local)
    }
    
    var body: some View {
        let filterStartDate = creditCardStatementsModel.statementsFilter.startDate.formatToLocalDate()
        let filterEndDate = creditCardStatementsModel.statementsFilter.endDate.formatToLocalDate()
        let cardNumberEnd = String(card.cardNumber.suffix(8))
        VStack {
            TitleViewWithBack(title: "credit_cards_statements_title \(cardNumberEnd)")
            ScrollView {
                VStack() {
                    VStack(spacing: 20) {
                        HStack {
                            Text("statements_filter_label_period")
                                .fontWeight(.bold)
                            Text("\(filterStartDate) - \(filterEndDate)")
                            Spacer()
                            Button {
                                showFilter = true
                            } label: {
                                FilledIcon(image: "IconAccountStatementsFilter")
                            }.popover(isPresented: $showFilter) {
                                StatementsFilterView(
                                    model: creditCardStatementsModel,
                                    isPresented: $showFilter)
                            }
                        }
                        if (creditCardStatementsModel.statements.isEmpty) {
                            Text("credit_cards_statements_label_no_items")
                                .foregroundColor(Color("SecondaryTextColor"))
                                .font(.footnote)
                                .multilineTextAlignment(.center)
                                .padding()
                        } else {
                            ForEach(creditCardStatementsModel.statements, id: \.statementId) { statement in
                                VStack {
                                    HStack {
                                        VStack (alignment: .leading, spacing: 5){
                                            Text(statement.statementId)
                                            Text(statement.date)
                                                .foregroundColor(Color("SecondaryTextColor"))
                                                .font(.subheadline)
                                        }
                                        Spacer()
                                        Button("credit_card_statement_download_button_text") {
                                            creditCardStatementsModel.downloadStatement(fileName: statement.fileName)
                                        }
                                        .sheet(isPresented: self.$isDocumentPickerPresented, content: {
                                            DirectoryPicker() { url in
                                                do {
                                                    if let tempFile = creditCardStatementsModel.downloadManager.tempFile {
                                                        let newDestination = url.appendingPathComponent(tempFile.lastPathComponent)
                                                        if FileManager.default.fileExists(atPath: newDestination.path) {
                                                            self.showAlert = true
                                                            alertMessage = "error_downloading_credit_card_statement_file_already_exists"
                                                        } else {
                                                            try FileManager.default.moveItem(at: tempFile, to: newDestination)
                                                        }
                                                    }
                                                } catch (let error) {
                                                    self.showAlert = true
                                                    self.alertMessage = "error_downloading_credit_card_statement"
                                                    print(error)
                                                }
                                                do {
                                                    creditCardStatementsModel.downloadManager.isDownloaded = false
                                                }
                                            } onCancelCallback: {
                                                do {
                                                    creditCardStatementsModel.downloadManager.isDownloaded = false
                                                }
                                            }
                                        })
                                        .alert(isPresented: $showAlert) {
                                            Alert(title: Text("error_downloading_credit_card_statement_label"), message: Text(LocalizedStringKey(alertMessage)), dismissButton: .cancel(Text("error_downloading_credit_card_statement_label_close")))
                                        }
                                        .onReceive(creditCardStatementsModel.downloadManager.$isDownloaded, perform: { isDownloaded in
                                            self.isDocumentPickerPresented = isDownloaded
                                        })
                                    }
                                    Divider()
                                        .background(Color("TertiaryColor"))
                                }
                            }
                        }
                    }.padding()
                }
                .background(Color.white)
                .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
                .padding()
            }
            .hiddenNavigationBarStyle()
            .hiddenTabBar()
            .background(Color("background"))
            .onAppear {
                creditCardStatementsModel.fetchStatements()
            }
        }
    }

}

struct CreditCardStatements_Previews: PreviewProvider {
    static var previews: some View {
        CreditCardStatements(card: Card.preview1!, true)
    }
}
