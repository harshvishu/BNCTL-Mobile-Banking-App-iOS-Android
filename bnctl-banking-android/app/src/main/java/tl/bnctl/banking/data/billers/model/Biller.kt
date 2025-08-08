package tl.bnctl.banking.data.billers.model

data class Biller(
    var billerId: String,
    var name: String,
    var type: String, // Service, Telco
    var subType: String, // Water, Electricity, etc.
    var identifier: String,
    var description: String
)
