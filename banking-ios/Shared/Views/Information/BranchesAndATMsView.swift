//
//  BranchesAndATMs.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 24.01.22.
//

import SwiftUI

struct BranchesAndATMsView: View {
    
    var body: some View {
        ZStack {
            Color("background").edgesIgnoringSafeArea(.all)
            VStack {
                TitleViewWithBack(title: "offices_and_atms_title")
                ScrollView {
                    ZStack {
                        Rectangle()
                            .foregroundColor(.white)
                            .cornerRadius(15)
                        VStack(alignment: .leading, spacing: 15) {
                            ListNavItem(label: "offices_and_atms_button_offices", image: "IconBranch") {
                                BranchesView()
                            }
                            ListNavItem(label: "offices_and_atms_button_atms", image: "IconATM") {
                                ATMsView()
                            }
                        }.padding()
                    }
                    .padding(.horizontal)
                }
            }
        }
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
    }
}

struct BranchesAndATMs_Previews: PreviewProvider {
    static var previews: some View {
        BranchesAndATMsView()
    }
}
