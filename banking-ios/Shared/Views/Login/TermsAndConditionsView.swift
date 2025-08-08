//
//  TermsAndConditionsView.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 8.12.22.
//

import SwiftUI

struct TermsAndConditionsView: View {
    
    let action:() -> ()
    @State var isChecked:CheckableState = .unchecked
    @State var needsToAgreeError:Bool = false
    @Environment(\.locale) var locale
    
    var body: some View {
        GeometryReader { proxi in
            ScrollView {
                VStack {
                    Text("temrms_text")
                        .padding(.bottom)
                    
                    Spacer()
                    
                    /*
                    Checkable(state: isChecked) {
                        if(isChecked == .checked) {
                            isChecked = .unchecked
                        } else {
                            isChecked = .checked
                            needsToAgreeError = false
                        }
                    } label: {
                        Text("terms_checkbox")
                            .multilineTextAlignment(.leading)
                    }
                    .indicateError(needsToAgreeError)
                    */
                    
                    Button {
                        /*
                         if(isChecked == .unchecked) {
                             needsToAgreeError = true
                         } else {
                             action()
                         }
                         */
                        action()
                    } label: {
                        Text("terms_close_button")
                            .textCase(.uppercase)
                            .commonButtonStyle()
                    }
                }
                .padding()
                .frame(minHeight: proxi.size.height)
            }
        }
        .hiddenNavigationBarStyle()
    }
}

struct FirstRunView:View {
    @State var viewTerms:Bool = false
    let action:() -> ()
    @Environment(\.locale) private var locale
    @AppStorage("preferedLanguage") var preferedLanguage:String = Language.default.code
    
    var body: some View {
        NavigationView {
            VStack(spacing:20) {
                TitleViewSimple(title: "choose_language")
                
                NavigationLink(
                    isActive: $viewTerms) {
                        VStack {
                            TitleViewWithLItem(title: "tearms_title") {
                                Button {
                                    viewTerms = false
                                } label: {
                                    Image("IconBack")
                                }
                            }
                            
                            TermsAndConditionsView() {
                                action()
                            }
                        }
                        .environment(\.locale, Locale(identifier: preferedLanguage))
                        
                        .hiddenNavigationBarStyle()
                    } label: {
                        EmptyView()
                    }
                Spacer()
                    .frame(height: Dimen.Spacing.huge)
                Image("Logo")
                    .resizable()
                    .scaledToFit()
                    .frame(height: 36.0)
                
                Text("name")
                    .foregroundColor(Color("PrimaryColor"))
                
                Spacer()
                ForEach(Language.allCases.sorted(by: { a, b in
                    a.code > b.code
                }), id: \.self.code) { item in
                    Button {
                        viewTerms = true
                        preferedLanguage = item.code
                    } label: {
                        Text(item.name)
                    }
                    .buttonStyle(ChooseLanguageButtonStyle())
                }
                
                Spacer()
            }
            .hiddenNavigationBarStyle()
            .environment(\.locale, Locale(identifier: "en-GB"))
        }
        .modifier(DesingSystemSetupModifier())
    }
}

struct ChooseLanguageButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .padding(30)
            .font(
                .system(
                    size: 18,
                    weight: .bold,
                    design: .default
                )
            )
            .frame(maxWidth: .infinity)
            .background(Color(.white))
            .cornerRadius(9)
            .shadow(
                color: Color(.black)
                    .opacity(0.15),
                radius: 10,
                x: 0,
                y: 3
            )
            .padding(.horizontal)
    }
}

struct CheckTermsAndConditionsState: ViewModifier {
    @AppStorage("isFirstRun") var isFirstRun:Bool = true
    @AppStorage("lastAgreedTerms") var lastAgreedTerms:Int = 0
    @State var showCover:Bool = false
    
    func body(content: Content) -> some View {
        content
            .onAppear(perform: {
                showCover = isFirstRun || lastAgreedTerms < 1 //magic variable
            })
            .fullScreenCover(isPresented: $showCover) {
                if(isFirstRun) {
                    FirstRunView() {
                        isFirstRun = false
                        lastAgreedTerms += 1
                        showCover = false
                    }
                } else {
                    TitleViewSimple(title: "choose_language")
                    TermsAndConditionsView {
                        lastAgreedTerms += 1
                        showCover = false
                    }
                }
            }
    }
}

struct TermsAndConditionsView_Previews: PreviewProvider {
    
    static var previews: some View {
        TermsAndConditionsView() {
            print("Hi")
        }
        
        FirstRunView() {
            print("Hi")
        }
            .previewDisplayName("First Run View")
        
        Text("The Test")
            .modifier(CheckTermsAndConditionsState())
            .previewDisplayName("theTest")
        
        RessetView()
            .previewDisplayName("Resset")
        
    }
    
    struct RessetView: View {
        @AppStorage("lastAgreedTerms") var lastAgreedTerms:Int = 0
        @AppStorage("isFirstRun") var isFirstRun:Bool = false // change to true to
        
        var body: some View {
            VStack {
                Button {
                    isFirstRun = true
                } label: {
                    Text("resset first run")
                }
                Button {
                    lastAgreedTerms = 0
                } label: {
                    Text("resset terms")
                }
            }
        }
    }
}
