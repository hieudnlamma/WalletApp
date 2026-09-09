package com.lmt.global.base.view.custom

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.lmt.global.base.R

class NumberKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private var onNumberClick: ((String) -> Unit)? = null
    private var onDeleteClick: (() -> Unit)? = null
    private lateinit var decimalButton: TextView

    private val numberTypeface: Typeface? by lazy {
        ResourcesCompat.getFont(
            context,
            R.font.sora_semibold,
        )
    }

    init {
        orientation = VERTICAL

        setupKeyboard()
    }

    private fun setupKeyboard() {
        addNumberRow("1", "2", "3")
        addNumberRow("4", "5", "6")
        addNumberRow("7", "8", "9")
        addLastRow()
    }

    private fun addNumberRow(
        first: String,
        second: String,
        third: String,
    ) {
        val row = createRow()

        row.addView(createNumberButton(first))
        row.addView(createNumberButton(second))
        row.addView(createNumberButton(third))

        addView(row)
    }

    private fun addLastRow() {
        val row = createRow()

        decimalButton = createNumberButton(".").apply {
            visibility = View.INVISIBLE
        }
        row.addView(decimalButton)

        // số 0 ở giữa
        row.addView(createNumberButton("0"))

        // delete bên phải
        row.addView(createDeleteButton())

        addView(row)
    }

    private fun createRow(): LinearLayout {
        return LinearLayout(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                64.dp,
            )

            orientation = HORIZONTAL
            gravity = Gravity.CENTER
        }
    }

    private fun createNumberButton(
        number: String,
    ): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(
                0,
                LayoutParams.MATCH_PARENT,
                1f,
            )

            gravity = Gravity.CENTER

            text = number

            textSize = 20f

            typeface = numberTypeface

            setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.neutrals_black,
                ),
            )

            isClickable = true
            isFocusable = true

            setOnClickListener {
                onNumberClick?.invoke(number)
            }
        }
    }

    private fun createDeleteButton(): ImageView {
        return ImageView(context).apply {
            layoutParams = LayoutParams(
                0,
                LayoutParams.MATCH_PARENT,
                1f,
            )

            setImageResource(
                R.drawable.icn_delete_back,
            )

            scaleType = ImageView.ScaleType.CENTER

            contentDescription = context.getString(
                R.string.delete,
            )

            isClickable = true
            isFocusable = true

            setOnClickListener {
                onDeleteClick?.invoke()
            }
        }
    }

    fun setOnNumberClickListener(
        listener: (String) -> Unit,
    ) {
        onNumberClick = listener
    }

    fun setOnDeleteClickListener(
        listener: () -> Unit,
    ) {
        onDeleteClick = listener
    }

    fun setDecimalEnabled(enabled: Boolean) {
        decimalButton.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}
