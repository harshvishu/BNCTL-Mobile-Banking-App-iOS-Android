package tl.bnctl.banking.data.templates.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Template(
    var payeeId: String,
    //var amount: Double?, // Standard banking templates don't have an amount
    var description: String?,
    var sourceAccount: String?,
    var accountNumber: String,
    var currency: String,
    var name: String, // Destination account holder name
    var transactionType: String?,
    var transferType: String?,

    var type: String, // Payee type - same bank ("bank"), national, international, etc.
    var additionalDetails: @RawValue Map<String, Any>?,
    // Currently unused. Will be used when transfer types different than "same bank" are added
    var country: String?,
    var address: String?,
    var city: String?,
    var walletAccountNumber: String?,
    var userId: String?,
    var walletProvider: String?,
    var bank: String?,
    var notificationLanguage: String?,
    var accountTypeId: String?,
    var isDeleted: String?,
    var email: String?,
    var swift: String?,
) : Parcelable
