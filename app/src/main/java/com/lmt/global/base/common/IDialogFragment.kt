package com.lmt.global.base.common

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.util.UUID
import androidx.core.graphics.drawable.toDrawable

abstract class IDialogFragment<VB : ViewDataBinding>(
    private val inflate: Inflater<VB> = DataBindingUtil::inflate
) : DialogFragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    @LayoutRes
    protected abstract fun provideLayout(): Int

    protected val TAG: String by lazy { this::class.java.canonicalName.ifEmpty { "BS_${UUID.randomUUID()}" } }

    var isCreated: Boolean = false
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflate(inflater, provideLayout(), container, false).also { _binding = it }.root.let {
        if (it.parent != null) {
            ((it.parent) as ViewGroup).removeView(it)
        }
        return@let it
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCreated = true

        initViews()
        initObservers()
        initListeners()
    }

    protected abstract fun initViews()
    protected open fun initObservers() {}
    protected open fun initListeners() {}

    protected open fun dimAmount(): Float = 0.6f
    protected open fun width(): Float = 0.95f

    fun show(fragmentManager: FragmentManager) {
        if (isAdded || isVisible || isRemoving || isStateSaved) {
            return
        }
        this.show(fragmentManager, TAG)
    }

    fun setDialogCancelable(isCancelable: Boolean) {
        this.isCancelable = isCancelable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isCreated = false
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

            val dialogWidth = (resources.displayMetrics.widthPixels * width()).toInt()
            setLayout(
                dialogWidth,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            val windowParams: WindowManager.LayoutParams = attributes
            windowParams.dimAmount = dimAmount()
            windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            attributes = windowParams
        }
    }
}
