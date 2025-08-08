//
//  PayeeListItem.swift
//  BNCTL
//
//  Created by harsh vishwakarma on 12/04/23.
//

import SwiftUI

struct PayeeListItem: View {
    var payee: Payee
    
    var body: some View {
        VStack {
            ZStack {
                Rectangle()
                    .foregroundColor(.white)
                    .cornerRadius(15)
                VStack(alignment: .leading, spacing: 15) {
                    
                    NavigationLink(destination:{
                        PayeeDetails(model: EditPayeeViewModel(editingMode: .edit(payee)))
                    }){
                        
                        HStack(alignment: .top) {
                            ZStack {
                                Circle()
                                    .strokeBorder(Color("SecondaryColor"), lineWidth: 1)
                                    .frame(width: 32, height: 32)
                                Image("IconTransferBetweenOwnAccounts") // TODO: FIXME: Pending Icon
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 24.0, height: 24.0)
                            }
                            VStack(alignment: .leading, spacing: Dimen.Spacing.short) {
                                Text(payee.name)
                                    .font(.subheadline)
                                    .multilineTextAlignment(.leading)
                                    .foregroundColor(Color("PrimaryTextColor"))
                                Text(payee.accountNumber)
                                    .font(.footnote)
                                    .foregroundColor(Color("SecondaryTextColor"))
                            }
                            .frame(maxWidth:.infinity,alignment:.leading)
                        }
                    }
                    .isDetailLink(false)
                    
                }.padding()
            }
            .padding(.horizontal)
        }
    }
}

struct PayeeListItem_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            ZStack {
                Color.gray.opacity(0.5)
                    .ignoresSafeArea()
                ScrollView {
                    VStack {
                        ForEach(Payee.preview) {
                            PayeeListItem(payee: $0)
                        }
                    }
                }
            }
        }
    }
}
