//
//  StatementsFilterView.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 5.01.22.
//

import SwiftUI

struct StatementsFilterView: View {
    
    @Binding var isPresented: Bool
    
    @State private var model: StatementsFilterable
        
    @State private var draft: StatementsFilter
    
    init(model: StatementsFilterable, isPresented: Binding<Bool>) {
        _isPresented = isPresented
        _model = State(wrappedValue: model)
        _draft = State(wrappedValue: model.statementsFilter)
    }
    
    var body: some View {
        GeometryReader { geometry in
            ScrollView {
                VStack {
                    TitleViewWithRItem(title: "statements_filter_title") {
                        Button {
                            isPresented = false;
                        } label: {
                            Image("IconClose")
                        }
                    }

//                    ZStack {
//                        HStack {
//                            Spacer()
//                            Text("statements_filter_title")
//                                .foregroundColor(Color("PrimaryColor"))
//                                .bold()
//                            Spacer()
//                        }
//                        HStack {
//                            Button {
//                                isPresented = false;
//                            } label: {
//                                Image("IconClose")
//                            }
//                            Spacer()
//                        }
//                    }.padding(10)
                    VStack(alignment: .leading) {
                        Text("statements_filter_label_period")
                            .fontWeight(.bold)
                            .font(.callout)
                        VStack {
                            VStack{
                                HStack {
                                    Text("statements_filter_label_start_date")
                                    Spacer()
                                    DateTextField(date: $draft.selStartDate, in: ...Date(), mode: .date)
                                        .padding(5)
                                        .fixedSize()
                                    /* DatePicker("",
                                        selection: $draft.startDate,
                                        in: ...Date(),
                                        displayedComponents: [.date]
                                    ).padding(.bottom, 5)
                                        .labelsHidden() */
                                }
                                Rectangle()
                                    .frame(maxHeight: 1)
                                    .foregroundColor(Color("TertiaryColor"))
                                HStack {
                                    Text("statements_filter_label_end_date")
                                    Spacer()
                                    let sDate = draft.selStartDate ?? Date()
                                    DateTextField(date: $draft.selEndDate, in: sDate...Date(), mode: .date)
                                        .padding(5)
                                        .fixedSize()
                                    /* DatePicker("",
                                        selection: $draft.endDate,
                                        in: draft.startDate...Date(),
                                        displayedComponents: [.date]
                                    ).padding(.top, 5)
                                        .labelsHidden() */
                                }
                            }
                            .padding()
                        }.background(Color.white)
                            .mask(RoundedRectangle(cornerRadius: Dimen.CornerRadius.regular))
                        Spacer()
                        Button {
                            if( model.statementsFilter != self.draft) {
                                self.model.statementsFilter = self.draft
                                self.model.fetchStatements()
                            }
                            self.isPresented = false
                            
                        } label: {
                            Text("statements_filter_button_filter")
                                .commonButtonStyle()
                                
                        }
                    }.padding()
                        
                }.frame(minHeight: geometry.size.height)
            }.background(Color("background"))
        }
    }
}

extension StatementsFilter:Equatable {
    static func == (lhs: StatementsFilter, rhs: StatementsFilter) -> Bool {
        return lhs.startDate == rhs.startDate
                && lhs.endDate == rhs.endDate
    }
}

struct StatementsFilterView_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper: View {
        @State var isPresented = true
        let model = AccountViewModel(account: Account.preview!)

        var body: some View {
            Button {
                isPresented = true
            } label: {
                Text("Statements filter")
            }.sheet(isPresented: $isPresented) {
                StatementsFilterView(model: model, isPresented: $isPresented)
            }

        }
    }
}
