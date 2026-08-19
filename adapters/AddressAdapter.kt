package com.example.pays.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pays.databinding.ViewholderAddressBinding
import com.example.pays.models.UserAddress

class AddressAdapter(private val addressList: MutableList<UserAddress>, private val onDeleteClick: () -> Unit): RecyclerView.Adapter<AddressAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderAddressBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Viewholder {
        val binding = ViewholderAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val address = addressList[position]

        holder.binding.apply {
            city.text = address.city

            if(address.podiezd == ""){
                podiezd.visibility = View.GONE
            }
            else{
                podiezd.visibility = View.VISIBLE
                podiezd.text = address.podiezd.toString()
            }

            if(address.etazh == ""){
                etazh.visibility = View.GONE
            }
            else{
                etazh.visibility = View.VISIBLE
                etazh.text = address.etazh.toString()
            }

            if(address.kvartira == ""){
                kvartira.visibility = View.GONE
            }
            else{
                kvartira.visibility = View.VISIBLE
                kvartira.text = address.kvartira.toString()
            }

            buttonOptions.setOnClickListener {
                onDeleteClick()
            }
        }
    }

    override fun getItemCount(): Int = addressList.size
}