package com.example.pays

import java.io.Serializable

data class ItemsModel(val id: Int = 0, val title: String = "", val price: Double = 0.0, val quantity: Int = 0, val picUrl: String = "", var numberInCart: Int = 1): Serializable
