package com.example.pays

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val name: TextView = findViewById(R.id.fio_profile)
        val phone: TextView = findViewById(R.id.phone_profile)
        val email: TextView = findViewById(R.id.email_profile)
        val firebase = FirebaseDatabase.getInstance().getReference("User")
        val userId = intent.getStringExtra("USER_ID") ?: ""

        firebase.child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    name.text = user?.name ?: "Имя не указано"
                    phone.text = user?.phone ?: "Имя не указано"
                    email.text = user?.email  ?: "Имя не указано"
                }
                else{
                    Toast.makeText(applicationContext, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_DEBUG", "Ошибка: ${error.message}")
            }
        })
    }
}