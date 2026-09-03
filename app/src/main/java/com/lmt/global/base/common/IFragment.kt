package com.lmt.global.base.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lmt.global.base.helper.firebase.RemoteConfigManagement
import com.lmt.global.base.helper.preferences.AppSharedPreferences
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

typealias Inflater<VB> = (LayoutInflater, Int, ViewGroup?, Boolean) -> VB

abstract class IFragment<VB : ViewDataBinding, VM : IViewModel<*>>(
    private val inflate: Inflater<VB> = DataBindingUtil::inflate
) : Fragment() {

    protected val appSharedPreferences by inject<AppSharedPreferences>()
    protected val configManagement by inject<RemoteConfigManagement>()

    private var _viewBinding: VB? = null
    protected val viewBinding: VB get() = _viewBinding!!

    protected abstract fun provideViewModel(): Lazy<VM>
    protected val viewModel: VM by this.provideViewModel()

    @LayoutRes
    protected abstract fun provideLayout(): Int

    var isCreated: Boolean = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        setupInit()
        super.onCreate(savedInstanceState)
    }

    protected open fun setupInit() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflate(inflater, provideLayout(), container, false).also { _viewBinding = it }.root.let {
        if (it.parent != null) {
            ((it.parent) as ViewGroup).removeView(it)
        }
        return@let it
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCreated = true

        initAds()
        initViews()
        initObservers()
        initListeners()
    }

    @CallSuper
    protected open fun initAds(block: (suspend CoroutineScope.() -> Unit)? = null) {
        block?.let {
            lifecycleScope.launch(CoroutineExceptionHandler { context, throwable ->
                context.ensureActive()
                throwable.printStackTrace()
            }) {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    it.invoke(this)
                }
            }
        }
    }

    abstract fun initViews()
    protected open fun initObservers() = Unit
    protected open fun initListeners() = Unit

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
        isCreated = false
    }
}
