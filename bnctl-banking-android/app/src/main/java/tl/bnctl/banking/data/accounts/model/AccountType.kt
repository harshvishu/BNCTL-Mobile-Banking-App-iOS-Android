package tl.bnctl.banking.data.accounts.model

import com.google.gson.annotations.SerializedName

enum class AccountType {
    @SerializedName("operational")
    OPERATIONAL,

    @SerializedName("card")
    CREDIT_CARD,

    @SerializedName("savings")
    SAVINGS
}