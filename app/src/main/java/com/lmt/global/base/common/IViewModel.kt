package com.lmt.global.base.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmt.global.base.helper.preferences.AppSharedPreferences
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class IViewModel<State : IViewModel.IState>() : ViewModel(), KoinComponent {

    protected val appSharedPreferences by inject<AppSharedPreferences>()

    protected suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Main.immediate, block)
    }

    protected suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.IO, block)
    }

    protected suspend fun <T> withDefault(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Default, block)
    }

    protected suspend fun <T> withUnconfined(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Unconfined, block)
    }

    protected fun launchWithMain(
        error: (Throwable) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(
        context = Dispatchers.Main.immediate + CoroutineExceptionHandler { context, throwable ->
            context.ensureActive()
            viewModelScope.launch(Dispatchers.Main.immediate) {
                error(throwable)
            }
        },
        block = {
            runCatching { withMain(block) }.onFailure {
                withMain { error(it) }
            }
        }
    )

    protected fun launchWithIO(
        error: (Throwable) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(
        context = Dispatchers.IO + CoroutineExceptionHandler { context, throwable ->
            context.ensureActive()
            viewModelScope.launch(Dispatchers.Main.immediate) {
                error(throwable)
            }
        },
        block = {
            runCatching { withIO(block) }.onFailure {
                withMain { error(it) }
            }
        }
    )

    protected fun launchWithDefault(
        error: (Throwable) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(
        context = Dispatchers.Default + CoroutineExceptionHandler { context, throwable ->
            context.ensureActive()
            viewModelScope.launch(Dispatchers.Main.immediate) {
                error(throwable)
            }
        },
        block = {
            runCatching { withDefault(block) }.onFailure {
                withMain { error(it) }
            }
        }
    )

    abstract fun onState(state: State)

    interface IState
}

class CommonViewModel() : IViewModel<IViewModel.IState>() {
    override fun onState(state: IState) = Unit
}
