package tl.bnctl.banking.data.cards

enum class CardType(val type: String) {
    MASTERCARD("mastercard"),
    VISA("visa"),
    UNKNOWN("unknown")
    ;

    companion object {
        fun fromString(type: String?): CardType {
            for (value in values()) {
                if (value.type == type) {
                    return value;
                }
            }
            return UNKNOWN
        }
    }
}