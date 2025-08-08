//
//  Information.swift
//  MobileBanking
//
//  Created by Evgeniy Raev on 12.12.22.
//

import SwiftUI
import UIKit

struct Information: View {
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            VStack {
                TitleViewWithLItem(title: "information_title") {
                    Button {
                        self.presentationMode.wrappedValue.dismiss()
                    } label: {
                        Image("IconBack")
                    }
                }
                
                VStack(spacing:30){
                    
                    NavigationLink {
                        DeviceInfo()
                    } label: {
                        HStack {
                            Image("Device")
                            Text("information_device")
                                .foregroundColor(.black)
                            Spacer()
                            Image("ArrowRight")
                        }
                    }
                    
                    NavigationLink {
                        AppInfo()
                    } label: {
                        HStack {
                            Image("App")
                            Text("information_app")
                                .foregroundColor(.black)
                            Spacer()
                            Image("ArrowRight")
                        }
                    }

                }
                .padding()
                .background(
                    Color
                        .white
                        .cornerRadius(Dimen.CornerRadius.regular)
                )
                Spacer()
            }
            .hiddenNavigationBarStyle()
            .padding()
            .background(
                Color("background")
                    .ignoresSafeArea(.all)
            )
        }
        .hiddenNavigationBarStyle()
    }
}

struct DeviceInfo:View {
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        VStack {
            TitleViewWithLItem(title: "information_device_title") {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconBack")
                }
            }
            
            VStack(alignment: .leading, spacing: 11) {
                HStack {
                    Text("information_device_model")
                        .font(.subheadline)
                        .foregroundColor(
                            Color("SecondaryTextColor")
                        )
                    Spacer()
                }
                /*
                Text(UIDevice.current.name)
                Text(UIDevice.current.model)
                Text(UIDevice.current.localizedModel)
                */
                Text(machineName())
                
                Text("information_device_os")
                    .font(.subheadline)
                    .foregroundColor(
                        Color("SecondaryTextColor")
                    )
                    .padding(.top, 27)
                Text("\(UIDevice.current.systemName) \(UIDevice.current.systemVersion)")
            }
            .padding()
            .background(
                Color
                    .white
                    .cornerRadius(Dimen.CornerRadius.regular)
            )
            .padding()
            Spacer()
        }
        .background(
            Color("background")
                .ignoresSafeArea(.all)
        )
        .hiddenNavigationBarStyle()
    }
    
    func machineName() -> String {
      var systemInfo = utsname()
      uname(&systemInfo)
      let machineMirror = Mirror(reflecting: systemInfo.machine)
      return machineMirror.children.reduce("") { identifier, element in
        guard let value = element.value as? Int8, value != 0 else { return identifier }
        return identifier + String(UnicodeScalar(UInt8(value)))
      }
    }
}

struct AppInfo:View {
    @Environment(\.presentationMode) var presentationMode
    
    let version: String? = Bundle.main.infoDictionary!["CFBundleShortVersionString"] as? String
    
    var body: some View {
        VStack {
            TitleViewWithLItem(title: "information_app_title") {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconBack")
                }
            }
            VStack {
                VStack(alignment: .leading, spacing: 11) {
                    HStack {
                        Text("information_app_appliaction")
                            .font(.subheadline)
                            .foregroundColor(
                                Color("SecondaryTextColor")
                            )
                        Spacer()
                    }
                    if let version = version {
                        Text("information_app_version \(version)")
                    } else {
                        Text("there is problem getting the version")
                    }
                }
                .padding()
                .background(
                    Color
                        .white
                        .cornerRadius(Dimen.CornerRadius.regular)
                )
                .padding()
                
            }
            
            Spacer()
        }
        .background(
            Color("background")
                .ignoresSafeArea(.all)
        )
        .hiddenNavigationBarStyle()
    }
}

struct Information_Previews: PreviewProvider {
    static var previews: some View {
        Information()
        
        DeviceInfo()
            .previewDisplayName("Device")
        
        AppInfo()
            .previewDisplayName("App")
    }
}
