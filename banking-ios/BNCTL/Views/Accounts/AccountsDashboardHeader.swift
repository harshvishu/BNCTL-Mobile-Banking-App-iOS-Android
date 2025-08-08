//
//  AccountsDashboardHeader.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 01/04/23.
//

import SwiftUI

struct AccountsDashboardHeader: View {
    var body: some View {
        HStack {
            Image("Logo")
                .resizable()
                .scaledToFit()
                .frame(height: 30.0)
                .padding(.horizontal, 10)
            Spacer()
        }
    }
}

struct AccountsDashboardHeader_Previews: PreviewProvider {
    static var previews: some View {
        AccountsDashboardHeader()
    }
}
