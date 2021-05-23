package com.example.fruithub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fruithub.databinding.BasketModelBinding
import com.example.fruithub.model.Basket

class BasketAdapter(val onItemSwipeListener: OnItemSwipeListener) :
    ListAdapter<Basket, BasketAdapter.BasketViewHolder>(BasketCallBack) {

    class BasketViewHolder(private val binding: BasketModelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(basketModel: Basket) {
            binding.basket = basketModel

            binding.executePendingBindings()
        }
    }

    fun getBasketItemAt(position: Int): Basket {
        return getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        val view = BasketModelBinding.inflate(LayoutInflater.from(parent.context))
        return BasketViewHolder(view)
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        val basketModel = getItem(position)
        holder.bind(basketModel)
    }
}

object BasketCallBack : DiffUtil.ItemCallback<Basket>() {
    override fun areItemsTheSame(oldItem: Basket, newItem: Basket): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Basket, newItem: Basket): Boolean {
        return oldItem.id == newItem.id
    }
}

interface OnItemSwipeListener {
    fun onItemSwipe(basket: Basket)
}











