package tl.bnctl.banking.data.cards.model

data class NewDebitCardRequest(
    var accountIban: String,
    var locationId: String,
    var embossName: String,
    var cardProductCode: String,
    var cardProductName: String,
    var statementOnDemand: Boolean,
    var statementOnEmail: Boolean,
    var statementEmail: String,
)