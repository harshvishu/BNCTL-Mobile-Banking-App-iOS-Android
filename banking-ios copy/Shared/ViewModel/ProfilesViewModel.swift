//
//  ProfilesViewModel.swift
//  Allianz (iOS)
//
//  Created by Dimitar Stoyanov Chukov on 11.03.22.
//

import Foundation

class ProfilesViewModel: ObservableObject {
    
    @Published var profiles: [Profile] = []

    private var refetchProfiles: Bool = false {
        didSet {
            if refetchProfiles {
                self.getProfiles()
            }
        }
    }
    
    init(_ isLocal: Bool = false) {
        if (isLocal) {
            profiles = ProfileModel.listPreview!
        }
    }
    
    func getProfiles() {
        self.refetchProfiles = false
        ProfileService().getProfiles() { error, profileResponse in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let profileResult = profileResponse {
                    self.profiles = profileResult
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }
    
    // Refactor this when the backend is changed to the standard
    func changeProfile(selectedProfile: String) {
        let profileIsPresent = self.profiles.contains(where: { profile in
            profile.customerNumber == selectedProfile
        })
        let accessToken = DataStore.instance.getLoginToken() ?? ""
        let accessTokenParts = accessToken.split(separator: ".")
        if (profileIsPresent && accessTokenParts.count == 3) {
            DataStore.instance.setLoginToken(
                token: "\(accessTokenParts[0]).\(selectedProfile).\(accessTokenParts[2])"
            )
            self.refetchProfiles = true
            PermissionsModel.shared.updateUserPermissions()
        }
    }
    
}
