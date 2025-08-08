package tl.bnctl.banking.data.templates.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemplateRequest(
    var type: String?,
    var accountNumber: String?,
    var accountTypeId: String?,
    var name: String?,
    var address: String?,
    var city: String?,
    var country: String?,
    var bank: String?,
    var swift: String?,
    var email: String?,
    var currency: String?,
    var notificationLanguage: String?,
    var saveNewPayee: Boolean?
) : Parcelable