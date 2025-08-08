package tl.bnctl.banking.data.cards.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CardStatementRequest(
    var cardAccountNumber: String?,
    var dateFrom: String?,
    var dateTo: String?
) : Parcelable
