//
//  LoginView.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 21/03/23.
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
                            

                            VStack(spacing: Dimen.Spacing.large) {
                                Image("Logo")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(height: 36.0)
                            }
                            
                            Text("name")
                                .foregroundColor(Color("PrimaryButtonColor"))
                                .font(.system(size: Dimen.TextSize.info))
                                .multilineTextAlignment(.center)
                                .padding()
                            
                            Spacer()
                        }

                        LoginForm()

                        Spacer()
                            .frame(minHeight: Dimen.Spacing.huge)

                    }
                    .padding(Dimen.Spacing.large)
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
    static var previews: some View {
        LoginView()
    }
}
