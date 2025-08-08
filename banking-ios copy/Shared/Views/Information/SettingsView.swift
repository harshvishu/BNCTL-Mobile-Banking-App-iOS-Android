//
//  SettingsView.swift
//  MobileBanking
//
//  Created by harsh vishwakarma on 15/03/23.
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
                        
                        ZStack {
                            Rectangle()
                                .foregroundColor(.white)
                                .cornerRadius(15)
                            VStack(alignment: .leading, spacing: 15) {
                                // TODO: Icon missing for change username
                                ListNavItem(label: "change_username", image: "IconLanguages") {
                                    ChangeUsernameView()
                                }
                            }.padding()
                        }
                        .padding(.horizontal)
                        .padding(.bottom, 10)
                        
                        ZStack {
                            Rectangle()
                                .foregroundColor(.white)
                                .cornerRadius(15)
                            VStack(alignment: .leading, spacing: 15) {
                                // TODO: Icon missing for change password
                                ListNavItem(label: "change_password", image: "IconLanguages") {
                                    ChangePasswordView()
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
