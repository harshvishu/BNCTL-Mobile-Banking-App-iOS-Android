package tl.bnctl.banking.data.billers.model

data class UtilityBill(
    var userBillerId: String,
    var biller: Biller,
    var userId: String,
    var name: String,
    var clientReference: String,
    var billAmount: String,
    var currencyName: String,
    var status: String,
    var isSelected: Boolean = false
)
