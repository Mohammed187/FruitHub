package com.example.fruithub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fruithub.databinding.ItemModelBinding
import com.example.fruithub.model.Item
import com.example.fruithub.viewmodel.FirestoreViewModel

class ItemsAdapter(
    private val clickListener: ItemClickListener,
    private val viewModel: FirestoreViewModel
) :
    ListAdapter<Item, ItemsAdapter.ItemViewHolder>(ItemCallBack) {

    class ItemViewHolder(private val binding: ItemModelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemModel: Item, clickListener: ItemClickListener, viewModel: FirestoreViewModel) {
            binding.item = itemModel
            binding.clickListener = clickListener
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = ItemModelBinding.inflate(LayoutInflater.from(parent.context))
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemModel = getItem(position)
        holder.bind(itemModel, clickListener, viewModel)
    }
}

object ItemCallBack : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }
}

class ItemClickListener(val clickListener: (itemId: String) -> Unit) {
    fun onClick(id: String) = clickListener(id)
}
