package com.lmt.global.base.presenter.login

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
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
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { _, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            viewBinding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = statusBar
            }
            insets
        }
        ViewCompat.requestApplyInsets(viewBinding.root)
    }
}
