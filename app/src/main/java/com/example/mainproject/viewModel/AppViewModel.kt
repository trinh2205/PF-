package com.example.mainproject.viewModel

// AppViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserInfo(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val userId: String,
    val passwordHash: String,
    val isVerified: Boolean
) {
    companion object {
        fun fromDocument(document: DocumentSnapshot): UserInfo? {
            return if (document.exists()) {
                UserInfo(
                    uid = document.id,
                    email = document.getString("email"),
                    displayName = document.getString("displayName"),
                    userId = userId,
                    passwordHash = hashedPasswordFromDb,
                    isVerified = true
                )
            } else {
                null
            }
        }
    }
}

class AppViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    fun setCurrentUser(user: UserInfo?) {
        _currentUser.value = user
        _isLoggedIn.value = user != null
    }

    fun fetchCurrentUser(firebaseUser: FirebaseUser?) {
        firebaseUser?.let { user ->
            viewModelScope.launch {
                usersCollection.document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val userInfo = UserInfo.fromDocument(document)
                        setCurrentUser(userInfo)
                    }
                    .addOnFailureListener { e ->
                        // Xử lý lỗi khi lấy thông tin người dùng
                        setCurrentUser(UserInfo(
                            user.uid,
                            user.email,
                            user.displayName,
                            userId,
                            hashedPasswordFromDb,
                            true
                        )) // Fallback to FirebaseUser info
                        println("Error fetching user info from Firestore: $e")
                    }
            }
        } ?: setCurrentUser(null)
    }

    fun logout() {
        setCurrentUser(null)
        // Optionally, sign out from Firebase Authentication
        // FirebaseAuth.getInstance().signOut()
    }
}