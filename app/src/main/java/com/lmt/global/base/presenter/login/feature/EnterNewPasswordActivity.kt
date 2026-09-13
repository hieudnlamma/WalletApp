package com.lmt.global.base.presenter.login.feature

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityEnterNewPasswordBinding
import com.lmt.global.base.extension.TopNotificationType
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.extension.showTemporaryError
import com.lmt.global.base.extension.showTopNotification
import com.lmt.global.base.extension.statusBars
import com.lmt.global.base.presenter.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnterNewPasswordActivity :
    IActivity<ActivityEnterNewPasswordBinding, CommonViewModel>() {

    override fun provideViewModel() = viewModel<CommonViewModel>()

    override fun provideLayout() = R.layout.activity_enter_new_password

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
    }

    override fun initListeners() = with(viewBinding) {
        btnBack.onDebounceClick { onBackPressedDispatcher.onBackPressed() }
        ivToggleNewPassword.onDebounceClick {
            togglePasswordVisibility(edtNewPassword, ivToggleNewPassword)
        }
        ivToggleConfirmNewPassword.onDebounceClick {
            togglePasswordVisibility(edtConfirmNewPassword, ivToggleConfirmNewPassword)
        }
        btnLogin.onDebounceClick { submitNewPassword() }
    }

    private fun submitNewPassword() = with(viewBinding) {
        val newPassword = edtNewPassword.text.toString()
        val confirmPassword = edtConfirmNewPassword.text.toString()

        when {
            newPassword.length < MIN_PASSWORD_LENGTH -> {
                edtNewPassword.showTemporaryError(getString(R.string.invalid_password))
            }

            confirmPassword != newPassword -> {
                edtConfirmNewPassword.showTemporaryError(
                    getString(R.string.passwords_do_not_match)
                )
            }

            else -> {
                showTopNotification(
                    message = getString(R.string.password_changed_successfully),
                    type = TopNotificationType.SUCCESS,
                )

                startActivity(
                    Intent(this@EnterNewPasswordActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
        }
    }

    private fun togglePasswordVisibility(input: EditText, icon: ImageView) {
        val showPassword = input.transformationMethod is PasswordTransformationMethod
        input.transformationMethod = if (showPassword) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        icon.setImageResource(
            if (showPassword) R.drawable.icn_eye_on_line else R.drawable.icn_eye_off_line
        )
        input.setSelection(input.text?.length ?: 0)
    }

    private fun setupInsets() {
        setupApplyWindowInsetListener { insets ->
            viewBinding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.statusBars().top
            }
        }
        ViewCompat.requestApplyInsets(window.decorView)
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}
