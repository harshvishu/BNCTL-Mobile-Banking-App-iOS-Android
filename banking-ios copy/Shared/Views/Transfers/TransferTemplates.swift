    //
    //  TransferTemplates.swift
    //  Allianz (iOS)
    //
    //  Created by Evgeniy Raev on 4.12.21.
    //

import SwiftUI

struct TransferTemplates: View {
    
    @EnvironmentObject private var permissionsModel: PermissionsViewModel
    
    @StateObject var model: TransferTemplatesViewModel
    @State private var selectedTemplate:TransferTemplate? = nil
    @State private var showTransferView:Bool = false
    
    init(isLocal: Bool = false) {
        self._model = StateObject(
            wrappedValue: TransferTemplatesViewModel(isLocal: isLocal)
        )
    }

    var body: some View {
        VStack {
            TitleViewWithBack(title: "templates_title")
            if model.templates.isEmpty {
                Spacer()
                if (model.isLoading) {
                    ActivityIndicator(label: "templates_label_loading")
                } else {
                    Text("templates_label_no_data")
                        .foregroundColor(Color("SecondaryTextColor"))
                        .font(.system(size: Dimen.TextSize.info))
                        .multilineTextAlignment(.center)
                        .padding()
                }
                Spacer()
            } else {
                NavigationLink(isActive: $showTransferView){
                    if let selectedTemplate = selectedTemplate, showTransferView {
                        TransferView(template: selectedTemplate)
                    }
                } label: {
                    EmptyView()
                }
                .isDetailLink(false)
                
                ScrollView {
                    LazyVStack(spacing: Dimen.Spacing.short) {
                        ForEach(model.templates) { template in
                            Button {
                                selectedTemplate = template
                                showTransferView = true;
                            } label: {
                                HStack(spacing: 0) {
                                    HStack(spacing: Dimen.Spacing.regular) {
                                        OutlinedIcon(image: "IconTransferTemplates")
                                        Text(template.templateName)
                                            .foregroundColor(Color("PrimaryTextColor"))
                                            .multilineTextAlignment(.leading)
                                            .lineLimit(1)
                                    }
                                    if let amount =  template.amount, let currency = template.destinationAccountCurrency {
                                        Spacer()
                                        HStack(spacing: Dimen.Spacing.tiny) {
                                            Text(amount.toCurrencyFormatter())
                                            Text(currency)
                                        }
                                        .padding(.leading, Dimen.Spacing.regular)
                                        .foregroundColor(Color("PrimaryTextColor"))
                                    }
                                }
                                .padding()
                                .background(Color.white.cornerRadius(Dimen.CornerRadius.regular))
                            }
                        }
                    }
                }.padding(.horizontal)
            }
        }
        .background(
            Color("background").edgesIgnoringSafeArea(.all)
        )
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .onAppear {
            selectedTemplate = nil
            model.getTemplates()
        }
    }
}

struct TransferTemplates_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            TransferTemplates(isLocal: true)
                .environmentObject(PermissionsModel.shared)
        }
    }
}
