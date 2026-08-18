package com.example.pays

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pays.OrderActivity
import com.example.pays.databinding.ActivityAddressBinding
import com.example.pays.databinding.ActivityOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private var addressAdapter = AddressAdapter(mutableListOf(), onDeleteClick = {})
    private var addressList = mutableListOf<UserAddress>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecycleView()
        initAddress()
        addAddress()
    }

    private fun setUpRecycleView() {
        val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val userId = sharePref.getString("USER_ID", "") ?: ""
        val database = FirebaseDatabase.getInstance().getReference("User")
        addressAdapter = AddressAdapter(addressList) {
            if(userId.isNotEmpty()){
                database.child(userId).child("address").removeValue().addOnSuccessListener {
                    Toast.makeText(this, "Адрес удалён", Toast.LENGTH_SHORT).show()
                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Адрес не удалён", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        binding.recyclerViewAddress.layoutManager = LinearLayoutManager(this@AddressActivity,
            LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewAddress.adapter = addressAdapter
    }

    private fun initAddress() {
        binding.apply {
            val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
            val userId = sharePref.getString("USER_ID", "") ?: ""
            val database = FirebaseDatabase.getInstance().getReference("User")

            if(userId.isNotEmpty()){
                database.child(userId).child("address").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val userAddress = snapshot.getValue(UserAddress::class.java)
                            if(userAddress != null){
                                addressList.add(userAddress)
                            }
                        }
                        addressAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        android.util.Log.e("DATABASE_ERROR", "Ошибка чтения адреса: ${error.message}")
                    }

                })
            }
        }
    }

    private fun addAddress(){
        binding.addAddressView.setOnClickListener {
            val intent = Intent(this, AddressAddActivity::class.java)
            startActivity(intent)
        }
    }

}