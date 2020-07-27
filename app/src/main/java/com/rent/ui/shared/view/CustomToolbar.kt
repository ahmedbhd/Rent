package com.rent.ui.shared.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.rent.R
import com.rent.global.listener.ToolbarListener
import com.rent.global.utils.setClickWithDebounce
import kotlinx.android.synthetic.main.custom_toolbar.view.*


@BindingMethods(
    BindingMethod(
        type = CustomToolbar::class,
        attribute = "app:onActionClicked",
        method = "setToolbarListener"
    )
)
class CustomToolbar : FrameLayout {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs)
    }

    private var toolbarListener: ToolbarListener? = null

    // Attributes
    var toolbarBackgroundDrawable = DEFAULT_BACKGROUND_COLOR
        set(value) {
            field = value
            this.setBackgroundResource(value)
        }

    var hasStartActionButton = false
        set(value) {
            field = value
            setComponentVisibility(imageToolbarStartActionIcon, field)
        }

    var hasEndActionButton = false
        set(value) {
            field = value
            setComponentVisibility(imageToolbarEndActionIcon, field)
        }

    var hasCenterTitle = false
        set(value) {
            field = value
            setComponentVisibility(textToolbarCenterTitle, field)
        }

    var startActionButtonDrawable = DEFAULT_LEFT_ACTION_BUTTON_DRAWABLE
        set(value) {
            field = value
            imageToolbarStartActionIcon.setImageResource(value)
        }

    var centerTitleText: String? = ""
        set(value) {
            field = value
            textToolbarCenterTitle.text = value
        }

    var endActionButtonDrawable = DEFAULT_RIGHT_ACTION_BUTTON_DRAWABLE
        set(value) {
            field = value
            imageToolbarEndActionIcon.setImageResource(value)
        }

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.custom_toolbar, this, true)
        initAttributes(context, attrs)
        initStart()
        initCenter()
        initEnd()
        toolbarBackgroundDrawable = toolbarBackgroundDrawable
        imageToolbarStartActionIcon.setClickWithDebounce { toolbarListener?.onStartActionClick() }
        imageToolbarEndActionIcon.setClickWithDebounce { toolbarListener?.onEndActionClick() }
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val array = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar)
            toolbarBackgroundDrawable = array.getResourceId(
                R.styleable.CustomToolbar_backgroundDrawable,
                DEFAULT_BACKGROUND_COLOR
            )
            hasStartActionButton =
                array.getBoolean(
                    R.styleable.CustomToolbar_hasStartActionButton,
                    DEFAULT_HAS_LEFT_ACTION_BUTTON
                )
            hasEndActionButton =
                array.getBoolean(
                    R.styleable.CustomToolbar_hasEndActionButton,
                    DEFAULT_HAS_RIGHT_ACTION_BUTTON
                )
            hasCenterTitle = array.getBoolean(
                R.styleable.CustomToolbar_hasCenterTitle,
                DEFAULT_HAS_CENTER_TITLE
            )
            startActionButtonDrawable = array.getResourceId(
                R.styleable.CustomToolbar_startActionButtonDrawable,
                DEFAULT_LEFT_ACTION_BUTTON_DRAWABLE
            )
            centerTitleText = array.getString(R.styleable.CustomToolbar_centerTitleText)
            endActionButtonDrawable = array.getResourceId(
                R.styleable.CustomToolbar_endActionButtonDrawable,
                DEFAULT_RIGHT_ACTION_BUTTON_DRAWABLE
            )
            array.recycle()
        }
    }

    private fun initCenter() {
        if (hasCenterTitle) {
            centerTitleText = centerTitleText
        }
    }

    private fun initStart() {
        if (hasStartActionButton) {
            startActionButtonDrawable = startActionButtonDrawable
        }
    }

    private fun initEnd() {
        if (hasEndActionButton) {
            endActionButtonDrawable = endActionButtonDrawable
        }
    }

    private fun setComponentVisibility(view: View, visible: Boolean) {
        if (visible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }

    fun setToolbarListener(toolbarListener: ToolbarListener) {
        this.toolbarListener = toolbarListener
    }
    
    fun setStartDrawable(drawable: Drawable) {
        imageToolbarStartActionIcon.setImageDrawable(drawable)
    }
}


private const val DEFAULT_BACKGROUND_COLOR = R.color.colorPrimary
private const val DEFAULT_HAS_LEFT_ACTION_BUTTON = false
private const val DEFAULT_HAS_RIGHT_ACTION_BUTTON = false
private const val DEFAULT_HAS_CENTER_TITLE = false
private const val DEFAULT_LEFT_ACTION_BUTTON_DRAWABLE = R.drawable.ambilwarna_arrow_right
private const val DEFAULT_RIGHT_ACTION_BUTTON_DRAWABLE = R.drawable.ambilwarna_arrow_down

