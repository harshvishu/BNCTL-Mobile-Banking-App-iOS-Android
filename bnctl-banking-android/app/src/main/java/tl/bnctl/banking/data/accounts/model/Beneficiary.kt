package tl.bnctl.banking.data.accounts.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Beneficiary(val name: String) : Parcelable
