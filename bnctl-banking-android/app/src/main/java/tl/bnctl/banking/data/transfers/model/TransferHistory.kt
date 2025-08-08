package tl.bnctl.banking.data.transfers.model

import tl.bnctl.banking.data.accounts.model.StatementsDataHolder

data class TransferHistory(
    var valueDate: String?,
    var transferDate: String,
    var issuerName: String,
    override var transferId: String,
    override var sourceAccount: String,
    override var destinationAccount: String?,
    override var beneficiary: String?,
    var description: String,
    var processType: String?,
    override var status: String,
    override var amount: String,
    override var currency: String,
    var beneficiaryBankName: String,
    var beneficiaryBankSwiftCode: String,
    override var transferDirection: String,
    override var transferIdIssuer: String?
) : StatementsDataHolder {
    override var dateOfExecution: String?
        get() = transferDate
        set(value) {}
    override var reason: String
        get() = description
        set(value) {}
    override var additionalInfo: String
        get() = ""
        set(value) {}
    override var transactionType: String
        get() = ""
        set(value) {}
}
