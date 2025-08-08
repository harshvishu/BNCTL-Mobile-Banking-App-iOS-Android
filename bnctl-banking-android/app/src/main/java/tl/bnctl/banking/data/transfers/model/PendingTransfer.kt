package tl.bnctl.banking.data.transfers.model

data class PendingTransfer(
    var transferId: String,
    var description: String,
    var beneficiary: String,
    var beneficiaryName: String,
    var beneficiaryBankName: String,
    var beneficiaryBankSwiftCode: String,
    var currency: String,
    var amount: String,
    var sourceAccount: String,
    var issuerName: String,
    var destinationAccount: String,
    var status: String,
    var transactionType: String,
    var isSelected: Boolean = false,
    var numberOfDocuments: Int
)