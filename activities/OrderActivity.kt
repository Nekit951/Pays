package com.example.pays.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pays.adapters.OrderAdapter
import com.example.pays.databinding.ActivityOrderBinding
import com.example.pays.models.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private var orderAdapter = OrderAdapter(mutableListOf())
    private var orderList = mutableListOf<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecycleView()
        initOrder()
    }

    private fun setUpRecycleView() {
        orderAdapter = OrderAdapter(orderList)
        binding.recyclerViewOrder.layoutManager = LinearLayoutManager(
            this@OrderActivity,
            LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerViewOrder.adapter = orderAdapter
    }

    private fun initOrder() {
        binding.apply {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Orders")
            databaseReference.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orderList.clear()

                    for(orderSnapshot in snapshot.children){
                        val order = orderSnapshot.getValue(OrderModel::class.java)
                        if(order != null){
                            orderList.add(order)
                        }
                    }

                    orderAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@OrderActivity, "Ошибка загрузки: ${error.message}", Toast.LENGTH_LONG).show()
                }

            })
        }
    }
}