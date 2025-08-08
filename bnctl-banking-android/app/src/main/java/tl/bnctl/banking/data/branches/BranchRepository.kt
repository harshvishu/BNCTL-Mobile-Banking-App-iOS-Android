package tl.bnctl.banking.data.branches

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.branches.model.Branch

class BranchRepository(private val dataSource: BranchDataSource) {
    suspend fun fetchBranches(): Result<List<Branch>> {
        return dataSource.fetchBranches()
    }

}