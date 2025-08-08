package tl.bnctl.banking.data.login.model

data class AuthData(
    val userId: String,
    val username: String,
    val usernameChanged: Boolean,
    val passwordChanged: Boolean,
    var permissions: List<Permission>,
)