package tl.bnctl.banking.ui.onboarding

import android.os.Bundle
import androidx.navigation.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.ActivityOnboardingBinding
import tl.bnctl.banking.ui.BaseActivity

/**
 * Onboarding Activity, that leads the user through a series of steps.
 * Each step is a separate Fragment:
 * 1. Intro Fragment - shows the user overview of the process
 * 2. Terms and Conditions Fragment - presents the user Terms and Conditions and forces them to accept
 * 3. Login Fragment - self-explanatory - login
 */
class OnboardingActivity : BaseActivity() {

    companion object {
        const val FLAG_DIRECT_TO_LOGIN = "directToLogin"
    }

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findNavController(R.id.nav_host_fragment_activity_onboarding)
    }
}
