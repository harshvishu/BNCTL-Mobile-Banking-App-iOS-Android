package tl.bnctl.banking.data.fallback

data class FallbackRequestParams(
    var useFallback: Boolean,
    var smsCode: String?,
    var pin: String?
)
