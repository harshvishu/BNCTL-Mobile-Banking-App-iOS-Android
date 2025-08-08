package tl.bnctl.banking.data.login.model

class TokenData(
    val accessToken: String?,
    val refreshToken: String?,
    val expiresIn: Int?,
    val refreshTokenExpiresIn: Int?,
    val username: String?,
    val userId: String?,
)