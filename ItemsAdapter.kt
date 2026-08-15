package com.example.pays

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.pays.databinding.ViewholderItemsBinding

class ItemsAdapter(private val items: MutableList<ItemsModel>): RecyclerView.Adapter<ItemsAdapter.Viewholder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateDate(newData: List<ItemsModel>){
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
        Log.d("ITEMS_DEBUG", "Адаптер обновил данные. Первый элемент: ${items.firstOrNull()?.quantity}")
    }

    class Viewholder(val binding: ViewholderItemsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemsAdapter.Viewholder {
        val binding = ViewholderItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: ItemsAdapter.Viewholder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            title.text = item.title
            price.text = "${item.price} руб."
            quantity.text = "${item.quantity} шт."

            Glide.with(holder.itemView.context).load(item.picUrl).apply(RequestOptions().transform(
                CenterCrop())).into(pic)

            root.setOnClickListener {
                val intent = Intent(holder.itemView.context, DescActivity::class.java)
                intent.putExtra("object", item)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}