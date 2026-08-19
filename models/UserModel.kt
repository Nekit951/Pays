package com.example.pays.models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class UserModel(val id: String = "", val name: String = "", val address: UserAddress? = null, val phone: String = "", val email: String = ""):
    Serializable