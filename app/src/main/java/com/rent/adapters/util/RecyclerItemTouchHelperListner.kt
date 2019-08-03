package com.rent.adapters.util

import androidx.recyclerview.widget.RecyclerView

interface RecyclerItemTouchHelperListner {
    fun onSwiped(   viewHolder: RecyclerView.ViewHolder, direction:Int , position:Int)
}
