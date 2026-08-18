package com.example.pays

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.pays.databinding.ActivityDescBinding
import com.example.pays.helpers.ManagmentCart

class DescActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDescBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDescBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        item = intent.getSerializableExtra("object")!! as ItemsModel

        setUpViews()
        addToCart()
    }

    private fun setUpViews() = with(binding) {
        itemTitle.text = item.title
        itemPrice.text = "${item.price} руб."
        itemQuantity.text = "${item.quantity} шт."

        Glide.with(this@DescActivity).load(item.picUrl).into(picMain)
    }

    private fun addToCart(){
        binding.buttonCart.setOnClickListener {
            val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
            val isLoggedIn = sharePref.getBoolean("isLoggedIn", false)
            val savedUserId = sharePref.getString("USER_ID", "") ?: ""

            if(isLoggedIn && savedUserId.isNotEmpty()){
                item.numberInCart = 1
                managmentCart.insertFood(item)
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, RegActivity::class.java)
                startActivity(intent)
            }
        }
    }
}