//
//  CreatePasswordView.swift
//  BNCTL
//
//  Created by harsh on 23/02/23.
//

import SwiftUI

struct CreatePasswordView: View {
    @Environment(\.presentationMode) var presentationMode
    
    @State private var newPasswordError: String?
    @State private var repeatPasswordError: String?
    
    @State var newPassword: String = ""
    @State var repeatPassword: String = ""
    
    var body: some View {
        VStack(spacing: Dimen.Spacing.huge) {
            TitleViewWithLItem(title: "create_password") {
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
                    label: Text("new_password"),
                    text: $newPassword,
                    error: newPasswordError
                ) {
                    print("match new password")
                }
                .textContentType(.password)
                
                FloatingLabelSecureField(
                    label: Text("repeat_password"),
                    text: $repeatPassword,
                    error: repeatPasswordError
                ) {
                    print("match new password")
                }
                .textContentType(.password)
                
                Spacer()

                VStack(spacing: Dimen.Spacing.huge) {
                    Button {
                        
                        newPasswordError = newPassword.isEmpty
                            ? "common_error_field_required" : nil
                        
                        repeatPasswordError = repeatPassword.isEmpty
                            ? "common_error_field_required" : nil
                       
                        
                        if (newPasswordError?.isEmpty == false
                            && repeatPasswordError?.isEmpty == false
                        )    // password can not empty
                        {
                            print("change_password")
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

struct CreatePasswordView_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            CreatePasswordView()
        }
        .padding(Dimen.Spacing.large)
      
    }
}
