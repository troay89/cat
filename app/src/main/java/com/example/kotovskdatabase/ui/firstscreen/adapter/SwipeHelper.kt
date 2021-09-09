package com.example.kotovskdatabase.ui.firstscreen.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kotovskdatabase.repositiry.entity.Cat

class SwipeCallback(
    private val onSwiped: (Cat) -> Unit,
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        (viewHolder as? CatAdapter.CatViewHolder)?.item?.let { onSwiped(it) }
    }
}

class SwipeHelper(onSwiped: (Cat) -> Unit,): ItemTouchHelper(SwipeCallback(onSwiped))