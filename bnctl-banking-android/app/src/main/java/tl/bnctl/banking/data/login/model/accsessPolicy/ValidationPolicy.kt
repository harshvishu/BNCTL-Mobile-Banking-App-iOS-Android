package tl.bnctl.banking.data.login.model.accsessPolicy

data class ValidationPolicy(
    var charMin: Int,
    var charMax: Int,
    var regexInfo: String,
    var regex: String,
)
