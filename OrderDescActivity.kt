package com.example.pays

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pays.databinding.ActivityOrderDescBinding
import com.example.pays.helpers.ManagmentCart
import com.google.firebase.database.FirebaseDatabase

class OrderDescActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDescBinding
    private lateinit var managmentCart: ManagmentCart
    private var currentOrder: OrderModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDescBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managmentCart = ManagmentCart(this)


        initOrderDetail()
        click()
    }

    private fun initOrderDetail() {
        // 1. Пытаемся получить объект заказа, если перешли из списка OrderAdapter
        currentOrder = intent.getSerializableExtra("object") as? OrderModel
        val order = currentOrder

        // 2. Определяем данные в зависимости от того, откуда пришли
        val orderedItems = order?.items ?: managmentCart.getListCart()
        val totalPrice = order?.summa ?: managmentCart.getTotalFee()

        // 3. Вытаскиваем адрес: либо из объекта заказа, либо из прямой строки (из BuyActivity)
        val userAddress = order?.address ?: intent.getStringExtra("USER_ADDRESS_EXTRA") ?: "Адрес не указан"

        // 4. Заполняем UI
        binding.apply {
            recyclerViewOrderDesc.layoutManager = LinearLayoutManager(
                this@OrderDescActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            recyclerViewOrderDesc.adapter = OrderDescAdapter(orderedItems)

            finalPrice.text = "$totalPrice руб."
            adress.text = userAddress

            // Элементы оформления шагов заказа
            lineStep2.setBackgroundColor(Color.parseColor("#0000FF"))
            ivStep3.setBackgroundResource(R.drawable.step_done_circle)
            tvStep3Text.setTextColor(Color.BLACK)
        }
    }

    private fun click() {
        binding.btnClick.setOnClickListener {
            val order = currentOrder

            if (order == null || order.id.isEmpty()) {
                Toast.makeText(this, "Невозможно отменить этот заказ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Возвращаем все товары из этого заказа обратно на склад
            for (cartItem in order.items) {
                restoreStock(cartItem.id, cartItem.numberInCart)
            }

            // 2. Удаляем сам заказ из узла "Orders" по его ID
            val database = FirebaseDatabase.getInstance().getReference("Orders")
            database.child(order.id).removeValue()
                .addOnSuccessListener {
                    Log.d("FIREBASE_WRITE", "Заказ ${order.id} успешно удален")
                    Toast.makeText(this, "Заказ успешно отменен", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("FIREBASE_WRITE", "Ошибка удаления заказа: ${e.message}")
                    Toast.makeText(this, "Не удалось отменить заказ: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    // Вспомогательный метод для возврата товара на склад (аналог вашего updateFire, но с плюсом)
    private fun restoreStock(itemId: Int, itemQuantity: Int) {
        val database = FirebaseDatabase.getInstance().getReference("Items")

        database.orderByChild("id").equalTo(itemId.toDouble()).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (itemSnapshot in snapshot.children) {
                    val currentStock = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    val newStock = currentStock + itemQuantity // ПРИБАВЛЯЕМ товары обратно на склад

                    itemSnapshot.child("quantity").ref.setValue(newStock)
                        .addOnSuccessListener {
                            Log.d("FIREBASE_WRITE", "Склад обновлен (возврат). ID $itemId. Новый остаток: $newStock")
                        }
                }
            }
        }.addOnFailureListener { e ->
            Log.e("FIREBASE_WRITE", "Ошибка восстановления склада для ID $itemId: ${e.message}")
        }
    }
}