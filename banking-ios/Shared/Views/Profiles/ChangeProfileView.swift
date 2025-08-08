    //
    //  ChangeProfile.swift
    //  Allianz (iOS)
    //
    //  Created by Dimitar Stoyanov Chukov on 11.03.22.
    //

import SwiftUI

struct ChangeProfileView: View {
    
    @ObservedObject var profilesModel: ProfilesViewModel
    @State var showAlert: Bool = false
    @State var selected: String = ""
    
    init(isLocal: Bool = false) {
        profilesModel = ProfilesViewModel(isLocal)
    }
    
    var body: some View {
        ZStack {
            Color("background").edgesIgnoringSafeArea(.all)
            VStack {
                TitleViewWithBack(title: "change_customer_title")
                ScrollView {
                    VStack(alignment: .leading, spacing: 30) {
                        ForEach(profilesModel.profiles, id: \.customerNumber) { profile in
                            Button {
                                if (profile.currentlySelected) {
                                    // Do nothing
                                } else {
                                    selected = profile.customerNumber
                                    showAlert = true
                                }
                            } label: {
                                ProfileView(profile: profile)
                                    .onAppear {
                                        if (profile.currentlySelected) {
                                            selected = profile.customerNumber
                                        }
                                    }
                                    .foregroundColor(Color("PrimaryTextColor"))
                            }
                        }
                    }
                    .padding()
                    .background(Color.white.cornerRadius(16))
                    .compatibleAllert(
                        showAlert: $showAlert,
                        titleKey: "change_customer_title",
                        message: "change_customer_dialog_message",
                        defailtLabel: "change_customer_dialog_confirm",
                        cancelLabel: "change_customer_dialog_reject",
                        defaultAction: {
                            profilesModel.changeProfile(selectedProfile: selected)
                        }
                    )
                }
                .padding(.horizontal, 20)
                .padding(.vertical)
                .hiddenNavigationBarStyle()
                .hiddenTabBar()
                .onAppear(perform: {
                    profilesModel.getProfiles()
                })
                
            }
        }
    }
}

struct ProfileView: View {
    
    let profile: Profile
    
    func getInitials() -> String {
        let profileName = profile
            .accountOwnerFullName
            .trimmingCharacters(in: .whitespacesAndNewlines)
        
        guard profileName.isEmpty == false else {
            return ""
        }
        
        let components = profileName.components(separatedBy: " ")
        
        if(components.count == 1) {
            return "\(components.first!.first!)"
        } else {
            return "\(components.first!.first!)\(components.last!.first!)"
        }
    }
    
    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            ZStack {
                Circle()
                    .strokeBorder(Color("SecondaryColor"), lineWidth: 1)
                    .frame(width: 36, height: 36)
                Text(getInitials())
            }
            .foregroundColor(Color("SecondaryColor"))
            Text(profile.accountOwnerFullName)
                .multilineTextAlignment(.leading)
            Spacer()
            if (profile.currentlySelected) {
                Image("IconTick")
            }
        }
        
    }
}

struct ChangeProfile_Previews: PreviewProvider {
    static var previews: some View {
        ChangeProfileView(isLocal: true)
    }
}
