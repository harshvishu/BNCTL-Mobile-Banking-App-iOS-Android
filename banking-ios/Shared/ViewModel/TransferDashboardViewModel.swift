//
//  TransferDashboardViewModel.swift
//  Allianz
//
//  Created by Evgeniy Raev on 25.11.21.
//

import Foundation
import SwiftUI

class TransferModel: ObservableObject, Identifiable {
    @Published var type:TransferType
    
    init(_ type:TransferType) {
        self.type = type
    }
    
    var id: String {
        get { self.type.rawValue }
    }
    
}

class TransferDashboardViewModel: ObservableObject {
    
    enum FallbackState {
        case createTransfer(TransferCreateResponse)
        case transfer(Transfer)
        case pendingTransfers([String], Bool)
        case utilityBills([String], Account)
    }
    
    @Published var screen: SelectedScreen?
    @Published var operationStatus = Confirmation<OperationStatus>(restricted: [
        .otpAvailabe,
        .wrongOtp
    ])
    
    private var fallbackState:FallbackState?
    @Published var operationStatusError: OperationStatusInfo? = nil
    @Published var transferValidationResult: TransferValidateResponse? = nil
    
    func makeTransfer(transfer: Transfer) {
        self.operationStatus.status = .waiting
        
        TransferService().createTransfer(params: transfer) { [self] error, result in
            if let error = error {
                if let serverError = error as? ServerError {
                    if let transferError = TransferError(rawValue: serverError.error.target ?? "") {
                        // TODO: Handle more .sca errors
                        switch transferError {
                        case .scaCancelled:
                            self.operationStatusError = OperationStatusInfo(
                                title: "operation_transfer_rejected_label_title",
                                message: "operation_transfer_rejected_label_message")
                            self.operationStatus.status = .rejected
                            self.screen = nil
                            self.fallbackState = nil
                        case .errFallBackAgreement, .scaExpired, .errScaError:
                            self.operationStatus.status = .otpAvailabe
                            self.fallbackState = .transfer(transfer)
                        case .errInvalidSms:
                            self.operationStatus.status = .wrongOtp
                        case .errNoFallBack:
                            self.operationStatus.status = nil
                            self.fallbackState = nil
                        default:
                            self.operationStatus.status = .failed(nil)
                            self.fallbackState = nil
                            self.screen = nil
                        }
                    } else {
                        self.operationStatus.status = .failed(nil)
                        self.fallbackState = nil
                        self.screen = nil
                    }
                }
            } else if let result = result {
                self.operationStatus.status = .waitingOtp
                self.fallbackState = .createTransfer(result)
                screen = nil
            } else {
                self.fallbackState = nil
                self.operationStatus.status = .failed(nil)
                screen = nil
            }
        }
    }
    
    func confirmTransferUsing(otp: String, transferCreateResponse: TransferCreateResponse) {
        self.operationStatus.status = .waiting
        TransferService().confirmTransfer(params: ConfirmTransfer(validationRequestId: transferCreateResponse.validationRequestId, secret: otp))
        { [self] error, result in
            
            if let error = error {
                if let serverError = error as? ServerError,
                   let transferError = TransferError(rawValue: serverError.error.target ?? "") {
                    switch transferError {
                    case .otp:
                        self.operationStatus.showAllert = true
                    default:
                        self.fallbackState = nil
                        self.operationStatus.status = .failed(nil)
                        screen = nil
                    }
                } else {
                    self.operationStatus.status = .failed(nil)
                    self.fallbackState = nil
                    self.screen = nil
                }
            }
            else if let result = result {
                switch result.status {
                case .success:
                    self.operationStatus.status = .success
                case .failure:
                    self.operationStatus.status = .failed("Something went wrong")
                }
                self.fallbackState = nil
                screen = nil
            } else {
                self.fallbackState = nil
                self.operationStatus.status = .failed(nil)
                screen = nil
            }
        }
    }
    
    func makeTransferUsingFallback() {
        if let fallbackState = fallbackState {
            switch fallbackState {
            case .transfer(var transfer):
                transfer.useFallback = true
                
                makeTransfer(transfer: transfer)
            case .pendingTransfers(let pendingTransfers, let approve):
                if (approve) {
                    approveSelectedTransfers(
                        ids: pendingTransfers,
                        fallback:Fallback(
                            useFallback:true,
                            smsCode: nil,
                            pin: nil)
                    )
                } else {
                    rejectSelectedTransfer(
                        ids: pendingTransfers,
                        fallback:Fallback(
                            useFallback: true,
                            smsCode: nil,
                            pin: nil
                        )
                    )
                }
            case .utilityBills(let bills, let account):
                pay(
                    utilityBills: bills,
                    withAccount: account,
                    fallback:Fallback(
                        useFallback: true,
                        smsCode: nil,
                        pin: nil
                    )
                )
            default:
                print("PENDING") // TODO: Not Implemented
            }
        }
        
    }
    
