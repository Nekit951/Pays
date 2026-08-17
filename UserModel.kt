package com.example.pays

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class UserModel(val id: String = "", val name: String = "", val phone: String = "", val email: String = ""): Serializable