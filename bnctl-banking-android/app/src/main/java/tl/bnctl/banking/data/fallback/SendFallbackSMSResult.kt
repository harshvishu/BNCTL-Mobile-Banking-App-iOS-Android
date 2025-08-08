package tl.bnctl.banking.data.fallback

data class SendFallbackSMSResult(
    var smsSent: Boolean,
    var usePin: Boolean
)