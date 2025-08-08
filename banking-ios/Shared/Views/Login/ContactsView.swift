//
//  ContactsView.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 12.12.22.
//

import SwiftUI

struct ContactsView: View {
    
    @StateObject var contactViewModel = ContactsViewModel()
    @Environment(\.presentationMode) var presentationMode
    
    fileprivate func showAddress(contacts: Contacts) -> some View {
        return HStack {
            Image("IconBranchesAndATMs")
            Text(contacts.address)
                .multilineTextAlignment(.leading)
            Spacer()
        }
    }
    
    var body: some View {
        VStack {
            TitleViewWithLItem(title: "contacts_title") {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconBack")
                }
            }
            if let contacts = contactViewModel.contacts {
            ScrollView {
                    VStack(alignment:.leading, spacing: 10) {
                        /*
                         ListButtonItem(
                         label: LocalizedStringKey(constacts.address),
                         image: "location"
                         ) {
                         }
                         */
                        Group {
                            HStack {
                                Image("Logo")
                                    .resizable()
                                    .frame(
                                        maxWidth: 113.41,
                                        maxHeight: 28.06
                                    )
                            }
                            .frame(minHeight: 107)
                            if let address = (contacts.address.split(separator: "\n")[1...]).joined().addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) {
                                let url = URL(string: "maps://?q=\(address)")
                                Link(destination: url!) {
                                    showAddress(contacts: contacts)
                                }
                                .foregroundColor(.black)
                            } else {
                                showAddress(contacts: contacts)
                            }
                            
                            
                            Button {
                                UIPasteboard.general.string = contacts.swiftCode
                            } label: {
                                HStack {
                                    Image("IconAccount")
                                    Text("contacts_swift \(contacts.swiftCode)")
                                        .foregroundColor(.black)
                                    Spacer()
                                }
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(
                            Color
                                .white
                                .cornerRadius(Dimen.CornerRadius.regular))
                        Text("contacts_contact")
                            .padding(.bottom, -8)
                        Group {
                            let phone = contacts.phone
                                .replacingOccurrences(
                                    of: " ",
                                    with: "-"
                                )
                            let phoneURL = URL( string: "tel:\(phone)")!
                            Link(destination: phoneURL) {
                                HStack {
                                    Image("Phone")
                                    Text(contacts.phone)
                                    Spacer()
                                }
                            }
                            Link(destination: URL(string: "mailto:\(contacts.email)")!) {
                                HStack {
                                    Image("Email")
                                    Text(contacts.email)
                                    Spacer()
                                }
                            }
                            Link(destination: URL(string: contacts.website)!) {
                                HStack {
                                    Image("Globe")
                                    Text(contacts.website)
                                    Spacer()
                                }
                            }
                        }
                        .foregroundColor(.black)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(
                            Color
                                .white
                                .cornerRadius(Dimen.CornerRadius.regular))
                        
                        Text("contacts_social")
                            .padding(.bottom, -8)
                        
                        HStack() {
                            ForEach(contacts.socialMedia) { socialMedia in
                                Link(destination: URL(string: socialMedia.url)!) {
                                    Image(socialMedia.platformKey)
                                }
                                .frame(maxWidth: .infinity)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(
                            Color
                                .white
                                .cornerRadius(Dimen.CornerRadius.regular))
                    }
                    .padding()
            }
            } else {
                Spacer()
                ProgressView()
                Spacer()
            }
        }
        .hiddenNavigationBarStyle()
        .background(
            Color("background")
                .ignoresSafeArea(.all)
        )
        .onAppear {
            contactViewModel.fetchBranches()
        }
    }
}

struct ContactsView_Previews: PreviewProvider {
    static var previews: some View {
        ContactsView()
    }
}
