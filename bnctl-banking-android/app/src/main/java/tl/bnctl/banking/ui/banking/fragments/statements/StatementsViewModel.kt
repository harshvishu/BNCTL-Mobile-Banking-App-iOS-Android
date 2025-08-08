package tl.bnctl.banking.ui.banking.fragments.statements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.accounts.model.StatementsDataHolder
import tl.bnctl.banking.data.common.Pagination
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsFilterData
import java.util.*

class StatementsViewModel: ViewModel() {

    private val _allStatements = MutableLiveData<List<StatementsDataHolder?>>()
    private val _pagination = MutableLiveData<Pagination>()
    private val _statementsPage =
        MutableLiveData<List<StatementsDataHolder?>>() // A *single* statements page. This is used for the pagination of statements. The result from this is added to the reyclerview's data
    private val _statementsFilter: MutableLiveData<StatementsFilterData> by lazy {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        with(calendar) {
            set(Calendar.HOUR, 23);
            set(Calendar.MINUTE, 59);
            set(Calendar.SECOND, 59);
        }
        val mutableLiveData = MutableLiveData<StatementsFilterData>(
            StatementsFilterData(
                Date(), calendar.time, 1, BuildConfig.STATEMENTS_PAGINATION_PAGE_SIZE
            )
        )
        mutableLiveData
    }

    private val _shouldFetchStatements = MutableLiveData(false)
    private val _shouldFetchNextStatementsPage =
        MutableLiveData(false) // Set to true when next statements page is needed. Used in outbound transfers RecyclerView
    private val _shouldViewStatementProperty = MutableLiveData(ViewStatementProperty.BENEFICIARY)
    private val _isDetailedViewEnabled =
        MutableLiveData(true) // FIXME: This should be concern of visualisation, not data model
    private val _fetchingStatements = MutableLiveData(false)
    private val _loadMoreButtonVisible = MutableLiveData(false)
    private val _detailedViewTitleResource =
        MutableLiveData(R.string.statements_details_title) // FIXME: This should be concern of visualisation, not data model

    val allStatements: LiveData<List<StatementsDataHolder?>> = _allStatements
    val pagination: LiveData<Pagination> = _pagination
    val statementsPage: LiveData<List<StatementsDataHolder?>> = _statementsPage
    val statementsFilter: LiveData<StatementsFilterData> = _statementsFilter
    val shouldFetchStatements: LiveData<Boolean> = _shouldFetchStatements
    val fetchingStatements: LiveData<Boolean> = _fetchingStatements
    val shouldFetchNextStatementsPage: LiveData<Boolean> = _shouldFetchNextStatementsPage
    val shouldViewStatementProperty: LiveData<ViewStatementProperty> = _shouldViewStatementProperty
    val isDetailedViewEnabled: LiveData<Boolean> = _isDetailedViewEnabled
    val detailedViewTitleResource: LiveData<Int> = _detailedViewTitleResource
    val loadMoreButtonVisible: LiveData<Boolean> = _loadMoreButtonVisible

    fun setFilter(filter: StatementsFilterData) {
        _statementsFilter.value = filter
    }

    fun raiseShouldFetchStatementsFlag() {
        _shouldFetchStatements.value = true
    }

    fun dropShouldFetchStatementsFlag() {
        _shouldFetchStatements.value = false
    }

    fun raiseShouldFetchNextStatementsPageFlag() {
        _shouldFetchNextStatementsPage.value = true
    }

    fun dropShouldFetchNextStatementsPageFlag() {
        _shouldFetchNextStatementsPage.value = false
    }

    fun raiseFetchingStatementsFlag() {
        _fetchingStatements.value = true
    }

    fun dropFetchingStatementsFlag() {
        _fetchingStatements.value = false
    }

    fun setAccountStatements(result: List<StatementsDataHolder?>) {
        _allStatements.value = result
    }

    fun setPagination(pagination: Pagination) {
        _pagination.value = pagination
    }

    fun addAccountStatementsPage(result: List<StatementsDataHolder?>) {
        _statementsPage.value = result
    }

    fun disableDetailedView() {
        _isDetailedViewEnabled.value = false
    }

    fun enableDetailedView() {
        _isDetailedViewEnabled.value = true
    }

    fun showProperty(prop: ViewStatementProperty) {
        _shouldViewStatementProperty.value = prop
    }

    fun showLoadMoreButton() {
        _loadMoreButtonVisible.postValue(true)
    }

    fun hideLoadMoreButton() {
        _loadMoreButtonVisible.postValue(false)
    }

}