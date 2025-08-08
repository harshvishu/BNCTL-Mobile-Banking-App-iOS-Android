//
//  OperationOTPView.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 7.04.22.
//

import SwiftUI
import Combine

struct OperationOTPView: View {
    @State var otpCode:String = ""
    @State var pinCode:String = ""
    let withPin:Bool
    let onConfirm:(String, String?) -> Void
    let prefix:String
    
    init(
        withPin:Bool = false,
        prefix:String = "common",
        onConfirm:@escaping ((String, String?) -> Void)
    ) {
        self.withPin = withPin
        self.onConfirm = onConfirm
        self.prefix = prefix
    }
    // TODO: add form validation
    var body: some View {
        VStack(alignment:.leading, spacing: 16) {
            Text(LocalizedStringKey(stringLiteral: localizedKey("title")))
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(Color("PrimaryColor"))
            Text(LocalizedStringKey(stringLiteral: localizedKey("message")))
                .font(.subheadline)
            
            Group {
                let codeLabel = "operation_\(prefix)_otp_code"
                FloatingLabelTextField(
                    label: Text(LocalizedStringKey(stringLiteral: codeLabel)),
                    text: $otpCode)
                    .textContentType(.oneTimeCode)
                    .keyboardType(.numberPad)
                
                if(withPin) {
                    let pinLabel = "operation_\(prefix)_otp_pin"
                    FloatingLabelTextField(
                        label: Text(LocalizedStringKey(stringLiteral:  pinLabel)),
                        text: $pinCode)
                }
            }
            .padding(.top, 50)
            .autocapitalization(.none)
            .disableAutocorrection(true)
            Spacer()
            Button {
                //Not sure
                if(withPin) {
                    onConfirm(otpCode, pinCode)
                } else {
                    onConfirm(otpCode, nil)
                }
            } label: {
                Text(
                    LocalizedStringKey(stringLiteral: "operation_\(prefix)_otp_confirm")
                )
                .commonButtonStyle()
            }
        }
        .padding(40)
    }
    
    func localizedKey(_ key:String) -> StringLiteralType {
        return "operation_\(prefix)_otp\(withPin ? "_pin" : "")_\(key)"
    }
}

struct OperationOTPView_Previews: PreviewProvider {
    static var previews: some View {
        OperationOTPView(withPin: false) { otp, pin in
            
        }
    }
}
