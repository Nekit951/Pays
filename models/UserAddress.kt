package com.example.pays.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserAddress(val city: String = "", val podiezd: String = "", val etazh: String = "", val kvartira: String = "")