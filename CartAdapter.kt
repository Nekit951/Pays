package com.example.pays

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pays.databinding.ViewholderCartBinding
import com.example.pays.helpers.ChangeNumberItemsListener
import com.example.pays.helpers.ManagmentCart

class CartAdapter(private val cartList: ArrayList<ItemsModel>, private val context: Context, var changeNumberItemsListener: ChangeNumberItemsListener? = null):
    RecyclerView.Adapter<CartAdapter.Viewholder>() {

    private val managmentCart = ManagmentCart(context)

    class Viewholder(val binding: ViewholderCartBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Viewholder {
        val binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(
        holder: Viewholder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = cartList[position]
        holder.binding.apply {
            title.text = item.title
            feeEachItemTxt.text = "${item.price} руб."
            totalEachItem.text = "${Math.round(item.numberInCart*item.price)} руб."
            countTxt.text = "${item.numberInCart} шт."

            Glide.with(holder.itemView.context).load(item.picUrl).into(picCart)

            btnAdd.setOnClickListener {
                managmentCart.plusItem(cartList, position, object: ChangeNumberItemsListener{
                    override fun onChanged() {
                        notifyDataSetChanged()
                        changeNumberItemsListener?.onChanged()
                    }
                })
            }

            btnDel.setOnClickListener {
                managmentCart.minusItem(cartList, position, object: ChangeNumberItemsListener{
                    override fun onChanged() {
                        notifyDataSetChanged()
                        changeNumberItemsListener?.onChanged()
                    }
                })
            }
        }
    }

    override fun getItemCount(): Int = cartList.size
}