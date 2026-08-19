package com.example.pays.helpers

import android.content.Context
import android.widget.Toast
import com.example.pays.models.ItemsModel

class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertFood(item: ItemsModel) {
        var listFood = getListCart()
        val existAlready = listFood.any { it.title == item.title }
        val index = listFood.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listFood[index].numberInCart = item.numberInCart
        } else {
            listFood.add(item)
        }
        tinyDB.putListObject("CartList", listFood as ArrayList<ItemsModel?>)
        Toast.makeText(context, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("CartList") as ArrayList<ItemsModel>
    }

    fun minusItem(listFood: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listFood[position].numberInCart == 1) {
            listFood.removeAt(position)
        } else {
            listFood[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listFood as ArrayList<ItemsModel?>)
        listener.onChanged()
    }

    fun plusItem(listFood: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listFood[position].numberInCart++
        tinyDB.putListObject("CartList", listFood as ArrayList<ItemsModel?>)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listFood = getListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.price * item.numberInCart
        }
        return fee
    }

    fun clearCart() {
        tinyDB.putListObject("CartList", ArrayList<ItemsModel>() as ArrayList<ItemsModel?>)
    }
}