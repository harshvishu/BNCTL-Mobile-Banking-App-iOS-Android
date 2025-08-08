package tl.bnctl.banking.util

class Constants {
    companion object {
        const val AMOUNT_FORMAT_SHORT = "######"
        const val SESSION_EXPIRED_CODE = "AccessDeniedException"

        const val ERROR_FALLBACK_AGREEMENT = "errFallBackAgreement"

        const val ERROR_FALLBACK_INVALID_SMS = "errInvalidSms"
        const val ERROR_SCA_EXPIRED = "scaExpired"
        const val ERROR_SCA_ERROR = "scaError"
        const val ERROR_USE_FALLBACK = "errUseFallBack"
        const val ERROR_NO_CONNECTION = "errNoConnection"
        const val ERROR_INVALID_USERNAME = "invalidUsername"
        const val ERROR_INVALID_PASSWORD = "invalidPassword"
        const val ERROR_INVALID_PHONE_NUMBER = "invalidPhoneNumber"

        const val LAST_KNOWN_RECOMMENDED_APP_VERSION_CODE = "lastKnownRecommendedAppVersionCode"
    }
}