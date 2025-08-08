//
//  LoginView.swift
//  Shared
//
//  Created by Evgeniy Raev on 10.08.21.
//

import SwiftUI

struct LoginView: View {
    @EnvironmentObject private var login: LoginModel

    var body: some View {
        NavigationView {
            GeometryReader { geometry in
                ScrollView([.vertical]) {
                    VStack {
                        Group { // Language Button & Logo
                            HStack {
                                Spacer()
                                LocalizationButton()
                            }
                            Spacer()
                                .frame(minHeight: Dimen.Spacing.huge)

                            VStack(spacing: Dimen.Spacing.large) {
                                Image("Logo")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(height: 36.0)
                                Text("name")
                                    .foregroundColor(Color("PrimaryColor"))
                            }
                            Spacer()
                                .frame(minHeight: Dimen.Spacing.huge)
                        }

                        LoginForm()

                        Spacer()
                            .frame(minHeight: Dimen.Spacing.huge)

                        HStack {
                            Spacer()
                            QuickButton(
                                image: "IconLoginNews",
                                label: "login_label_news"
                            ) {
                                NewsView()
                            }
                            Spacer()
                            QuickButton(
                                image: "IconLoginContacts",
                                label: "login_label_contacts"
                            ) {
                                ContactsView()
                            }
                            Spacer()
                            QuickButton(
                                image: "IconLoginBranches",
                                label: "login_label_branches"
                            ){
                                BranchesView()
                            }
                            Spacer()
                            QuickButton(
                                image: "IconLoginInfo",
                                label: "login_label_info"
                            ) {
                                Information()
                            }
                            Spacer()
                        }
                    }
                    .padding(Dimen.Spacing.large)
                    .frame(minHeight: geometry.size.height)
                }
            }
            .hiddenNavigationBarStyle()
        }
    }
}

struct QuickButton<Destination:View>: View {

    let image:String
    let label:LocalizedStringKey
    let destination:() -> Destination

    init(
        image:String,
        label:LocalizedStringKey,
        @ViewBuilder destination: @escaping () -> Destination
    ) {
        self.image = image
        self.label = label
        self.destination = destination
    }

    var body: some View {
        NavigationLink {
            VStack {
                destination()
            }

        } label: {
            VStack {
                OutlinedIcon(image: image)
                Text(label)
                    .font(.subheadline)
                    .foregroundColor(Color("SecondaryTextColor"))
            }
        }
    }
}

struct LoginView_Previews: PreviewProvider {

    @State static var lang: String = "en"

    static var previews: some View {
        LoginView()
            .environmentObject(LoginModel())
            .environment(\.locale, .init(identifier: "bg"))
    }
}
