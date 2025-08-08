//
//  ChangeUsernameView.swift
//  BNCTL
//
//  Created by harsh on 23/02/23.
//

import SwiftUI

struct ChangeUsernameView: View {
    @Environment(\.presentationMode) var presentationMode
    
    @State private var usernameError: String?
    @State private var newUsernameError: String?
    @State private var repeatUsernameError: String?
    
    @State var username: String = ""
    @State var newUsername: String = ""
    @State var repeatUsername: String = ""
    
    var body: some View {
        VStack(spacing: Dimen.Spacing.huge) {
            TitleViewWithLItem(title: "change_username") {
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
                    label: Text("old_username"),
                    text: $username,
                    error: usernameError
                )
                .textContentType(.username)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                
                FloatingLabelTextField(
                    label: Text("new_username"),
                    text: $newUsername,
                    error: newUsernameError
                )
                .textContentType(.username)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                
                FloatingLabelTextField(
                    label: Text("repeat_username"),
                    text: $repeatUsername,
                    error: repeatUsernameError
                )
                .textContentType(.username)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                
                Spacer()

                VStack(spacing: Dimen.Spacing.huge) {
                    Button {
                        usernameError = username.isEmpty
                            ? "common_error_field_required" : nil
                        
                        newUsernameError = newUsername.isEmpty
                            ? "common_error_field_required" : nil
                       
                        repeatUsernameError = repeatUsername.isEmpty
                            ? "common_error_field_required" : nil
                        
                        if (usernameError?.isEmpty == false
                            && newUsernameError?.isEmpty == false
                            && repeatUsernameError?.isEmpty == false
                        )    // username must not be empty
                        {
                           print("change username")
                        }
                    } label: {
                        Text("common_button_next")
                            .commonButtonStyle()
                    }
                }
            }
        }
        .padding(.horizontal)
        .hiddenNavigationBarStyle()
    }
}

struct ChangeUsernameView_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            ChangeUsernameView()
        }
        .padding(Dimen.Spacing.large)
    }
}
