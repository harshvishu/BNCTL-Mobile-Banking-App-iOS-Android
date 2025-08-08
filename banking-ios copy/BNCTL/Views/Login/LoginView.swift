//
//  LoginView.swift
//  BNCTL
//
//  Created by Prem's on 09/02/23.
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
                                // TODO: See if this needs to be declared again in BNCTL target
                            }
                            

                            VStack(spacing: Dimen.Spacing.large) {
                                Image("Logo")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(height: 36.0)
                            }
                            
                            // TODO: Not clear what this text means 
                            Text("SOME TEXT TODO: //")
                                .foregroundColor(Color("SecondaryTextColor"))
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
    static var previews: some View {
        LoginView()
    }
}
