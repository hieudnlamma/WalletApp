package com.lmt.global.base.presenter.profile

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityProfileBinding
import com.lmt.global.base.databinding.ItemProfileSettingOptionBinding
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.hideKeyboard
import com.lmt.global.base.extension.statusBars
import com.lmt.global.base.model.User
import com.lmt.global.base.presenter.login.feature.EnterNewPasswordActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : IActivity<ActivityProfileBinding, ProfileViewModel>() {
    override fun provideViewModel() = viewModel<ProfileViewModel>()
    override fun provideLayout() = R.layout.activity_profile

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
        setupInputTypes()
    }

    override fun initObservers() {
        super.initObservers()
        collectLatestRepeatOnLifecycle(viewModel.user) { user ->
            user?.let(::renderUser)
        }
        collectLatestRepeatOnLifecycle(viewModel.effects) { effect ->
            handleProfileEffect(effect)
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

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setupEditor(viewBinding.itemFullName, ProfileField.FULL_NAME)
        setupEditor(viewBinding.itemMobile, ProfileField.MOBILE)
        setupEditor(viewBinding.itemEmail, ProfileField.EMAIL)
        viewBinding.onChangePasswordClicked = View.OnClickListener {
            startActivity(Intent(this, EnterNewPasswordActivity::class.java))
        }
    }

    private fun setupInputTypes() {
        viewBinding.itemFullName.edtProfile.apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            setHint(R.string.enter_your_name)
        }
        viewBinding.itemMobile.edtProfile.apply {
            inputType = InputType.TYPE_CLASS_PHONE
            setHint(R.string.enter_your_mobile_number)
        }
        viewBinding.itemEmail.edtProfile.apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setHint(R.string.enter_your_email)
        }
    }

    private fun setupEditor(
        itemBinding: ItemProfileSettingOptionBinding,
        field: ProfileField,
    ) {
        itemBinding.tvEdit.setOnClickListener {
            if (!itemBinding.edtProfile.isVisible) {
                itemBinding.edtProfile.setText(itemBinding.tvValueOption.text)
                itemBinding.edtProfile.setSelection(itemBinding.edtProfile.text?.length ?: 0)
                setEditing(itemBinding, isEditing = true)
                itemBinding.edtProfile.requestFocus()
                return@setOnClickListener
            }

            viewModel.onState(
                ProfileState(
                    field = field,
                    value = itemBinding.edtProfile.text?.toString().orEmpty(),
                )
            )
        }
    }

    private fun renderUser(user: User) = with(viewBinding) {
        val fullName = user.fullName.orEmpty()
        tvUserName.text = fullName
        tvTimeJoined.text = getString(
            R.string.joined_since_format,
            SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Date(user.createdAt)),
        )
        if (!itemFullName.edtProfile.isVisible) itemFullName.optionValue = fullName
        if (!itemMobile.edtProfile.isVisible) itemMobile.optionValue = user.phoneNumber
        if (!itemEmail.edtProfile.isVisible) itemEmail.optionValue = user.email.orEmpty()
        executePendingBindings()
    }

    private fun handleProfileEffect(effect: ProfileEffect) {
        val itemBinding = bindingFor(effect.field)
        when (effect) {
            is ProfileEffect.Saved -> {
                itemBinding.optionValue = itemBinding.edtProfile.text?.toString()?.trim().orEmpty()
                setEditing(itemBinding, isEditing = false)
                itemBinding.edtProfile.hideKeyboard()
            }

            is ProfileEffect.SaveFailed -> Toast.makeText(
                this,
                R.string.profile_update_failed,
                Toast.LENGTH_SHORT,
            ).show()

            is ProfileEffect.ValueRequired -> Toast.makeText(
                this,
                R.string.profile_value_required,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun bindingFor(field: ProfileField): ItemProfileSettingOptionBinding = when (field) {
        ProfileField.FULL_NAME -> viewBinding.itemFullName
        ProfileField.MOBILE -> viewBinding.itemMobile
        ProfileField.EMAIL -> viewBinding.itemEmail
    }

    private fun setEditing(
        itemBinding: ItemProfileSettingOptionBinding,
        isEditing: Boolean,
    ) {
        itemBinding.isEditing = isEditing
        itemBinding.executePendingBindings()
    }
}
