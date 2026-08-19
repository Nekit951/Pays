package com.example.pays.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pays.activities.ProfileActivity
import com.example.pays.R
import com.example.pays.activities.RegActivity
import com.example.pays.models.UserModel
import com.google.firebase.database.FirebaseDatabase

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        val userName: EditText = findViewById(R.id.tvName)
        val userPhone: EditText = findViewById(R.id.tvPhone)
        val button: Button = findViewById(R.id.btnAuth)
        val reg: TextView = findViewById(R.id.reg)
        val database = FirebaseDatabase.getInstance().getReference("User")

        button.setOnClickListener {
            val name = userName.text.toString().trim()
            val phone = userPhone.text.toString().trim()

            if(name == "" || phone == ""){
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            database.orderByChild("phone").equalTo(phone).get().addOnSuccessListener{ snapshot ->
                val childSnapshot = snapshot.children.firstOrNull()

                if(childSnapshot != null){
                    val user = childSnapshot.getValue(UserModel::class.java)
                    if(user?.name == name){
                        val userId = user.id

                        val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
                        sharePref.edit().apply{
                            putBoolean("isLoggedIn", true)
                            putString("USER_ID", userId)
                            putString("USER_PHONE", phone)
                            apply()
                        }

                        val intent = Intent(this, ProfileActivity::class.java).apply {
                            putExtra("USER_ID", userId)
                        }
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, "Неверное имя пользователя", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this, "Пользователь с таким телефон не найден", Toast.LENGTH_SHORT).show()
                }
            }
                .addOnFailureListener {
                    Toast.makeText(this, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
        }

        reg.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }
    }
}