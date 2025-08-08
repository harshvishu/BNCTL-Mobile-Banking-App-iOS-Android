//
//  MyPayeesView.swift
//  BNCTL
//
//  Created by Rahul B on 21/02/23.
//

import SwiftUI

struct MyPayeesView: View {
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    var body: some View {
        VStack {
            TitleView(
                title: "my_payees_title",
                leftItem: {
                    Button {
                        self.presentationMode.wrappedValue.dismiss()
                    } label: {
                        Image("IconBack")
                    }
                },
                rightItem: {
                    NavigationLink(destination: {
                        AddNewPayeeView()
                    }) {
                        Image("ButtonRequestCard")
                    }
                }
            )
            if #available(iOS 16.0, *) {
                List (/*@START_MENU_TOKEN@*/0 ..< 5/*@END_MENU_TOKEN@*/) { item in
                    if #available(iOS 15.0, *) {
                        HStack {
                            PayeesList(title: "rahul", id: "12345678910")
                            
                        }
                        .listRowSeparator(.hidden)
                        .listRowBackground(Color("background"))
                    } else {
                        // Fallback on earlier versions
                    }
                }.environment(\.defaultMinListRowHeight, 80) //minimum row height
                    .listStyle(.inset)
                    .scrollContentBackground(.hidden)
            } else {
                // Fallback on earlier versions
            }

        }
        .background(
            Color("background").edgesIgnoringSafeArea(.all)
        )
    }
}

struct MyPayeesView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            MyPayeesView()
        }
    }
}
