package com.example.kotovskdatabase.ui.firstscreen.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotovskdatabase.databinding.ItemCatBinding

import com.example.kotovskdatabase.repositiry.entity.Cat

class CatAdapter(private val onClick: (Cat) -> Unit)
    : ListAdapter<Cat, CatAdapter.CatViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val binding = ItemCatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class CatViewHolder(private val binding: ItemCatBinding, onClick: (Cat) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        var item: Cat? = null
            private set

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val cat = getItem(position)
                        onClick(cat)
                    }
                }
            }
        }

        fun bind(cat: Cat) {

            this.item = cat

            binding.apply {
                nameList.text = item?.name
                breedList.text = item?.breed
                ageList.text = item?.age.toString()
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Cat>() {
        override fun areItemsTheSame(oldItem: Cat, newItem: Cat) =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean =
            oldItem == newItem
    }
}