package tl.bnctl.banking.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.ActivityLoginBinding
import tl.bnctl.banking.ui.BaseActivity

/**
 * Login Activity, that contains only a LoginFragment.
 * Used when user has already gone through the First Login flow.
 */
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findNavController(R.id.nav_host_fragment_activity_login)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
