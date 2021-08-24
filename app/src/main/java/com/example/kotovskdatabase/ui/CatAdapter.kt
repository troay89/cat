package com.example.kotovskdatabase.ui


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotovskdatabase.databinding.ItemCatBinding
import com.example.kotovskdatabase.repositiry.entity.Cat

class CatAdapter: ListAdapter<Cat, CatAdapter.CatViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val binding = ItemCatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class CatViewHolder(private val binding: ItemCatBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
//                click
            }
        }

        fun bind(cat: Cat) {
            binding.apply {
                nameList.text = cat.name
                breedList.text = cat.breed
                ageList.text = cat.age
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