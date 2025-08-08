package tl.bnctl.banking.ui.fallback

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.ActivityFallbackConfirmBinding
import tl.bnctl.banking.ui.BaseActivity
import tl.bnctl.banking.ui.utils.DialogFactory

class FallbackConfirmActivity : BaseActivity() {

    private var _binding: ActivityFallbackConfirmBinding? = null
    private val binding get() = _binding!!

    private var usePin: Boolean = false
    private var wrongSMSCode: Boolean = false
    private var titleTextId: Int = 0
    private var subtitleTextId: Int = 0
    private val fallbackConfirmActivityViewModel: FallbackConfirmActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFallbackConfirmBinding.inflate(layoutInflater)

        usePin = intent.getBooleanExtra(EXTRA_USE_PIN_KEY, false)
        wrongSMSCode = intent.getBooleanExtra(EXTRA_WRONG_SMS_CODE_KEY, false)

        titleTextId = intent.getIntExtra(EXTRA_TITLE_TEXT_KEY, 0)
        subtitleTextId = intent.getIntExtra(EXTRA_SUBTITLE_TEXT_KEY, 0)

        setupUI()
        setContentView(binding.root)
    }

    private fun setupUI() {
        if (wrongSMSCode) {
            DialogFactory.createCancellableDialog(
                this,
                R.string.error_authentication_err_invalid_sms_login
            ).show()
        }

        if (titleTextId != 0) {
            binding.fallbackTitle.text = getString(titleTextId)
        } else if (usePin) {
            binding.fallbackTitle.text =
                getString(R.string.fallback_complete_action_via_sms_code_and_pin)
        }
        if (subtitleTextId != 0) {
            binding.fallbackSubtitle.text = getString(subtitleTextId)
        } else if (usePin) {
            binding.fallbackSubtitle.text = getString(R.string.fallback_enter_sms_code_and_pin)
        }

        if (usePin) {
            binding.fallbackPinLayout.visibility = View.VISIBLE
        } else {
            binding.fallbackPinLayout.visibility = View.GONE
        }

        binding.confirmFallbackButton.setOnClickListener {
            onConfirm()
        }
        binding.toolbar.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        fallbackConfirmActivityViewModel.smsCodeError.observe(this) {
            if (it == null) {
                binding.fallbackSmsCode.error = null
            } else {
                binding.fallbackSmsCode.error = getString(it)
            }
        }

        fallbackConfirmActivityViewModel.pinError.observe(this) {
            if (it == null) {
                binding.fallbackPin.error = null
            } else {
                binding.fallbackPin.error = getString(it)
            }
        }
        binding.fallbackSmsCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                fallbackConfirmActivityViewModel.setSmsCode(text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.fallbackPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                fallbackConfirmActivityViewModel.setPin(text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun onConfirm() {
        val smsCode = binding.fallbackSmsCode.text.toString()
        val pin = binding.fallbackPin.text.toString()

        if (smsCode.isBlank()) {
            fallbackConfirmActivityViewModel.raiseSmsCodeError(R.string.fallback_dialog_error_enter_sms_code)
        } else {
            fallbackConfirmActivityViewModel.clearSmsCodeError()
        }

        if (usePin && pin.isBlank()) {
            fallbackConfirmActivityViewModel.raisePinError(R.string.fallback_dialog_error_enter_pin)
        } else {
            fallbackConfirmActivityViewModel.clearPinError()
        }

        if (fallbackConfirmActivityViewModel.smsCodeError.value == null &&
            fallbackConfirmActivityViewModel.pinError.value == null
        ) {
            val intent = Intent()
            intent.putExtra(EXTRA_SMS_CODE_KEY, smsCode)
            intent.putExtra(EXTRA_PIN_KEY, pin)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_USE_PIN_KEY = "usePin"
        const val EXTRA_SMS_CODE_KEY = "smsCode"
        const val EXTRA_PIN_KEY = "pin"
        const val EXTRA_WRONG_SMS_CODE_KEY = "wrongSmsCode"
        const val EXTRA_TITLE_TEXT_KEY = "titleTextId"
        const val EXTRA_SUBTITLE_TEXT_KEY = "subtitleTextId"
    }
}