package tl.bnctl.banking.ui.banking.fragments.services

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.ui.BaseFragment

abstract class BasePermissionFragment : BaseFragment() {

    private val keyToViewId = HashMap<String, String>()

    private lateinit var permissionsViewModel: PermissionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionsViewModel = ViewModelProvider(this, PermissionsViewModelFactory())[PermissionsViewModel::class.java]
    }

    fun setupViewsToPermission(ketToViewId: Map<String, String>, container: ViewGroup) {
        keyToViewId.putAll(ketToViewId)
        addObserverForViewFiltering(container)
        permissionsViewModel.registerViews(keyToViewId.keys.toList())
    }

    private fun addObserverForViewFiltering(container: ViewGroup) {
        permissionsViewModel.menuViews.observe(viewLifecycleOwner) {
            it.entries.forEach { entry ->
                val view = container.findViewById<View>(keyToViewId[entry.key]!!.toInt())
                if (entry.value){
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyToViewId.clear()
    }
}
