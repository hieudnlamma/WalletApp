package com.lmt.global.base.presenter.create_account

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.util.Patterns
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityCreateAccountBinding
import com.lmt.global.base.extension.TopNotificationType
import com.lmt.global.base.extension.showTemporaryError
import com.lmt.global.base.extension.showTopNotification
import com.lmt.global.base.extension.statusBars
import com.lmt.global.base.presenter.create_account.feature.InputOptActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateAccountActivity :
    IActivity<ActivityCreateAccountBinding, CommonViewModel>() {

    override fun provideViewModel() = viewModel<CommonViewModel>()

    override fun provideLayout() = R.layout.activity_create_account

    private var isPasswordVisible = false

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
        setupTermsAndPrivacy()
    }

    override fun initListeners() {
        viewBinding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewBinding.ivTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        viewBinding.btnCreateNewAccount.setOnClickListener {
            createAccount()
        }
    }

    private fun setupInsets() {
        setupApplyWindowInsetListener { insets ->
            viewBinding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.statusBars().top
            }
        }
        ViewCompat.requestApplyInsets(window.decorView)
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        val currentPosition =
            viewBinding.edtPassword.selectionStart

        if (isPasswordVisible) {
            viewBinding.edtPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

            viewBinding.ivTogglePassword.setImageResource(
                R.drawable.icn_eye_on_line
            )
        } else {
            viewBinding.edtPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()

            viewBinding.ivTogglePassword.setImageResource(
                R.drawable.icn_eye_off_line
            )
        }

        viewBinding.edtPassword.setSelection(
            currentPosition.coerceAtLeast(0)
        )
    }

    private fun setupTermsAndPrivacy() {
        val fullText =
            getString(R.string.i_accept_terms_and_conditions_and_privacy_policy)

        val terms =
            getString(R.string.terms_and_conditions)

        val privacy =
            getString(R.string.privacy_policy)

        val spannable = SpannableString(fullText)

        addClickableSpan(
            spannable = spannable,
            fullText = fullText,
            targetText = terms,
        ) {
            openTermsAndConditions()
        }

        addClickableSpan(
            spannable = spannable,
            fullText = fullText,
            targetText = privacy,
        ) {
            openPrivacyPolicy()
        }

        viewBinding.tvTermsAndPrivacy.apply {
            text = spannable
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    private fun addClickableSpan(
        spannable: SpannableString,
        fullText: String,
        targetText: String,
        onClick: () -> Unit,
    ) {
        val start = fullText.indexOf(targetText)

        if (start == -1) return

        val end = start + targetText.length

        spannable.setSpan(
            object : ClickableSpan() {

                override fun onClick(widget: View) {
                    onClick()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(
                        this@CreateAccountActivity,
                        R.color.hues_blue_celtic_blue
                    )

                    ds.isUnderlineText = false
                }
            },
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun openTermsAndConditions() {
        // TODO: Open Terms and Conditions screen
    }

    private fun openPrivacyPolicy() {
        // TODO: Open Privacy Policy screen
    }

    private fun createAccount() {
        val name =
            viewBinding.edtName.text.toString().trim()

        val email =
            viewBinding.edtEmail.text.toString().trim()

        val password =
            viewBinding.edtPassword.text.toString()

        val acceptedTerms =
            viewBinding.cbAcceptTerms.isChecked

        val phoneNumber = viewBinding.mobileInputContainer.phoneNumberOrNull
        when {
            phoneNumber == null -> {
                viewBinding.mobileInputContainer.showValidationError(
                    getString(
                        R.string.invalid_phone_number,
                        viewBinding.mobileInputContainer.selectedCountryName,
                    )
                )
            }

            name.isBlank() -> {
                viewBinding.edtName.showTemporaryError(getString(R.string.invalid_name))
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                viewBinding.edtEmail.showTemporaryError(getString(R.string.invalid_email))
            }

            password.length < MIN_PASSWORD_LENGTH -> {
                viewBinding.edtPassword.showTemporaryError(getString(R.string.invalid_password))
            }

            !acceptedTerms -> {
                showTopNotification(
                    getString(R.string.accept_terms_required),
                    TopNotificationType.WARNING,
                )
            }

            else -> {
                startActivity(
                    Intent(this, InputOptActivity::class.java).apply {
                        putExtra(InputOptActivity.EXTRA_FLOW, InputOptActivity.FLOW_CREATE_ACCOUNT)
                        putExtra(InputOptActivity.EXTRA_PHONE_NUMBER, phoneNumber)
                        putExtra(InputOptActivity.EXTRA_FULL_NAME, name)
                        putExtra(InputOptActivity.EXTRA_EMAIL, email)
                    }
                )
            }
        }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
