package tl.bnctl.banking.data.accounts.model

data class AccountStatement(
    override var transferId: String,
    override var amount: String,
    var debitAmount: Double,
    var creditAmount: Double,
    override var beneficiary: String?,
    var description: String,
    var valueDate: String,
    var transferDate: String,
    var balance: Double,
    var closingBalance: Double,
    override var currency: String,
    override var sourceAccount: String,
    override var destinationAccount: String?,
    override var transactionType: String,
    override var transferDirection: String,
    override var transferIdIssuer: String?
) : StatementsDataHolder {
    override var dateOfExecution: String?
        get() {
            return transferDate
        }
        set(value) {}
    override var status: String
        get() = ""
        set(value) {}
    override var reason: String
        get() = description
        set(value) {}
    override var additionalInfo: String
        get() = ""
        set(value) {}

    override fun hashCode(): Int {
        return super.hashCode()
    }
}