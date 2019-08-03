//package com.rent.adapters.util
//
//import android.graphics.Canvas
//import android.view.View
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.RecyclerView
//import com.rent.adapters.CustomListAdapter
//
//class RecyclerItemTouchHelper( dragDirs: Int,  swipeDirs: Int,  listeners: RecyclerItemTouchHelperListner): ItemTouchHelper.SimpleCallback(dragDirs,swipeDirs) {
//
//    private var listener:RecyclerItemTouchHelperListner = listeners
//    override fun onMove(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ): Boolean {
//       return true
//    }
//
//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        if (listener != null){
//            listener.onSwiped(viewHolder,direction,viewHolder.adapterPosition)
//        }
//    }
//
//    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//        val foregroundView: View = (viewHolder as (CustomListAdapter.MyViewHolder )).viewFg!!
//        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
//    }
//
//    override fun onChildDraw(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        dX: Float,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
//    ) {
//        val foregroundView: View = (viewHolder as (CustomListAdapter.MyViewHolder )).viewFg!!
//        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive)
//    }
//
//    override fun onChildDrawOver(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder?,
//        dX: Float,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
//    ) {
//        val foregroundView: View = (viewHolder as (CustomListAdapter.MyViewHolder )).viewFg!!
//        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive)
//    }
//
//    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//        if (viewHolder != null){
//            val foregroundView: View = (viewHolder as (CustomListAdapter.MyViewHolder )).viewFg!!
//            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
//        }
//    }
//
//    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
//        return super.convertToAbsoluteDirection(flags, layoutDirection)
//    }
//}