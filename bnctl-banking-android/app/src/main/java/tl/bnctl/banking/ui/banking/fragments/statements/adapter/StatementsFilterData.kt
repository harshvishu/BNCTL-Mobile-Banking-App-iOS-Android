package tl.bnctl.banking.ui.banking.fragments.statements.adapter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tl.bnctl.banking.BuildConfig
import java.util.*

@Parcelize
data class StatementsFilterData(
    val startDate: Date,
    val endDate: Date,
    val pageNumber: Int = 1,
    val pageSize: Int = BuildConfig.STATEMENTS_PAGINATION_PAGE_SIZE
) : Parcelable
