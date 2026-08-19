package com.example.pays.models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class OrderModel(val id: String = "", val items: ArrayList<ItemsModel> = ArrayList(), val user: UserModel? = null, val summa: Double = 0.0, val quantity: Int = 0, val address: String = "", val paymentToken: String = "",
                      val status: String = "pending"): Serializable