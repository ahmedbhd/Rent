package com.rent.global.utils

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rent.R
import com.rent.data.model.flight.Flight
import com.rent.data.model.relations.LocataireWithPayment
import com.rent.data.model.relations.RentalWithLocataire
import com.rent.ui.main.calendar.CalendarAdapter
import com.rent.ui.main.payment.PaymentListAdapter
import com.rent.ui.main.rental.RentalListAdapter


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
    value: String?
) {
    spinner.setSelection(
        spinner.context.resources.getStringArray(R.array.house_list).indexOf(value), false
    )
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

@BindingAdapter("items")
fun setCalendarItems(
    recyclerView: RecyclerView,
    data: ArrayList<Flight>?
) {
    data?.let {
        recyclerView.invalidate()
        (recyclerView.adapter as CalendarAdapter).setData(it)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                RecyclerView.VERTICAL
            )
        )
    }
}

@BindingAdapter("data")
fun setRentalItems(
    recyclerView: RecyclerView,
    data: ArrayList<RentalWithLocataire>?
) {
    data?.let {
        (recyclerView.adapter as RentalListAdapter).setData(it)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        prepareRentalRecyclerView(recyclerView)
    }
}

private fun prepareRentalRecyclerView(recyclerView: RecyclerView) {

    val colorDrawableBackground = ColorDrawable(Color.parseColor("#0097A7"))
    val deleteIcon = ContextCompat.getDrawable(
        recyclerView.context,
        R.drawable.phonecall
    )!!

    val itemTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            viewHolder2: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
            (recyclerView.adapter as RentalListAdapter).showPopupTel(viewHolder.absoluteAdapterPosition)
            (recyclerView.adapter as RentalListAdapter).notifyItemChanged(viewHolder.absoluteAdapterPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val iconMarginVertical =
                (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

            if (dX > 0) {
                colorDrawableBackground.setBounds(
                    itemView.left,
                    itemView.top,
                    dX.toInt(),
                    itemView.bottom
                )
                deleteIcon.setBounds(
                    itemView.left + iconMarginVertical,
                    itemView.top + iconMarginVertical,
                    itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                    itemView.bottom - iconMarginVertical
                )
            } else {
                colorDrawableBackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                deleteIcon.setBounds(
                    itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                    itemView.top + iconMarginVertical,
                    itemView.right - iconMarginVertical,
                    itemView.bottom - iconMarginVertical
                )
                deleteIcon.level = 0
            }

            colorDrawableBackground.draw(c)

            c.save()

            if (dX > 0)
                c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            else
                c.clipRect(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )

            deleteIcon.draw(c)

            c.restore()

            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }

    }

    val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
    itemTouchHelper.attachToRecyclerView(recyclerView)
}

@BindingAdapter("swipeColorScheme")
fun setupSwipeColors(swipe: SwipeRefreshLayout, isColored: Boolean) {
    if (isColored) {
        swipe.setColorSchemeColors(
            ContextCompat.getColor(
                swipe.context,
                R.color.colorAccent
            ), ContextCompat.getColor(swipe.context, R.color.colorPrimaryDark)
        )
    }
}

@BindingAdapter("data")
fun setPaymentItems(
    recyclerView: RecyclerView,
    data: ArrayList<LocataireWithPayment>?
) {
    data?.let {
        (recyclerView.adapter as PaymentListAdapter).setData(it)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        preparePaymentRecyclerView(recyclerView)
    }
}

private fun preparePaymentRecyclerView(recyclerView: RecyclerView) {


    val colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
    val deleteIcon = ContextCompat.getDrawable(
        recyclerView.context,
        R.drawable.ic_delete_white_24dp
    )!!

    val itemTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            viewHolder2: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
            (recyclerView.adapter as PaymentListAdapter).removeItem(
                viewHolder.absoluteAdapterPosition
            )
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val iconMarginVertical =
                (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

            if (dX > 0) {
                colorDrawableBackground.setBounds(
                    itemView.left,
                    itemView.top,
                    dX.toInt(),
                    itemView.bottom
                )
                deleteIcon.setBounds(
                    itemView.left + iconMarginVertical,
                    itemView.top + iconMarginVertical,
                    itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                    itemView.bottom - iconMarginVertical
                )
            } else {
                colorDrawableBackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                deleteIcon.setBounds(
                    itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                    itemView.top + iconMarginVertical,
                    itemView.right - iconMarginVertical,
                    itemView.bottom - iconMarginVertical
                )
                deleteIcon.level = 0
            }

            colorDrawableBackground.draw(c)

            c.save()

            if (dX > 0)
                c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            else
                c.clipRect(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )

            deleteIcon.draw(c)

            c.restore()

            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        }

    }

    val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
    itemTouchHelper.attachToRecyclerView(recyclerView)
}

@BindingAdapter("backgroundColor")
fun setBackgroundColor(
    textView: AppCompatTextView,
    color: Int?
) {
    color?.let {
        val background = textView.background
        (background as GradientDrawable).setColor(it)
    }
}

@BindingAdapter(value = ["searchListener", "searchActivity"], requireAll = true)
fun setSearchListener(
    searchView: SearchView,
    listener: SearchView.OnQueryTextListener?,
    activity: Activity
) {
    listener?.let {
        val searchManager =
            activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(activity.componentName)
        )
        searchView.isSubmitButtonEnabled = false
        searchView.setOnQueryTextListener(listener)
    }
}