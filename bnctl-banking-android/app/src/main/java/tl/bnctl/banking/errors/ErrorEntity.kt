package tl.bnctl.banking.errors

sealed class ErrorEntity {
    object Network : ErrorEntity()
    object ServiceUnavailable : ErrorEntity()
    object Unknown : ErrorEntity()
}