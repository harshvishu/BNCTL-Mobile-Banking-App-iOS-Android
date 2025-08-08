//
//  UsernameView.swift
//  BNCTL
//
//  Created by harsh on 23/02/23.
//

import SwiftUI

struct CreateUsernameView: View {
    @Environment(\.presentationMode) var presentationMode
    
    @State private var usernameError: String?
    @State var username: String = ""
    
    var body: some View {
        VStack(spacing: Dimen.Spacing.huge) {
            TitleViewWithLItem(title: "create_username") {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconBack")
                }
            }
            
            Spacer()
                .frame(height: Dimen.Spacing.huge)

            
            VStack(alignment:.leading, spacing: Dimen.Spacing.huge) {
                
                FloatingLabelTextField(
                    label: Text("username"),
                    text: $username,
                    error: usernameError
                )
                .textContentType(.username)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                
                Spacer()

                VStack(spacing: Dimen.Spacing.huge) {
                    Button {
                        usernameError = username.isEmpty
                            ? "common_error_field_required" : nil
                       
                        
                        if (usernameError?.isEmpty == false)    // username not empty
                        {
                           print("Create username")
                        }
                    } label: {
                        Text("common_button_next")
                            .commonButtonStyle()
                    }
                }
            }
        }
    }
}

struct CreateUsernameView_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            CreateUsernameView()
        }
        .padding(Dimen.Spacing.large)
      
    }
}
