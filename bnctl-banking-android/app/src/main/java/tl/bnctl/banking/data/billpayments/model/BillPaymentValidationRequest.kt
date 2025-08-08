package tl.bnctl.banking.data.billpayments.model

data class BillPaymentValidationRequest(
    var sourceAccountId: String,
    var sourceAccount: String,
    var paymentAmount: String,
    var paymentCurrency: String,
    var executionDate: String,
    var executionTime: String
)
