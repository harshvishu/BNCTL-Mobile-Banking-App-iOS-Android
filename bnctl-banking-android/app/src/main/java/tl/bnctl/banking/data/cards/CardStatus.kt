package tl.bnctl.banking.data.cards

enum class CardStatus(val status: String) {
    ACTIVE("active"),
    BLOCKED("blocked"),
    DEACTIVATED("deactivated"),
    PRODUCED_NOT_RECEIVED("produced_not_received"),
    UNKNOWN("unknown");

    companion object {
        fun fromString(status: String?): CardStatus {
            for (value in values()) {
                if (value.status == status) {
                    return value;
                }
            }
            return UNKNOWN
        }
    }
}