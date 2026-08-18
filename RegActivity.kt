package com.example.pays

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class RegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg)

        val userName: EditText = findViewById(R.id.tvName)
        val userPhone: EditText = findViewById(R.id.tvPhone)
        val userEmail: EditText = findViewById(R.id.tvEmail)
        val button: Button = findViewById(R.id.btnReg)
        val auth: TextView = findViewById(R.id.auth)

        button.setOnClickListener {
            val name = userName.text.toString().trim()
            val phone = userPhone.text.toString().trim()
            val email = userEmail.text.toString().trim()

            if(name == "" || phone == "" || email == ""){
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            }
            else{
                val firebase = FirebaseDatabase.getInstance().getReference("User")
                val userId = firebase.push().key ?: ""

                val newUser = UserModel(userId, name, null, phone, email)

                firebase.child(userId).setValue(newUser).addOnSuccessListener {
                    userName.text.clear()
                    userPhone.text.clear()
                    userEmail.text.clear()

                    val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
                    with(sharePref.edit()) {
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
            }
        }

        auth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

    }
}