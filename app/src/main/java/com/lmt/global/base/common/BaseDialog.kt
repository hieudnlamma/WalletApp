package com.lmt.global.base.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lmt.global.base.R
import androidx.core.graphics.drawable.toDrawable

abstract class BaseDialog<VB : ViewDataBinding>(
    context: Context,
    themeResId: Int = R.style.Base_Theme_PhoneTracker_Kotlin,
) :
    Dialog(context, themeResId) {
    lateinit var binding: VB

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        createContentView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        onResizeViews()
        onClickViews()
    }

    private fun createContentView() {
        val layoutView = getLayoutDialog()
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutView, null, false)
        setContentView(binding.root)
    }

    abstract fun getLayoutDialog(): Int

    open fun initViews() {}

    open fun onResizeViews() {}

    open fun onClickViews() {}


    fun setDialogBottom() {
        window?.run {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
        }
    }

}