package com.example.pays

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pays.databinding.ActivityCartBinding
import com.example.pays.helpers.ChangeNumberItemsListener
import com.example.pays.helpers.ManagmentCart
import com.google.firebase.database.FirebaseDatabase

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        initCart()
        calculatorCart()
        clickCart()
    }

    private fun initCart() {
        binding.apply {
            recyclerViewCart.layoutManager = LinearLayoutManager(
                this@CartActivity,
                LinearLayoutManager.VERTICAL, false
            )

            recyclerViewCart.adapter = CartAdapter(
                managmentCart.getListCart(), this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculatorCart()
                    }
                })

            if (managmentCart.getListCart().isEmpty()) {
                emptyStateLayout.visibility = View.VISIBLE
                recyclerViewCart.visibility = View.GONE
                layoutCheckout.visibility = View.GONE
            } else {
                emptyStateLayout.visibility = View.GONE
                recyclerViewCart.visibility = View.VISIBLE
                layoutCheckout.visibility = View.VISIBLE
            }
        }
    }

    private fun calculatorCart() {
        val total = managmentCart.getTotalFee()
        binding.totalPrice.text = String.format("%.2f руб.", total)
    }

    private fun clickCart() {
        binding.btnCheckout.setOnClickListener {
            val cartItems = managmentCart.getListCart()

            if (cartItems.isNotEmpty()) {
                checkQuantity(cartItems, 0)
            } else {
                Toast.makeText(this, "Ваша корзина пуста", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkQuantity(items: ArrayList<ItemsModel>, index: Int){
        val itemRef = items[index]
        val databaseReference = FirebaseDatabase.getInstance().getReference("Items")

        databaseReference.orderByChild("id").equalTo(itemRef.id.toDouble()).get().addOnSuccessListener { snapshot ->
            if(snapshot.exists()){
                for(itemSnaphot in snapshot.children){
                    val currentStock = itemSnaphot.child("quantity").getValue(Int::class.java) ?: 0
                    if(currentStock >= itemRef.numberInCart){
                        val intent = Intent(this, BuyActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, "Недостаточное количество товаров", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
            .addOnFailureListener {
                Log.e("FIREBASE_WRITE", "Ошибка получения данных: ${it.message}")
            }
    }
}