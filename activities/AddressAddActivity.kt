package com.example.pays.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pays.databinding.ActivityAddressAddBinding
import com.example.pays.models.UserAddress
import com.google.firebase.database.FirebaseDatabase

class AddressAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressAddBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddressAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAddress()
        addAddress()
    }

    private fun userAddress(){
        val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val userId = sharePref.getString("USER_ID", "") ?: ""

        val database = FirebaseDatabase.getInstance().getReference("User")

        if(userId.isNotEmpty()){
            database.child(userId).child("address").get().addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    val userAddress = snapshot.getValue(UserAddress::class.java)
                    binding.mapText.setText(userAddress?.city)
                    binding.mapPodiezd.setText(userAddress?.podiezd)
                    binding.mapEtazh.setText(userAddress?.etazh)
                    binding.mapKvartira.setText(userAddress?.kvartira)
                }
            }
        }
    }

    private fun addAddress() {
        binding.btnAdd.setOnClickListener {
            val city = binding.mapText.text.toString().trim()
            val podiezd = binding.mapPodiezd.text.toString().trim()
            val etazh = binding.mapEtazh.text.toString().trim()
            val kvartira = binding.mapKvartira.text.toString().trim()

            if(city == ""){
                Toast.makeText(this, "Заполните адрес", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
            val userId = sharePref.getString("USER_ID", "") ?: ""

            val firebase = FirebaseDatabase.getInstance().getReference("User")

            val newAddress =
                UserAddress(city = city, podiezd = podiezd, etazh = etazh, kvartira = kvartira)

            if(userId.isNotEmpty()){
                firebase.child(userId).child("address").setValue(newAddress).addOnSuccessListener {
                    Toast.makeText(this, "Адрес сохранён", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AddressActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Адрес не сохранён", Toast.LENGTH_SHORT).show()
                    }
            }
            else{
                Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
            }
        }
    }
}