    func makeTransferUsingFallback(otp:String, pin:String?) {
        if let fallbackState = fallbackState {
            switch fallbackState {
            case .transfer(var transfer):
                transfer.useFallback = true
                transfer.fallbackSmsCode = otp
                transfer.fallbackPin = pin
                
                makeTransfer(transfer: transfer)
            case .pendingTransfers(let pendingTransfers, let approve):
                if (approve) {
                    approveSelectedTransfers(
                        ids: pendingTransfers,
                        fallback:Fallback(
                            useFallback: true,
                            smsCode: otp,
                            pin: pin)
                    )
                } else {
                    rejectSelectedTransfer(
                        ids: pendingTransfers,
                        fallback:Fallback(
                            useFallback: true,
                            smsCode: otp,
                            pin: pin
                        )
                    )
                }
            case .utilityBills(let bills, let account):
                pay(
                    utilityBills: bills,
                    withAccount: account,
                    fallback:Fallback(
                        useFallback: true,
                        smsCode: otp,
                        pin: pin
                    )
                )
            case .createTransfer(let transferCreateResponse):
                confirmTransferUsing(otp: otp, transferCreateResponse: transferCreateResponse)
            }
        }
    }
    
    func approveSelectedTransfers(
        ids:[String],
        fallback:Fallback? = nil
    ) -> Void {
        
        operationStatus.status = .waiting
        
        PendingTransfersService().approveTransfers(
            transferIds: ids, fallback: fallback) { error, approved in
                if let error = error {
                    if let networkError = error as? NetworkError {
                        Logger.E(tag: APP_NAME, networkError.localizedDescription)
                    } else if let _ = error as? ServerFailierError {
                        self.operationStatus.status = .failed(nil)
                        self.fallbackState = nil
                    } else if let serverError = error as? ServerError {
                        switch serverError.error.target {
                            //TODO: make them enum
                        case "errFallBackAgreement", "scaExpired", "errScaError":
                            self.operationStatus.status = .otpAvailabe
                            self.fallbackState = .pendingTransfers(ids, true)
                        case "errInvalidSms":
                            self.operationStatus.status = .wrongOtp
                        case "errNoFallBack":
                            self.operationStatus.status = nil
                            self.fallbackState = nil
                        case "scaCancelled":
                            self.operationStatus.status = .rejected
                            self.fallbackState = nil
                        default:
                            self.operationStatus.status = .failed(nil)
                            self.fallbackState = nil
                            Logger.E(tag: APP_NAME, "unhandaled error login status")
                            self.fallbackState = nil
                        }
                        Logger.E(tag: APP_NAME, serverError.localizedDescription)
                    }
                } else {
                    if let result = approved {
                        switch result.status {
                        case "waiting_for_fallback":
                            if(result.fallback.usePin) {
                                self.operationStatus.status = .waitingOtpWithPin
                            } else {
                                self.operationStatus.status = .waitingOtp
                            }
                        case "success":
                            self.operationStatus.status = .success
                            self.fallbackState = nil
                        default:
                            self.fallbackState = nil
                            Logger.E(tag: APP_NAME,  "unhendaled status \(result.status)")
                        }
                        print(result)
                    }else{
                        self.fallbackState = nil
                        self.operationStatus.status = .failed(nil)
                        Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                    }
                }
            }
    }
    
