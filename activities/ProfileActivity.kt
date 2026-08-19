package com.example.pays.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pays.activities.ProfileEditActivity
import com.example.pays.R
import com.example.pays.activities.RegActivity
import com.example.pays.models.UserModel
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
        val btnEdit: ImageView = findViewById(R.id.buttonOptions)
        val btnExit: Button = findViewById(R.id.exit)
        val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val firebase = FirebaseDatabase.getInstance().getReference("User")
        val userId = intent.getStringExtra("USER_ID") ?: ""

        firebase.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    name.text = user?.name ?: "Имя не указано"
                    phone.text = user?.phone ?: "Имя не указано"
                    email.text = user?.email  ?: "Имя не указано"
                }
                else{
                    register()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_DEBUG", "Ошибка: ${error.message}")
            }
        })

        btnExit.setOnClickListener {
            val editor = sharePref.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register(){
        val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
        with(sharePref.edit()) {
            putBoolean("isLoggedIn", false)
            putString("USER_ID", "")
            apply()
        }

        val intent = Intent(this, RegActivity::class.java)
        startActivity(intent)
        finish()
    }
}