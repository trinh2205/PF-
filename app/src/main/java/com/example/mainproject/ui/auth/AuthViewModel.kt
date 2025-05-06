package com.example.mainproject.ui.auth

import androidx.lifecycle.ViewModel
import com.example.mainproject.Data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signUp(email: String, password: String,fullName:String, phone: String) {
        _authState.update { currentState ->
            currentState.copy(signUpState = SignUpState(isLoading = true))
        }
        repository.signUp(email, password, fullName, phone) { success, error, emailResult, passwordResult ->
            _authState.update { currentState ->
                if (success) {
                    currentState.copy(
                        signUpState = SignUpState(
                            isSuccess = true,
                            email = emailResult,
                            password = passwordResult,
                            isLoading = false
                        ),
                        signInState = SignInState()
                    )
                } else {
                    currentState.copy(
                        signUpState = SignUpState(
                            errorMessage = error,
                            isLoading = false
                        )
                    )
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        _authState.update { currentState ->
            currentState.copy(signInState = SignInState(isLoading = true))
        }
        repository.signIn(email, password) { success, error, firebaseUser -> // Thay đổi callback để nhận FirebaseUser
            _authState.update { currentState ->
                currentState.copy(
                    signInState = SignInState(
                        isSuccess = success,
                        errorMessage = error,
                        isLoading = false,
                        firebaseUser = firebaseUser // Lưu trữ FirebaseUser
                    )
                )
            }
        }
    }

    fun setSignUpError(errorMessage: String) {
        _authState.update { currentState ->
            currentState.copy(
                signUpState = SignUpState(
                    errorMessage = errorMessage,
                    isLoading = false
                )
            )
        }
    }
}

data class AuthState(
    val signUpState: SignUpState = SignUpState(),
    val signInState: SignInState = SignInState()
)

data class SignUpState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val email: String? = null,
    val password: String? = null
)

data class SignInState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val firebaseUser: FirebaseUser? = null // Thêm FirebaseUser vào SignInState
)