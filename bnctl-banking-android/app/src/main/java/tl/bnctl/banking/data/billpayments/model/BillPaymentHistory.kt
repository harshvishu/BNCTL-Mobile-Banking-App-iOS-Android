package tl.bnctl.banking.data.billpayments.model

import tl.bnctl.banking.data.accounts.model.StatementsDataHolder

data class BillPaymentHistory(
    var billPaymentId: String,
    var billerId: String,
    override var sourceAccount: String,
    override var transferId: String,
    var billPaymentDate: String,
    var paymentProcessedDateTime: String,
    override var amount: String,
    override var currency: String,
    var reference: String,
    var creatorUserId: String,
    override var status: String,
    var customerText: String,
    var creatorName: String,
    var billerName: String,
    var billerType: String,
    override var destinationAccount: String?,
    var provider: String?,
    override var transferIdIssuer: String?
) : StatementsDataHolder {
    override var beneficiary: String?
        get() = billerName
        set(value) {}
    override var dateOfExecution: String?
        get() = paymentProcessedDateTime
        set(value) {}
    override var reason: String
        get() = customerText
        set(value) {}
    override var additionalInfo: String
        get() = provider.orEmpty()
        set(value) {}
    override var transactionType: String
        get() = ""
        set(value) {}
    override var transferDirection: String
        get() = ""
        set(value) {}
}
