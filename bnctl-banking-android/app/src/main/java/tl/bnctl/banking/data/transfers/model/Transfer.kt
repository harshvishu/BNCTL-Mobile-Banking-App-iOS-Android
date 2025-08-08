package tl.bnctl.banking.data.transfers.model

import tl.bnctl.banking.data.templates.model.TemplateRequest

data class Transfer(
    var amount: String,
    var description: String,
    var destinationAccountCurrency: String,
    var sourceAccount: String,
    var sourceAccountId: String,
    var sourceAccountCurrency: String,
    var destinationAccount: String,
    var destinationAccountHolder: String,
    var transferType: String,
    var executionType: String,
    var additionalDetails: Map<String, Any>,
    var newPayee: TemplateRequest?,
    var transferId: String? = null
)
