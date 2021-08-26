package com.example.kotovskdatabase.ui.firstscreen.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import com.example.kotovskdatabase.repositiry.entity.Cat

class SwipeHelper(onSwiped: (Cat) -> Unit,): ItemTouchHelper(SwipeCallback(onSwiped))