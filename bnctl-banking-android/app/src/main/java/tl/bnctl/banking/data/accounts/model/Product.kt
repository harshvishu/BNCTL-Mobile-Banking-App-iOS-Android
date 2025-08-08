package tl.bnctl.banking.data.accounts.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val code: String,
    val name: String,
    val type: String,
    val group: String
) : Parcelable
