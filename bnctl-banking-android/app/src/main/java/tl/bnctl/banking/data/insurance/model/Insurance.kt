package tl.bnctl.banking.data.insurance.model

data class Insurance(
    val policy: String,
    val dueDate: String,
    val amount: String,
    val currency: String,
    val iban: String?,
    val amountBgn: String,
    val ibanBgn: String,
    val insurer: String,
    val insuranceAgencyName: String,
    val reason: String,
    val billNumber: String?
)