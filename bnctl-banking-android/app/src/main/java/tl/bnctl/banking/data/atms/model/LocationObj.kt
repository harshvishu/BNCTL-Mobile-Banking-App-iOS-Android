package tl.bnctl.banking.data.atms.model

data class LocationObj(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val nameBg: String,
    val nameEn: String,
    val address: String
)