package com.example.pays

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pays.databinding.ViewholderBuyBinding

class BuyAdapter(private val buyList: ArrayList<ItemsModel>): RecyclerView.Adapter<BuyAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderBuyBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BuyAdapter.Viewholder {
        val binding = ViewholderBuyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: BuyAdapter.Viewholder, position: Int) {
        val buy = buyList[position]
        holder.binding.apply {
            title.text = buy.title
            totalEachItem.text = "${Math.round(buy.numberInCart*buy.price)} руб."
            countTxt.text = "${buy.numberInCart} шт."

            Glide.with(holder.itemView.context).load(buy.picUrl).into(picBuy)
        }
    }

    override fun getItemCount(): Int = buyList.size
}