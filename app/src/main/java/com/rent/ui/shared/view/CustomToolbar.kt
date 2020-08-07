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
    ),

    BindingMethod(
        type = CustomToolbar::class,
        attribute = "app:setCenterTitleText",
        method = "setCenterTitleText"
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
    private var toolbarBackgroundDrawable = DEFAULT_BACKGROUND_COLOR
        set(value) {
            field = value
            this.setBackgroundResource(value)
        }

    private var hasStartActionButton = false
        set(value) {
            field = value
            setComponentVisibility(imageToolbarStartActionIcon, field)
        }

    private var hasEndActionButton = false
        set(value) {
            field = value
            setComponentVisibility(endAction, field)
        }

    private var hasCenterTitle = false
        set(value) {
            field = value
            setComponentVisibility(textToolbarCenterTitle, field)
        }

    private var startActionDrawable = DEFAULT_LEFT_ACTION_BUTTON_DRAWABLE
        set(value) {
            field = value
            imageToolbarStartActionIcon.setImageResource(value)
        }

    private var titleText
            : String? = ""
        set(value) {
            field = value
            textToolbarCenterTitle.text = value
        }

    private var endActionDrawable = DEFAULT_RIGHT_ACTION_BUTTON_DRAWABLE
        set(value) {
            field = value
            imageToolbarEndActionIcon.setImageResource(value)
        }

    private var endActionTitle: String? = ""
        set(value) {
            field = value
            textToolbarEndAction.text = value
        }

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.custom_toolbar, this, true)
        initAttributes(context, attrs)
        initStart()
        initCenter()
        initEnd()
        toolbarBackgroundDrawable = toolbarBackgroundDrawable
        imageToolbarStartActionIcon.setClickWithDebounce { toolbarListener?.onStartActionClick() }
        endAction.setClickWithDebounce { toolbarListener?.onEndActionClick() }
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
            startActionDrawable = array.getResourceId(
                R.styleable.CustomToolbar_startActionDrawable,
                DEFAULT_LEFT_ACTION_BUTTON_DRAWABLE
            )
            titleText = array.getString(R.styleable.CustomToolbar_centerTitleText)
            endActionDrawable = array.getResourceId(
                R.styleable.CustomToolbar_endActionDrawable,
                DEFAULT_RIGHT_ACTION_BUTTON_DRAWABLE
            )
            endActionTitle = array.getString(R.styleable.CustomToolbar_endActionTitle)
            array.recycle()
        }
    }

    private fun initCenter() {
        if (hasCenterTitle) {
            titleText = titleText
        }
    }

    private fun initStart() {
        if (hasStartActionButton) {
            startActionDrawable = startActionDrawable
        }
    }

    private fun initEnd() {
        if (hasEndActionButton) {
            if (endActionTitle.isNullOrEmpty()) {
                endActionDrawable = endActionDrawable
                setComponentVisibility(imageToolbarEndActionIcon, true)
                setComponentVisibility(textToolbarEndAction, false)
            } else {
                endActionTitle = endActionTitle
                setComponentVisibility(imageToolbarEndActionIcon, false)
                setComponentVisibility(textToolbarEndAction, true)
            }
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

    fun setCenterTitleText(title: String?) {
        title?.let {
            titleText = title
        }
    }
}


private const val DEFAULT_BACKGROUND_COLOR = R.drawable.global_background
private const val DEFAULT_HAS_LEFT_ACTION_BUTTON = false
private const val DEFAULT_HAS_RIGHT_ACTION_BUTTON = false
private const val DEFAULT_HAS_CENTER_TITLE = false
private const val DEFAULT_LEFT_ACTION_BUTTON_DRAWABLE = R.drawable.ic_arrow_back
private const val DEFAULT_RIGHT_ACTION_BUTTON_DRAWABLE = R.drawable.ic_garbage