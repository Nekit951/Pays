package com.example.pays

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pays.databinding.ViewholderOrderBinding

class OrderAdapter(private val orderList: MutableList<OrderModel>): RecyclerView.Adapter<OrderAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderOrderBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderAdapter.Viewholder {
        val binding = ViewholderOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: OrderAdapter.Viewholder, position: Int) {
        val order = orderList[position]

        val allProductName = order.items.joinToString(", ") { it.title }
        val allQuantity = order.items.sumOf { it.numberInCart }

        holder.binding.apply {
            title.text = allProductName
            finalPrice.text = "${order.summa} руб."
            countTxt.text = "$allQuantity шт."

            Glide.with(holder.itemView.context).load(order.items[0].picUrl).into(picOrder)

            root.setOnClickListener {
                val intent = Intent(holder.itemView.context, OrderDescActivity::class.java)
                intent.putExtra("object", order)
                holder.itemView.context.startActivity(intent)
            }
        }


    }

    override fun getItemCount(): Int = orderList.size
}