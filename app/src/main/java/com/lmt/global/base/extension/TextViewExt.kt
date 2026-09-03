package com.lmt.global.base.extension

import android.content.Context
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.common.internal.Preconditions.checkMainThread
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        checkMainThread()
        val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun EditText.enableKeyboardOnTouch(context: Context) {
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
        false
    }
}

