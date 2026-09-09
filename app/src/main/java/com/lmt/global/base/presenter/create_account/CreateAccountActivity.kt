package com.lmt.global.base.presenter.login.feature

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityCreateAccountBinding
import com.lmt.global.base.presenter.create_account.feature.InputOptActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateAccountActivity :
    IActivity<ActivityCreateAccountBinding, CommonViewModel>() {

    override fun provideViewModel() = viewModel<CommonViewModel>()

    override fun provideLayout() = R.layout.activity_create_account

    private var isPasswordVisible = false

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
        setupKeyboardScroll()
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
        val toolbarPaddingTop = viewBinding.toolbar.paddingTop
        val scrollPaddingBottom = viewBinding.createAccountScrollView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { _, insets ->

            val statusBar =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            val navigationBar =
                insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            val ime =
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            viewBinding.toolbar.updatePadding(
                top = toolbarPaddingTop + statusBar
            )

            viewBinding.createAccountScrollView.updatePadding(
                bottom = scrollPaddingBottom + maxOf(ime, navigationBar)
            )

            if (ime > 0) {
                getFocusedInput()?.let {
                    scrollInputAboveKeyboard(it)
                }
            }

            insets
        }

        ViewCompat.requestApplyInsets(viewBinding.root)
    }

    private fun setupKeyboardScroll() {

        viewBinding.edtName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollInputAboveKeyboard(viewBinding.edtName)
            }
        }

        viewBinding.edtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollInputAboveKeyboard(viewBinding.edtEmail)
            }
        }

        viewBinding.edtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollInputAboveKeyboard(viewBinding.passwordInputContainer)
            }
        }
    }

    private fun getFocusedInput(): View? {
        return when {
            viewBinding.edtName.hasFocus() ->
                viewBinding.edtName

            viewBinding.edtEmail.hasFocus() ->
                viewBinding.edtEmail

            viewBinding.edtPassword.hasFocus() ->
                viewBinding.passwordInputContainer

            else -> null
        }
    }

    private fun scrollInputAboveKeyboard(target: View) {
        viewBinding.createAccountScrollView.post {

            val targetBottom = target.bottom

            val visibleBottom =
                viewBinding.createAccountScrollView.height -
                        viewBinding.createAccountScrollView.paddingBottom

            val scrollY =
                (targetBottom - visibleBottom).coerceAtLeast(0)

            viewBinding.createAccountScrollView.smoothScrollTo(
                0,
                scrollY
            )
        }
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
            object : android.text.style.ClickableSpan() {

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

        // TODO: validate data
        // TODO: call ViewModel/API
        startActivity(Intent(this, InputOptActivity::class.java))
    }
}
