package tl.bnctl.banking.data.accounts.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Balance(
    val current: Double,
    val available: Double,
    val blocked: Double,
    val opening: Double,
    val overdraft: Double
) : Parcelable
