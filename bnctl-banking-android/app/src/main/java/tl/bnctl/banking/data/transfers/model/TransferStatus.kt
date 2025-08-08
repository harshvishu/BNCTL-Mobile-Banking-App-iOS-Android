package tl.bnctl.banking.data.transfers.model

import com.google.gson.annotations.SerializedName

enum class TransferStatus {
    @SerializedName("pending")
    PENDING,

    @SerializedName("rejected")
    REJECTED,

    @SerializedName("cancelled")
    CANCELLED,

    @SerializedName("processing")
    PROCESSING,

    @SerializedName("success")
    SUCCESS,

    @SerializedName("stored")
    STORED,

    @SerializedName("scheduled")
    SCHEDULED,

    @SerializedName("error")
    ERROR,

    @SerializedName("failure")
    FAILURE,

    @SerializedName("reversed")
    REVERSED,

    @SerializedName("waiting_for_fallback")
    WAITING_FOR_FALLBACK_CONFIRMATION,
}