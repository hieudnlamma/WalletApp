package com.lmt.global.base.common

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<VB : ViewBinding>(val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB) : DialogFragment() {

    lateinit var binding: VB

    abstract fun isFull() : Boolean

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, if (isFull()) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.8f)
            // Make the navigation bar transparent
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
            navigationBarColor = Color.TRANSPARENT
        }
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onViewCreated()
    }

    abstract fun onViewCreated()
}