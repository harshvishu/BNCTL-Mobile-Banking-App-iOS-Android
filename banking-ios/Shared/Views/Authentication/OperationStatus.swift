//
//  OperationStatus.swift
//  Allianz (iOS)
//
//  Created by Peter Velchovski on 28.02.22.
//

import Foundation
import SwiftUI
 
enum OperationStatus: Identifiable, Equatable {
    // Progress Statuses
    case waiting // Operation in progress - use for SCA and non-SCA operations
    
    //Fallback statuses
    case otpAvailabe // indicates to the user that he can use OTP
    case waitingOtp // Waiting for user input of OTP
    case waitingOtpWithPin //Waiting for user input of OTP and must be along a PIN
    case wrongOtp // the otp that is entered is not valid
    
    // Outcome Statuses
    case success // Operation successful - use for SCA and Non-SCA operations
    case rejected // Used for user-determined failure - e.g. rejected a SCA operation
    case cancelled // Used for user-determined failure - e.g. cancelled a SCA operation
    case failed(String?) // Used unknown failure - expired or invalid OTP, network error, backend error, etc..

    var id: String { self.rawValue }
    
    var rawValue: String {
        switch self {
        case .waiting:
            return "waiting"
        case .otpAvailabe:
            return "otpAvailabe"
        case .waitingOtp:
            return "waitingOtp"
        case .waitingOtpWithPin:
            return "waitingOtpWithPin"
        case .wrongOtp:
            return "wrongOtp"
        case .success:
            return "success"
        case .rejected:
            return "rejected"
        case .cancelled:
            return "cancelled"
        case .failed(let c):
            if let c = c {
                return "failed_\(c)"
            } else {
                return "failed"
            }
            
        }
    }
}

/**
 Operation Status Info
 Use this struct when a ViewModel needs to track custom errors.
 */
struct OperationStatusInfo: Hashable {
    var title: LocalizedStringKey
    var message: LocalizedStringKey

    func hash(into hasher: inout Hasher) {
        hasher.combine("\(title)")
        hasher.combine("\(message)")
    }
}
