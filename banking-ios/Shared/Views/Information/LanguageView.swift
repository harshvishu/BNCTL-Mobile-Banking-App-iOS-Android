//
//  LanguageView.swift
//  Allianz (iOS)
//
//  Created by Prem's on 10/02/22.
//

import SwiftUI

struct LanguageView: View {
    
    @State var showAlert = false
    @AppStorage("preferedLanguage") var preferedLanguage:String = Language.default.code
    @State var whantToChangeTo:String = ""
    
    var body: some View {
        ZStack{
            Color("background").edgesIgnoringSafeArea(.all)
            ScrollView{
                VStack{
                    ZStack {
                        Rectangle()
                            .foregroundColor(.white)
                            .cornerRadius(15)
                        VStack(alignment: .leading, spacing: 15) {
                            ForEach(Language.allCases) { language in
                                ListButtonItem(
                                    label: LocalizedStringKey(language.name),
                                    image: "Icon\(language.backend.uppercased())language",
                                    rightIcon: preferedLanguage == language.code ? "IconTick" : nil) {
                                    withAnimation {
                                        self.whantToChangeTo = language.code
                                        self.showAlert = true
                                    }
                                }
                            }
                            
                            /*
                            ListButtonItem(label: "language_EN", image: "IconEnglanguage", rightIcon: localizationViewModel.lang == "en" ? "IconTick" : nil) {
                                withAnimation {
                                    self.showAlert = (localizationViewModel.lang == "en") == false
                                }
                            }
                            .alert(isPresented: $showAlert){
                                Alert(title: Text("language_dialog_message"), message: Text(""),
                                      primaryButton: .default (Text("language_dialog_confirm")) {
                                    changeLanguage()
                                },
                                      secondaryButton: .cancel(Text("language_dialog_reject"))
                                )
                            }
                             */
                        }
                        .alert(isPresented: $showAlert){
                            Alert(
                                title: Text("language_dialog_message"),
                                message: Text(""),
                                primaryButton:
                                        .default (Text("language_dialog_confirm"))
                                {
                                preferedLanguage = whantToChangeTo
                            },
                                  secondaryButton: .cancel(Text("language_dialog_reject"))
                            )
                            
                        }
                        .padding()
                    }
                    .padding(.horizontal)
                    .fixedSize(horizontal: false, vertical: true)
                }
            }
        }
        .navigationBarTitle("language_title", displayMode: .inline)
        .hiddenTabBar()
    }
}

struct LanguageView_Previews: PreviewProvider {
    
    static var previews: some View {
        LanguageView()
    }
}
