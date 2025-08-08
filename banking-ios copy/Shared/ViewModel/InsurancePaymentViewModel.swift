//
//  InsurancePaymentViewModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 14/12/21.
//

import Foundation

class InsurancePaymentViewModel: ObservableObject {
    
    struct Amount: Equatable, Identifiable {
        let amount: String
        let currency: String
        let iban:String
        var id: String { self.currency }
    }
    
    @Published var isLoading: Bool = false
    
    @Published var insurances: [InsuranceData] = []
    @Published var insurance: InsuranceData? = nil {
        didSet {
            amounts = createAmountList()
        }
    }
    
    @Published var sourceAccount: Account?
    @Published var selectingSourceAccount = false
    @Published var selectedAmount: Amount? = nil
    
    @Published var status: OperationStatus? = nil
    
    var amounts: [Amount] = []
    
    init(_ isLocal: Bool = false) {
        if (isLocal) {
            insurances = InsuranceData.listPreview
            insurance = InsuranceData.preview
        }
    }
    
    func fetchInsurances() {
        isLoading = true
        InsuranceService().getInsurance { error, insuranceResponse in
            self.isLoading = false
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            } else {
                if let insurenceResult = insuranceResponse {
                    self.insurances = insurenceResult
                } else {
                    Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
                }
            }
        }
    }
    
    func createAmountList() -> [Amount] {
        var arr = [Amount]()
        if let insurance = insurance {
            arr.append(
                Amount(
                    amount: insurance.amount,
                    currency: insurance.currency,
                    iban: insurance.iban ?? insurance.ibanBgn!
                )
            )
            
            if insurance.currency != "BGN", let bgn = insurance.amountBgn {
                arr.append(
                    Amount(
                        amount: bgn,
                        currency: "BGN",
                        iban: insurance.ibanBgn!
                    )
                )
                
            }
        }
        return arr
    }
    
    func createTransferObject() -> Transfer {
        let selectedAmount = selectedAmount!
        let insurance = insurance!
        let account = sourceAccount as! Account
        
        var transferType:TransferType = .intrabank
        var additionalDetails:TransferDetails? = nil
        
        if selectedAmount.currency == insurance.currency
        {
            transferType = .interbank
            additionalDetails = TransferDetails(
                transactionType: .intrabankCurrencyTransfer,
                orderType: nil,
                rateType: nil,
                offerId: nil)
        }
        
        return Transfer(
            amount:selectedAmount.amount,
            description: "BILL#\(insurance.reason!)",
            additionalDescription: "POLICY#\(insurance.policy)",
            destinationAccount: selectedAmount.iban,
            destinationAccountCurrency: selectedAmount.currency,
            recipientName: insurance.insuranceAgencyName,
            sourceAccount: account.account,
            sourceAccountId: account.accountId,
            sourceAccountCurrency: account.currency,
            transferType: transferType,
            executionType: "now",
            additionalDetails: additionalDetails,
            useFallback: nil,
            fallbackSmsCode: nil,
            fallbackPin: nil
        )
    }
}
