package com.rent.ui.shared.view

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.rent.R

class CustomProgressBar : ProgressBar {

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize()
    }

    private fun initialize() {
        indeterminateDrawable.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(context, R.color.colorAccent),
            PorterDuff.Mode.MULTIPLY
        )
    }
}