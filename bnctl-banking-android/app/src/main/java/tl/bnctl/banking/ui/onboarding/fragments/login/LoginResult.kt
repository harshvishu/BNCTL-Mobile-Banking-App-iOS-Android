package tl.bnctl.banking.ui.onboarding.fragments.login

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.login.model.LoggedInUser

data class LoginResult(
    val success: LoggedInUser? = null,
    val error: Result.Error? = null
)