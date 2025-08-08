//
//  SettingsView.swift
//  Allianz (iOS)
//
//  Created by Prem's on 10/02/22.
//

import SwiftUI

struct SettingsView: View {
    
    var body: some View {
        ZStack{
            Color("background").edgesIgnoringSafeArea(.all)
            VStack {
                TitleViewWithBack(title: "settings_title")
                ScrollView {
                    VStack {
                        ZStack {
                            Rectangle()
                                .foregroundColor(.white)
                                .cornerRadius(15)
                            VStack(alignment: .leading, spacing: 15) {
                                ListNavItem(label: "settings_label_languages", image: "IconLanguages") {
                                    LanguageView()
                                }
                            }.padding()
                        }
                        .padding(.horizontal)
                        .padding(.bottom, 10)
                        Spacer()
                    }
                }
            }
        }
        .hiddenNavigationBarStyle()
    }
}

struct SettingsView_Previews: PreviewProvider {
    
    static var previews: some View {
        SettingsView()
    }
}
