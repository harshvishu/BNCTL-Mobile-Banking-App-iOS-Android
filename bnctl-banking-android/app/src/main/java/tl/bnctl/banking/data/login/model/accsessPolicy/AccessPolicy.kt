package tl.bnctl.banking.data.login.model.accsessPolicy

/**
 * Describes rules for handling usernames and passwords as configured in the IB backend
 */
data class AccessPolicy(
    var usernamePolicy: ValidationPolicy,
    var passwordPolicy: ValidationPolicy
)
