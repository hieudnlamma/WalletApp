package com.lmt.global.base.common

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.lmt.global.base.helper.firebase.RemoteConfigManagement
import com.lmt.global.base.helper.preferences.AppSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Locale

typealias OnPerformBackPressed = () -> Unit

abstract class IActivity<VB : ViewDataBinding, VM : IViewModel<*>> : AppCompatActivity() {

    protected val appSharedPreferences by inject<AppSharedPreferences>()
    protected val configManagement by inject<RemoteConfigManagement>()

    protected val viewModel: VM by provideViewModel()
    protected abstract fun provideViewModel(): Lazy<VM>

    @LayoutRes
    protected abstract fun provideLayout(): Int
    protected val viewBinding: VB by lazy {
        DataBindingUtil.setContentView<VB>(this@IActivity, provideLayout())
    }

    private var onPerformBackPressed: OnPerformBackPressed? = null
    private var onBackPressedCallback = object : OnBackPressedCallback(enabled = true) {
        override fun handleOnBackPressed() {
            onPerformBackPressed?.invoke()
        }
    }

    protected val TAG: String
        get() = this::class.java.simpleName

    override fun attachBaseContext(newBase: Context) {
        var isoLanguage = appSharedPreferences.currentLanguage
        if (isoLanguage.isEmpty()) {
            isoLanguage = Resources.getSystem().configuration.locales[0].language
        }
        val newLocale = Locale(isoLanguage.toString())
        Locale.setDefault(newLocale)
        val configuration = newBase.resources.configuration
        configuration.setLocale(newLocale)
        val newContext = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(ContextWrapper(newContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupInit()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        enableConfigEdgeSystemBar()
        registerBackPressedDispatcher()
        viewBinding.lifecycleOwner = this@IActivity

        initViews(savedInstanceState)
        initObservers()
        initListeners()
    }

    protected fun enableConfigEdgeSystemBar(
        isFitsSystemWindows: Boolean = true,
        isLight: Boolean = true
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            WindowCompat.setDecorFitsSystemWindows(window, isFitsSystemWindows)
            window.decorView.post {
                WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                    controller.isAppearanceLightStatusBars = isLight
                    controller.isAppearanceLightNavigationBars = isLight
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }

    protected open fun isHideSystemBars(): Boolean = false
    protected fun setupApplyWindowInsetListener(block: (insets: WindowInsetsCompat) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insetCompact ->
            block(insetCompact)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun registerBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this@IActivity, onBackPressedCallback)
        onBackPressedCallback.isEnabled = onHandleBackPressed()
    }

    @CallSuper
    protected open fun onHandleBackPressed(onBackPressed: OnPerformBackPressed? = null): Boolean {
        this.onPerformBackPressed = onBackPressed
        return onBackPressed != null
    }

    protected open fun setupInit() = Unit

    protected abstract fun initViews(savedInstanceState: Bundle?)

    @CallSuper
    protected open fun initObservers() {
        viewModel
    }

    protected open fun initListeners() = Unit

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        var isoLanguage = appSharedPreferences.currentLanguage
        if (isoLanguage.isEmpty()) {
            isoLanguage = Resources.getSystem().configuration.locales[0].language
        }
        val newLocale = Locale(isoLanguage)
        Locale.setDefault(newLocale)
        newConfig.setLocale(newLocale)
    }

    protected fun setupToolbar(
        toolbar: Toolbar?,
        @StringRes title: Int? = null,
        @DrawableRes icon: Int? = null,
        onNavigationClicked: (() -> Unit)? = null
    ) {
        toolbar ?: return
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(title != null)
            title?.let { supportActionBar?.setTitle(title) }

            it.setDisplayHomeAsUpEnabled(icon != null)
            icon?.let { supportActionBar?.setHomeAsUpIndicator(icon) }
        }
        toolbar.setNavigationOnClickListener {
            onNavigationClicked?.invoke() ?: onBackPressedDispatcher.onBackPressed()
        }
    }

    @CallSuper
    override fun onStart() {
        postHideSystemBar()
        super.onStart()
    }

    protected fun postHideSystemBar() {
        window.decorView.postDelayed({
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                if (isHideSystemBars()) {
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                } else {
                    controller.show(WindowInsetsCompat.Type.statusBars())
                    controller.hide(WindowInsetsCompat.Type.navigationBars())
                }
            }
        }, 50L)
    }
}
