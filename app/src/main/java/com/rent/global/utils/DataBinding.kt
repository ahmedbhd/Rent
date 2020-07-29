package com.rent.global.utils

import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import com.rent.R


@BindingAdapter("onClickWithDebounce")
fun onClickWithDebounce(view: View, listener: View.OnClickListener) {
    view.setClickWithDebounce {
        listener.onClick(view)
    }
}

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("isInvisible")
fun bindIsInvisible(view: View, isInvisible: Boolean) {
    view.visibility = if (isInvisible) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("app:onMultipleClicks")
fun onMultipleClicks(imageView: View, action: (() -> Unit)?) {
    var count = 0
    var startMillis = 0L
    imageView.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val time = System.currentTimeMillis()
                if (startMillis == 0L || time - startMillis > 3_000L) {
                    startMillis = time
                    count = 1
                } else {
                    count++
                    println(count)
                }
                if (count == 5) {
                    action?.invoke()
                }
            }
            MotionEvent.ACTION_UP -> {
                v.performClick()
            }
        }
        true
    }
}


@BindingAdapter("selectedValue")
fun setSelectedValue(
    spinner: AppCompatSpinner,
    position: Int
) {
    spinner.setSelection(position, false)
}

@BindingAdapter("items")
fun setSpinnerEntries(
    spinner: AppCompatSpinner,
    data: ArrayList<String>?
) {
    data?.let {
        val adapter = ArrayAdapter(
            spinner.context,
            R.layout.drop_down_list_tel, data
        )
        spinner.adapter = adapter
    }
}

@BindingAdapter("onItemSelected")
fun setOnItemSelectedListener(
    spinner: AppCompatSpinner,
    listener: AdapterView.OnItemSelectedListener
) {
    spinner.onItemSelectedListener = listener
}
