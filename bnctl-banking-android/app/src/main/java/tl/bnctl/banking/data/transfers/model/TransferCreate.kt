package tl.bnctl.banking.data.transfers.model

data class TransferCreate(
    var validationRequestId: String,
    var validationStatus: String,
    var tenantId: String,
    var serviceId: String,
    var actionId: String,
    var objectId: String,
    var channel: String,
    var recipient: String,
    var authorizationType: String,
)
