package tl.bnctl.banking.data.news.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class News(
    var id: String,
    var date: Date,
    var title: String?,
    var text: String,
    var url: String?
) : Parcelable