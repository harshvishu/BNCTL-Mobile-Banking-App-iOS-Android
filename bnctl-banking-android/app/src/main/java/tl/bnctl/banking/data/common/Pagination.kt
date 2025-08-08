package tl.bnctl.banking.data.common


data class Pagination(
    var pageNumber: Int,
    var pageSize: Int,
    var recordsTotal: Int,
    var totalPages: Int
)