package com.example.fruithub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fruithub.databinding.OrderModelBinding
import com.example.fruithub.model.Order

class OrdersAdapter : ListAdapter<Order, OrdersAdapter.OrderViewHolder>(OrderCallBack) {

    class OrderViewHolder(private val binding: OrderModelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderModel: Order) {
            binding.order = orderModel

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = OrderModelBinding.inflate(LayoutInflater.from(parent.context))
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderModel = getItem(position)
        holder.bind(orderModel)
    }
}

object OrderCallBack : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.id == newItem.id
    }


}
