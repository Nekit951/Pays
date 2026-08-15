package com.example.pays

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pays.databinding.ActivityMainBinding

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
}