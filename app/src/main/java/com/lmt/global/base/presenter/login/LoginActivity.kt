package com.lmt.global.base.presenter.login

import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityLoginBinding
import com.lmt.global.base.presenter.login.feature.CreateAccountActivity
import com.lmt.global.base.presenter.login.feature.EnterPasswordActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : IActivity<ActivityLoginBinding, CommonViewModel>() {
    override fun provideViewModel() = viewModel<CommonViewModel>()
    override fun provideLayout() = R.layout.activity_login
    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
        setupKeyboardScroll()
    }

    override fun initListeners() {
        viewBinding.btnContinue.setOnClickListener {
            startActivity(Intent(this, EnterPasswordActivity::class.java))
        }

        viewBinding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    private fun setupInsets() {
        val toolbarPaddingTop = viewBinding.toolbar.paddingTop
        val scrollPaddingBottom = viewBinding.loginScrollView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { _, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navigationBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            viewBinding.toolbar.updatePadding(top = toolbarPaddingTop + statusBar)
            viewBinding.loginScrollView.updatePadding(
                bottom = scrollPaddingBottom + maxOf(ime, navigationBar)
            )

            if (ime > 0 && viewBinding.edtMobileNumber.hasFocus()) {
                scrollMobileInputAboveKeyboard()
            }

            insets
        }
        ViewCompat.requestApplyInsets(viewBinding.root)
    }

    private fun setupKeyboardScroll() {
        viewBinding.edtMobileNumber.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollMobileInputAboveKeyboard()
            }
        }
    }

    private fun scrollMobileInputAboveKeyboard() {
        viewBinding.loginScrollView.post {
            val targetBottom = viewBinding.mobileInputContainer.bottom
            val visibleBottom = viewBinding.loginScrollView.height - viewBinding.loginScrollView.paddingBottom
            val scrollY = (targetBottom - visibleBottom).coerceAtLeast(0)
            viewBinding.loginScrollView.smoothScrollTo(0, scrollY)
        }
    }
}
