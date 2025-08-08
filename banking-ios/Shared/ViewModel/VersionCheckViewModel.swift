//
//  VersionCheckViewModel.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 10.01.23.
//

import Foundation

class VersionViewModel: ObservableObject {
    
    
    @Published var verion:MobileAppVersion?
    @Published var force:Bool = false
    @Published var recomend:Bool = false
    
    let currentVersion:Double
    let latestKnownVersion:Double
    
    init() {
        let bundleVersion = Bundle.main.infoDictionary!["CFBundleVersion"] as? String
        
        if let bundleVersion = bundleVersion, let parsedVersion = Double(bundleVersion) {
            currentVersion = parsedVersion
        } else {
            currentVersion = -1
        }
        
        latestKnownVersion = UserDefaults.standard.double(forKey: "latestKnownVersion")
    }
    
    func checkVersion() async {
        do {
            let version = try await VersionCheckService().checkVersion()
            let force = version.minimum.productVersion > currentVersion
            let recomend = (force == false) && version.latest.productVersion > max(currentVersion, latestKnownVersion)
            
            await MainActor.run {
                self.verion = version
                self.force = force
                self.recomend = recomend
            }
        } catch {
            
        }
    }
    
    func viewed() {
        UserDefaults.standard.set(
            verion?.latest.productVersion,
            forKey: "latestKnownVersion"
        )
    }
    
}
