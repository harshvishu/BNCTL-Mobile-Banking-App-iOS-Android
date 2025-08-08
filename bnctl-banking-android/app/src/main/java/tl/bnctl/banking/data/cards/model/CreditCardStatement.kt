package tl.bnctl.banking.data.cards.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreditCardStatement(
    var statementId: String?,
    var cardNumber: String?,
    var fileName: String?,
    var date: String?
) : Parcelable
