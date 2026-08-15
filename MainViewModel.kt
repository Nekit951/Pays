package com.example.pays

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel(): ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _items = MutableLiveData<List<ItemsModel>>()

    val items: LiveData<List<ItemsModel>> = _items

    fun loadItems(){
        val ref = firebaseDatabase.getReference("Items")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for(child in snapshot.children){
                    val model = child.getValue(ItemsModel::class.java)
                    if(model != null){
                        list.add(model)
                    }
                }
                _items.value = list
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}