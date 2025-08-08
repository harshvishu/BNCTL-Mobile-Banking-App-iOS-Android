package tl.bnctl.banking.data.transfers.model

data class TransferValidate(
    var canCreateTransfer: Boolean,
    var status: String,
    var message: String,
    var chargeAmount: Number,
    var chargeCurrency: String,
    var destinationCustomerName: String?
)