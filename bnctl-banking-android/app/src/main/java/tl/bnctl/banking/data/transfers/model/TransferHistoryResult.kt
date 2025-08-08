package tl.bnctl.banking.data.transfers.model

import tl.bnctl.banking.data.common.Pagination

data class TransferHistoryResult(
    var records: List<TransferHistory>,
    var pagination: Pagination
)
