package tl.bnctl.banking.data.payee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tl.bnctl.banking.data.accounts.model.Beneficiary

@Parcelize
data class Payee(
    var beneficiary: Beneficiary,
    var accountNumber: String,
    var balance: Double? = null,
    var currencyName: String? = null
) : Parcelable
