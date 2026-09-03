package com.lmt.global.base.common

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lmt.global.base.R
import java.util.UUID

abstract class IBottomSheetDialogFragment<T : ViewDataBinding>(
    private val inflate: Inflater<T>
) : BottomSheetDialogFragment() {

    protected val TAG: String by lazy { this::class.java.canonicalName.ifEmpty { "BS_${UUID.randomUUID()}" } }

    private var _binding: T? = null
    protected val binding get() = _binding!!

    @LayoutRes
    protected abstract fun provideLayout(): Int

    var isCreated: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(dismissWhenTouchOutside())
        }
    }

    protected open fun dismissWhenTouchOutside(): Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStyle(STYLE_NORMAL, R.style.Base_Theme_PhoneTracker_Kotlin)
        return inflate(inflater, provideLayout(), container, false).also {
            _binding = it
        }.root.let {
            it.parent?.runCatching { (this as ViewGroup).removeView(it) }
            it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCreated = true

        initViews()
        initObservers()
        initListeners()
    }

    protected abstract fun initViews()
    protected open fun initObservers() = Unit
    protected open fun initListeners() = Unit

    protected open fun dimAmount(): Float = 0.6f

    fun show(fragmentManager: FragmentManager) {
        if (this.isAdded || this.isRemoving || fragmentManager.isStateSaved) {
            return
        }
        this.show(fragmentManager, TAG)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            val windowParams: WindowManager.LayoutParams = attributes
            windowParams.dimAmount = dimAmount()
            windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            attributes = windowParams
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
