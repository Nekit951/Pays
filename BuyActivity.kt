package com.example.pays

import android.content.Intent
import android.icu.util.Currency
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pays.databinding.ActivityBuyBinding
import com.example.pays.helpers.ManagmentCart
import com.google.firebase.database.FirebaseDatabase
import okhttp3.Address
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizeIntent
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.TestParameters
import java.math.BigDecimal


class BuyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyBinding
    private lateinit var managmentCart: ManagmentCart

    private val checkoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val tokenizationResult = data?.let { Checkout.createTokenizationResult(it) }
            val paymentToken = tokenizationResult?.paymentToken

            if (paymentToken != null) {
                // Только теперь, когда токен у нас, сохраняем заказ в БД
                saveOrderToFirebase(paymentToken)
            } else {
                Toast.makeText(this, "Ошибка: Токен оплаты не получен", Toast.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Оплата отменена", Toast.LENGTH_SHORT).show()
        }
    }

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
            val cartItems = managmentCart.getListCart()
//            val summa = managmentCart.getTotalFee()
//            val quantity = cartItems.sumOf { it.numberInCart }
            val address = binding.address.text.toString().trim()
//            val database = FirebaseDatabase.getInstance().getReference("Orders")
//            val orderId = database.push().key ?: return@setOnClickListener

            if (address.isEmpty()) {
                binding.address.error = "Пожалуйста, введите адрес доставки"
                Toast.makeText(this, "Введите адрес доставки!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startTokenize()

//            if (orderId != null) {
//                val newOrder = OrderModel(orderId, items = ArrayList(cartItems), summa, quantity, address)
//                database.child(orderId).setValue(newOrder).addOnSuccessListener {
//                    for (cartItem in cartItems) {
//                        updateFire(cartItem.id, cartItem.numberInCart)
//                    }
//                    val intent = Intent(this, OrderActivity::class.java)
//                    startActivity(intent)
//                    managmentCart.clearCart()
//                    finish()
//                }
//            } else {
//                Toast.makeText(this, "Ваша корзина пуста", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    fun startTokenize(){
        val totalSum = managmentCart.getTotalFee()
        val paymentParameters = PaymentParameters(
            amount = Amount(BigDecimal(totalSum), java.util.Currency.getInstance("RUB")),
            title = "Название",
            subtitle = "Описание товара",
            clientApplicationKey = "test_MTM3NDYyNOSa6MwUhsy-veW8FIfpP15tNqnqouc6p-A",
            shopId = "1374624",
            savePaymentMethod = SavePaymentMethod.OFF,
            paymentMethodTypes = setOf(PaymentMethodType.YOO_MONEY, PaymentMethodType.BANK_CARD,
                PaymentMethodType.SBERBANK),
            gatewayId = "gatewayId"
        )
        val intent = createTokenizeIntent(this, paymentParameters)
        startActivityForResult(intent, 1001)
    }

    private fun saveOrderToFirebase(paymentToken: String) {
        val cartItems = managmentCart.getListCart()
        val summa = managmentCart.getTotalFee()
        val quantity = cartItems.sumOf { it.numberInCart }
        val address = binding.address.text.toString().trim()

        val database = FirebaseDatabase.getInstance().getReference("Orders")
        val orderId = database.push().key ?: return

        // ВАЖНО: Добавьте поле paymentToken и status в вашу OrderModel,
        // либо передавайте HashMap, чтобы Node.js сервер видел токен и статус "pending"!
        val newOrder = OrderModel(
            id = orderId,
            items = ArrayList(cartItems),
            summa = summa,
            quantity = quantity,
            address = address,
            paymentToken = paymentToken,
            status = "pending"
        )

        database.child(orderId).setValue(newOrder).addOnSuccessListener {
            for (cartItem in cartItems) {
                updateFire(cartItem.id, cartItem.numberInCart)
            }

            // Переходим на экран успешного оформления / ожидания
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