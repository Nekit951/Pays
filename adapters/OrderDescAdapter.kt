package com.example.pays.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pays.databinding.ViewholderOrderDescBinding
import com.example.pays.models.ItemsModel

class OrderDescAdapter(private val orderDescList: ArrayList<ItemsModel>): RecyclerView.Adapter<OrderDescAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderOrderDescBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Viewholder {
        val binding = ViewholderOrderDescBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val orderDesc = orderDescList[position]

        holder.binding.apply {
            title.text = orderDesc.title
            countTxt.text = "${orderDesc.numberInCart} шт."

            Glide.with(holder.itemView.context).load(orderDesc.picUrl).into(picOrderDesc)
        }
    }

    override fun getItemCount(): Int = orderDescList.size
}