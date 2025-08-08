//
//  ChangePasswordView.swift
//  BNCTL
//
//  Created by harsh on 23/02/23.
//

import SwiftUI

struct ChangePasswordView: View {
    @Environment(\.presentationMode) var presentationMode
    
    @State private var oldPasswordError: String?
    @State private var newPasswordError: String?
    @State private var repeatPasswordError: String?
    
    @State var oldPassword: String = ""
    @State var newPassword: String = ""
    @State var repeatPassword: String = ""
    
    var body: some View {
        VStack(spacing: Dimen.Spacing.huge) {
            TitleViewWithLItem(title: "change_password") {
                Button {
                    self.presentationMode.wrappedValue.dismiss()
                } label: {
                    Image("IconBack")
                }
            }
            
            Spacer()
                .frame(height: Dimen.Spacing.huge)

            
            VStack(alignment:.leading, spacing: Dimen.Spacing.huge) {
                
                FloatingLabelSecureField(
                    label: Text("old_password"),
                    text: $oldPassword,
                    error: oldPasswordError
                ) {
                    print("match new password")
                }
                .textContentType(.password)
                
                FloatingLabelSecureField(
                    label: Text("new_password"),
                    text: $newPassword,
                    error: newPasswordError
                ) {
                    print("match new password")
                }
                .textContentType(.newPassword)
                
                FloatingLabelSecureField(
                    label: Text("repeat_password"),
                    text: $repeatPassword,
                    error: repeatPasswordError
                ) {
                    print("match new password")
                }
                .textContentType(.newPassword)
                
                Spacer()

                VStack(spacing: Dimen.Spacing.huge) {
                    Button {
                        oldPasswordError = oldPassword.isEmpty
                            ? "common_error_field_required" : nil
                        
                        newPasswordError = newPassword.isEmpty
                            ? "common_error_field_required" : nil
                        
                        repeatPasswordError = repeatPassword.isEmpty
                            ? "common_error_field_required" : nil
                        
                        if (oldPasswordError?.isEmpty == false
                            && newPasswordError?.isEmpty == false
                            && repeatPasswordError?.isEmpty == false
                        )    // password can not empty
                        {
                            print("Change password")
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

struct ChangePasswordView_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            ChangePasswordView()
        }
        .padding(Dimen.Spacing.large)
      
    }
}
