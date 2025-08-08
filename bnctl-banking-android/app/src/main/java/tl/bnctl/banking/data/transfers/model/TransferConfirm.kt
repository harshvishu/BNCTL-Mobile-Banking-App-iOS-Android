package tl.bnctl.banking.data.transfers.model

data class TransferConfirm(
    var transferId: String,
    var status: TransferStatus,
    var processingResult: String,
    var transferIdIssuer: String,
    var transferIdMerchant: String,
    var transferIdAcquirer: String,
    var transferIdLedger: String,
    var transactionType: String,
    var usePin: Boolean = false,
)
