package com.example.mainproject.FireBase

import com.example.mainproject.Data.model.TransactionFirebase
import com.google.firebase.database.*

class TransactionRepository {
    private val dbRef = FirebaseDatabase.getInstance().getReference("transactions")

    fun addTransaction(transaction: TransactionFirebase) {
        val id = dbRef.push().key!!
        transaction.id = id
        dbRef.child(id).setValue(transaction)
    }

    fun getTransactions(callback: (List<TransactionFirebase>) -> Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<TransactionFirebase>()
                for (item in snapshot.children) {
                    val tx = item.getValue(TransactionFirebase::class.java)
                    tx?.let { list.add(it) }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun deleteTransaction(id: String) {
        dbRef.child(id).removeValue()
    }
}
