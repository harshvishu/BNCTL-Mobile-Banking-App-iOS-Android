package tl.bnctl.banking.data.accounts.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    var accountId: String,
    var accountTypeDescription: String,
    var product: Product,
    var beneficiary: Beneficiary,
    var balance: Balance,
    var currencyName: String,
    var accountName: String,
    var accountNumber: String,
    var iban: String,
    var swift: String,
    var ownerId: String,
) : Parcelable