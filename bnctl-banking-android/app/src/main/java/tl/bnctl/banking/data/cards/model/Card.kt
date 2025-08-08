package tl.bnctl.banking.data.cards.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    var accountId: String?,
    var cardNumber: String?,
    var cardBIN: String?,
    var cardIIN: String?,
    var cardType: String?, // debit or credit
    var cardProduct: String?,
    var cardProductCode: String?,
    var cardAccountNumber: String?, // the account number, associated with this card
    var cardPrintName: String?,
    var cardStatus: String?,
    var expiryDate: String?,
    var currency: String?,
    var cardProductLabel: String?,
    var accountBalance: Double,
    var availableBalance: Double,
    val blockedAmount: Double,
    val approvedOverdraft: Double

) : Parcelable
