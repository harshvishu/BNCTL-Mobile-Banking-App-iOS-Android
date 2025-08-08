package tl.bnctl.banking.data.cashWithdrawal.model

data class CashWithdrawalRequest(
    var amount: String,
    var currency: String,
    var executionDate: String,
    var branch: String,
    var description: String
)
