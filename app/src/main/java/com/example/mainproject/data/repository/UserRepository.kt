package com.example.mainproject.data.repository

import android.util.Log
import com.example.mainproject.data.model.Account
import com.example.mainproject.data.model.AccountBank
import com.example.mainproject.data.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getUserProfileFlow(userId: String): Flow<UserInfo?> = callbackFlow {
        val userRef = database.child("users").child(userId).child("profile")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userInfo = snapshot.getValue(UserInfo::class.java)
                trySend(userInfo).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        userRef.addValueEventListener(listener)
        awaitClose { userRef.removeEventListener(listener) }
    }

    suspend fun saveUserProfile(userId: String, userInfo: UserInfo): Result<Unit> {
        return try {
            database.child("users").child(userId).child("profile")
                .setValue(userInfo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAccount(userId: String): Flow<Account?> = callbackFlow {
        val accountRef = database.child("users").child(userId).child("accounts")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val account = snapshot.getValue(Account::class.java)
                trySend(account).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        accountRef.addValueEventListener(listener)
        awaitClose { accountRef.removeEventListener(listener) }
    }

    fun updateAccountBalance(account: Account, accountId: String, callback: (Boolean, String?) -> Unit) {
        database.child("users").child(account.userId).child("account").child(accountId)
            .setValue(account)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message)
            }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cập nhật hàm saveAccountBankInfo để lưu trực tiếp vào node BankAccount
    fun saveAccountBankInfo(accountBank: AccountBank, callback: (Boolean, String?) -> Unit) {
        val userId = accountBank.userId
        database.child("users").child(userId).child("BankAccount")
            .setValue(accountBank)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message)
            }
    }

    // Cập nhật hàm getAccountBankInfo để đọc trực tiếp từ node BankAccount
    fun getAccountBankInfo(userId: String): Flow<AccountBank?> = callbackFlow {
        val accountBankRef = database.child("users").child(userId).child("BankAccount")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseData", "BankAccount snapshot: ${snapshot.value}")
                val accountBank = snapshot.getValue(AccountBank::class.java)
                trySend(accountBank).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        accountBankRef.addValueEventListener(listener)
        awaitClose { accountBankRef.removeEventListener(listener) }
    }

    fun getAccountwithoutAccountId(userId: String): Flow<Account?> = callbackFlow { // Không cần accountId
        val accountRef = database.child("users").child(userId).child("account")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val account = snapshot.getValue(Account::class.java)
                trySend(account).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        accountRef.addValueEventListener(listener)
        awaitClose { accountRef.removeEventListener(listener) }
    }

    fun updateAccountBalance(account: Account, callback: (Boolean, String?) -> Unit) { // Không cần accountId
        database.child("users").child(account.userId).child("accounts").setValue(account)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message)
            }
    }

    // Hàm để lấy tham chiếu đến node BankAccount của người dùng
    fun getUserBankAccountRef(userId: String) = database.child("users").child(userId).child("BankAccount")
}