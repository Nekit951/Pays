package com.example.pays

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pays.databinding.ActivityBuyBinding
import com.example.pays.helpers.ManagmentCart
import com.google.firebase.database.FirebaseDatabase
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.MockConfiguration
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.TestParameters
import java.math.BigDecimal
import java.util.ArrayList


class BuyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyBinding
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managmentCart = ManagmentCart(this)

        initBuy()
        initTotal()
        buyClick()
    }

    private fun initBuy() {
        val cartItems = managmentCart.getListCart()

        binding.apply {
            recyclerViewBuy.layoutManager = LinearLayoutManager(this@BuyActivity,
                LinearLayoutManager.VERTICAL, false)

            recyclerViewBuy.adapter = BuyAdapter(cartItems)
        }
    }

    private fun initTotal(){
        val totalAmount = managmentCart.getTotalFee()
        binding.itemPrice.text = "$totalAmount руб."
        binding.finalPrice.text = "$totalAmount руб."
    }

    private fun buyClick() {
        binding.btnClick.setOnClickListener {
            val address = binding.address.text.toString().trim()
            val totalSum = managmentCart.getTotalFee()
            val cartItems = managmentCart.getListCart()
            val title = if (cartItems.isNotEmpty()) {
                cartItems.joinToString(separator = ", ") { it.title }
            } else {
                "Оплата заказа" // Резервное название, если корзина пуста
            }


            if (address.isEmpty()) {
                binding.address.error = "Пожалуйста, введите адрес доставки"
                Toast.makeText(this, "Введите адрес доставки!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentParameters = PaymentParameters(
                amount = Amount(BigDecimal(totalSum), java.util.Currency.getInstance("RUB")),
                title = title,
                subtitle = "Адрес доставки: ${address}",
                clientApplicationKey = "test_MTM3NDYyNOSa6MwUhsy-veW8FIfpP15tNqnqouc6p-A",
                shopId = "1374624",
                savePaymentMethod = SavePaymentMethod.ON,
                paymentMethodTypes = setOf(PaymentMethodType.BANK_CARD, PaymentMethodType.SBERBANK)
            )
            val testParameters = TestParameters(
                showLogs = true,
                mockConfiguration = MockConfiguration() // Заставляет SDK показать все методы оплаты
            )
            val intent = Checkout.createTokenizeIntent(
                context = this,
                paymentParameters = paymentParameters,
                testParameters = testParameters // Передаем параметры отладки
            )
           startActivityForResult(intent, 1001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val tokenizationResult = data?.let { Checkout.createTokenizationResult(it) }

                    // Извлекаем чистую строку токена. В Mock-режиме она вернет "mock_token_..."
                    val actualTokenString = tokenizationResult?.paymentToken

                    if (actualTokenString != null) {
                        val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
                        val userId = sharePref.getString("USER_ID", "") ?: ""
                        val database = FirebaseDatabase.getInstance().getReference("User")

                        database.child(userId).get().addOnSuccessListener { snapshot ->
                            if(snapshot.exists()){
                                val user = snapshot.getValue(UserModel::class.java)

                                if(user != null){
                                    createOrder(user, actualTokenString)
                                }
                                else{
                                    Toast.makeText(this, "Ошибка чтения профиля", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else {
                                Toast.makeText(this, "Профиль не найден в базе данных", Toast.LENGTH_SHORT).show()
                            }
                        }
                            .addOnFailureListener {
                                Toast.makeText(this, "Ошибка сети при загрузке профиля", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // Пользователь закрыл экран оплаты самостоятельно
                    Toast.makeText(this, "Оплата отменена", Toast.LENGTH_SHORT).show()
                }
                Checkout.RESULT_ERROR -> {
                    val errorDescription = data?.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION)
                    Log.e("YooKassa", "Ошибка SDK: $errorDescription")
                }
            }
        }
    }

    private fun createOrder(user: UserModel, actualTokenString: String){
        val cartItems = managmentCart.getListCart()
        val summa = managmentCart.getTotalFee()
        val database = FirebaseDatabase.getInstance().getReference("Orders")
        val orderId = database.push().key ?: ""
        val quantity = cartItems.sumOf { it.numberInCart }
        val address = binding.address.text.toString().trim()


        val newOrder = OrderModel(
            id = orderId,
            items = ArrayList(cartItems),
            user = user,
            summa = summa,
            quantity = quantity,
            address = address,
            paymentToken = actualTokenString,
            status = "pending"
        )

        database.child(orderId).setValue(newOrder).addOnSuccessListener {
            for(cartItem in cartItems){
                updateFire(cartItem.id, cartItem.numberInCart)
            }
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
            managmentCart.clearCart()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Ошибка создания заказа в БД", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFire(itemId: Int, itemQuantity: Int){
        val database = FirebaseDatabase.getInstance().getReference("Items")

        database.orderByChild("id").equalTo(itemId.toDouble()).get().addOnSuccessListener { snapshot ->
            if(snapshot.exists()){
                for(itemSnapshot in snapshot.children){
                    val currentStock = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    val newStock = (currentStock - itemQuantity).coerceAtLeast(0)

                    itemSnapshot.child("quantity").ref.setValue(newStock).addOnSuccessListener {
                        Log.d("FIREBASE_WRITE", "Успешно обновлено для ID $itemId. Новый остаток: $newStock")
                    }
                        .addOnFailureListener {
                            Log.e("FIREBASE_WRITE", "Ошибка записи для ID $itemId: ${it.message}")
                        }
                }
            }
            else{
                Log.e("FIREBASE_WRITE", "Товар с внутренним ID $itemId не найден ни в одном узле базы по пути Items")
            }
        }
            .addOnFailureListener {
                Log.e("FIREBASE_WRITE", "Ошибка выполнения запроса orderByChild: ${it.message}")
            }
    }
}
