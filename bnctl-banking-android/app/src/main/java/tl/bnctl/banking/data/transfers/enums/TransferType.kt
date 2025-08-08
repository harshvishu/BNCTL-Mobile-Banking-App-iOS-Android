package tl.bnctl.banking.data.transfers.enums

enum class TransferType(val type: String) {
    BETWEENACC("betweenacc"), // Between Own Accounts
    INTRABANK("intrabank"), // Same Bank
    INTERBANK("interbank"), // National
    INTERNATIONAL("international"), // International
    UNKNOWN("UNKNOWN"); // Dunno

    companion object {
        fun of(transferType: String): TransferType {
            for (value in values()) {
                if (transferType == value.toString().lowercase()) {
                    return value;
                }
            }
            return UNKNOWN
        }
    }
}