    func rejectSelectedTransfer(
        ids:[String],
        fallback:Fallback? = nil
    ) -> Void {
        
        operationStatus.status = .waiting
        
        PendingTransfersService().rejectTransfers(
            transferIds: ids, fallback: fallback) { error, rejected in
                if let error = error {
                    if let networkError = error as? NetworkError {
                        Logger.E(tag: APP_NAME, networkError.localizedDescription)
                    } else if let _ = error as? ServerFailierError {
                        self.operationStatus.status = .failed(nil)
                        self.fallbackState = nil
                    } else if let serverError = error as? ServerError {
                        switch serverError.error.target {
                            //TODO: make them enum
                        case "errFallBackAgreement", "scaExpired", "errScaError":
                            self.operationStatus.status = .otpAvailabe
                            self.fallbackState = .pendingTransfers(ids, false)
                        case "errInvalidSms":
                            self.operationStatus.status = .wrongOtp
                        case "errNoFallBack":
                            self.operationStatus.status = nil
                            self.fallbackState = nil
                        case "scaCancelled":
                            self.operationStatus.status = .rejected
                            self.fallbackState = nil
                        default:
                            self.operationStatus.status = .failed(nil)
                            self.fallbackState = nil
                            Logger.E(tag: APP_NAME, "unhandaled error login status")
                        }
                        Logger.E(tag: APP_NAME, serverError.localizedDescription)
                    }
                } else {
                    if let result = rejected {
                        switch result.status {
                        case "waiting_for_fallback":
                            if(result.fallback.usePin) {
                                self.operationStatus.status = .waitingOtpWithPin
                            } else {
                                self.operationStatus.status = .waitingOtp
                            }
                        case "success":
                            self.operationStatus.status = .success
                            self.fallbackState = nil
                        default:
                            self.operationStatus.status = .failed(nil)
                            Logger.E(tag: APP_NAME,  "unhendaled status \(result.status)")
                        }
                        print(result)
                    }else{
                        Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                        self.operationStatus.status = .failed(nil)
                        self.fallbackState = nil
                    }
                }
            }
    }
    
    func pay(
        utilityBills ids:[String],
        withAccount selectedAccount:Account,
        fallback:Fallback? = nil
    ) {
        operationStatus.status = .waiting
        UtilityBillsService().approveUtilityBills(
            billPayments:ids,
            sourceAccount:selectedAccount,
            fallback: fallback
        ) { error, utilityBills in
            if let error = error {
                if let networkError = error as? NetworkError {
                    Logger.E(tag: APP_NAME, networkError.localizedDescription)
                } else if let serverError = error as? ServerError {
                    switch serverError.error.target {
                        //TODO: make them enum
                    case "errFallBackAgreement", "scaExpired", "errScaError":
                        self.operationStatus.status = .otpAvailabe
                        self.fallbackState = .utilityBills(ids, selectedAccount)
                    case "errInvalidSms":
                        self.operationStatus.status = .wrongOtp
                    case "errNoFallBack":
                        self.operationStatus.status = nil
                        self.fallbackState = nil
                    case "scaCancelled":
                        self.operationStatus.status = .rejected
                        self.fallbackState = nil
                    case "errBillsDuplicatedTransaction":
                        self.operationStatus.status = .failed("duplicated_transaction")
                        self.fallbackState = nil
                    case "errBillDueAmountRetrieval":
                        self.operationStatus.status = .failed("insufficient_funds")
                        self.fallbackState = nil
                    default:
                        self.operationStatus.status = .failed(nil)
                        self.fallbackState = nil
                        Logger.E(tag: APP_NAME, "unhandaled error login status")
                    }
                    Logger.E(tag: APP_NAME, serverError.localizedDescription)
                }
            } else {
                if let result = utilityBills {
                    switch result.status {
                    case "waiting_for_fallback":
                        if(result.usePin) {
                            self.operationStatus.status = .waitingOtpWithPin
                        } else {
                            self.operationStatus.status = .waitingOtp
                        }
                    case "success":
                        self.operationStatus.status = .success
                        self.fallbackState = nil
                    default:
                        Logger.E(tag: APP_NAME,  "unhendaled status \(result.status)")
                        self.operationStatus.status = .failed(nil)
                    }
                    print(result)
                }else{
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }
    
    func validateTransfer(params: Transfer, completionHandler: @escaping (_ error: AuthenticationError?, _ transferResponse: TransferValidateResponse?) -> Void) {
        TransferService().validateTransfer(params: params) { error, transferResponse in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
                completionHandler(error, nil)
            } else {
                if let tranferResult = transferResponse {
                    completionHandler(nil, tranferResult)
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                    completionHandler(error, nil)
                }
            }
        }
    }
    
    func validateTransfer(transfer:Transfer) async throws -> TransferValidateResponse {
        do {
            async let transferValidation = try TransferService().validateTransfer(transfer: transfer)
            return try await transferValidation
        } catch {
            throw error
        }
        
    }
}

enum TransferError: String {
    case scaCancelled
    case errFallBackAgreement
    case scaExpired
    case errScaError
    case errInvalidSms
    case errNoFallBack
    case otp
}
