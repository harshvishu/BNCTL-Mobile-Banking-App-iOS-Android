//
//  ScaModal.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 28.03.22.
//

import SwiftUI

//MARK: - View extension
extension View {
    
    /// This is modal handaling the SCA flow
    /// - Parameters:
    ///   - scaStatus: the status that have to be displayed
    ///   - prefix: the translation prefix that will be used for the messages
    ///   - flowComletion: operation status modal is closed
    ///   - acceptOTP: the OTP warning is shown
    ///   - declineOTP: the user does not want to use OTP
    ///   - otpHandler(otp, pin?): otp is received,
    func operationStatusModal(
        operationStatus:Binding<Confirmation<OperationStatus>>,
        prefix:String = "common",
        flowComletion:(() -> ())? = nil,
        acceptOTP:(() -> ())? = nil,
        declineOTP:(() -> ())? = nil,
        otpHandler:((_ otp:String,_ pin:String?) -> ())? = nil
    ) -> some View {
        self.modifier(
            OperationStatusModal(
                operationStatus: operationStatus,
                prefix: prefix,
                acceptOTP: acceptOTP,
                declineOTP: declineOTP,
                otpHandler: otpHandler,
                flowCompletion: flowComletion
            )
        )
    }
}

struct Confirmation<V> where V:Identifiable, V:Equatable {
    var restricted:[V]
    private var current:V?
    private var next:V?
    
    var status:V? {
        get {
            return self.current
        }
        set {
            if(restricted.contains(where: { el in
                el == newValue
            })) {
                self.next = newValue
            } else {
                self.current = newValue
            }
        }
    }
    
    var showAllert:Bool {
        get {
            return next != nil
        }
        set {
            
        }
    }
    
    init(restricted:[V]) {
        self.restricted = restricted
        self.current = nil
        self.next = nil
    }
    
    mutating func accept() {
        current = next
        next = nil
    }
    
    mutating func decline() {
        //current = nil
        next = nil
    }
}

struct OperationStatusModal: ViewModifier {
    @Binding var operationStatus:Confirmation<OperationStatus>
    let prefix:String
    let acceptOTP:(() -> ())?
    let declineOTP:(() -> ())?
    let otpHandler:((_ otp:String, _ pin:String?) -> ())?
    let flowCompletion:(() -> ())?
    
    
    func body(content: Content) -> some View {
        content
            .compatibleFullScreen(item: $operationStatus.status, content: { scaStage in
            switch scaStage {
            case .waiting:
                OperationWaitingView(
                    type: .sca,
                    localizedStringKeyPrefix: prefix
                )
                .compatibleAllert(
                    showAlert: $operationStatus.showAllert,
                    titleKey: LocalizedStringKey(
                        stringLiteral: "operation_\(prefix)_confirmation_title"),
                    defailtLabel: LocalizedStringKey(
                        stringLiteral: "operation_\(prefix)_confirmation_accept"),
                    cancelLabel: LocalizedStringKey(
                        stringLiteral: "operation_\(prefix)_confirmation_decline"),
                    defaultAction: {
                        acceptOTP?()
                        operationStatus.decline()
                    },cancelAction: {
                        declineOTP?()
                        operationStatus.decline()
                        operationStatus.status = nil
                    }
                )
            case .waitingOtp, .waitingOtpWithPin:
                OperationOTPView(withPin: scaStage == .waitingOtpWithPin) { otp, pin in
                    otpHandler?(otp, pin)
                }
                .compatibleAllert(
                    showAlert: $operationStatus.showAllert,
                    titleKey: LocalizedStringKey(
                        stringLiteral: "operation_\(prefix)_wrong_otp"),
                    defailtLabel:LocalizedStringKey(
                        stringLiteral: "operation_\(prefix)_confirmation_accept"),
                    defaultAction: {
                        operationStatus.decline()
                    }
                )
            default:
                OperationOutcomeView(
                    status: scaStage,
                    localizedStringKeyPrefix: prefix) {
                        flowCompletion?()
                        operationStatus.decline()
                        operationStatus.status = nil
                }
            }
        })
    }
}
//MARK: - Preview
struct ScaModal_Previews: PreviewProvider {
    static var previews: some View {
        
        StateProblemMockup()
    }
    
    struct StateProblemMockup: View {
        @StateObject var model = PreviewModel()
        @State var text = ""
        
        var body: some View {
            VStack(spacing:16) {
                TextField("something", text: $model.username)
                SecureField("pass", text: $text)
                
                ForEach(OperationStatus.allReturnCases) { el in
                    Button {
                        model.status.status = .waiting
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            model.status.status = el
                        }
                    } label: {
                        Text(el.name)
                    }
                }
            }
            .operationStatusModal(operationStatus: $model.status) {
                print("operation status modal is closed")
            } acceptOTP: {
                print("the OTP warning is shown")
            } declineOTP: {
                print("the user does not want to use OTP")
            } otpHandler: { otp, pin in
                print("otp is received")
            }
        }
    }
    
    class PreviewModel: ObservableObject {
        @Published var status = Confirmation<OperationStatus>(restricted: [
            .otpAvailabe,
            .wrongOtp
        ])
        
        @Published var username = ""
        @Published var password = ""
    }
    
    enum FocusableField: Hashable {
      case firstName
      case lastName
    }
}

//MARK: OperationStatus extension
fileprivate extension OperationStatus {
    static var allReturnCases: [OperationStatus] {
        return [
            .success,
            .cancelled,
            .waitingOtp,
            .waitingOtpWithPin,
            .rejected,
            .failed(nil),
        ]
    }
    
    var name:String {
        self.rawValue.capitalized
    }
}
