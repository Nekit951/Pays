package com.example.pays

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pays.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy{
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding

    private var itemsAdapter = ItemsAdapter(mutableListOf())
    private var itemList = mutableListOf<ItemsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initItems()
        reg()
        address()
    }

    private fun initItems() {
        binding.apply {
            recyclerViewItems.layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            recyclerViewItems.adapter = itemsAdapter

            viewModel.items.observe(this@MainActivity) { data ->
                itemList = data.toMutableList()
                itemsAdapter.updateDate(ArrayList(data))
            }
            viewModel.loadItems()
        }
    }

    private fun reg(){
        binding.btnReg.setOnClickListener {
            val sharePref = getSharedPreferences("UserPref", MODE_PRIVATE)
            val isLoggedIn = sharePref.getBoolean("isLoggedIn", false)
            val savedUserId = sharePref.getString("USER_ID", "") ?: ""
            if(isLoggedIn && savedUserId.isNotEmpty()){
                val intent = Intent(this, ProfileActivity::class.java).apply{
                    putExtra("USER_ID", savedUserId)
                }
                startActivity(intent)
            }
            else{
                val intent = Intent(this, RegActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun address(){
        binding.btnAddress.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            startActivity(intent)
        }
    }
}