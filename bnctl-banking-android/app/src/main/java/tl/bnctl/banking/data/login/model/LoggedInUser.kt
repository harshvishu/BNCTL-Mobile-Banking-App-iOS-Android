package tl.bnctl.banking.data.login.model

data class LoggedInUser(
    val customerData: CustomerData,
    val tokenData: TokenData,
    val authData: AuthData
)