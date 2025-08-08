package tl.bnctl.banking.data.transfers.enums

enum class TransferExecution(val time: String) {
    NOW("now"),
    LATER("later"),
    STANDING_ORDER("standingOrder")
}