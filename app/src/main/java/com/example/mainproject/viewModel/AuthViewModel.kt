//package com.example.mainproject.viewModel
//
//import androidx.lifecycle.ViewModel
//import com.example.mainproject.Data.repository.AuthRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//
//class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
//    private val _authState = MutableStateFlow(AuthState())
//    val authState: StateFlow<AuthState> = _authState.asStateFlow()
//
//    fun signUp(email: String, password: String, fullName: String, phone: String = "", dob: String = "") {
//        _authState.update { it.copy(signUpState = SignUpState(isLoading = true)) }
//        repository.signUp(email, password, fullName, phone, dob) { success, error, emailResult, passwordResult ->
//            if (success) {
//                _authState.update {
//                    it.copy(
//                        signUpState = SignUpState(isSuccess = true, email = emailResult, password = passwordResult),
//                        signInState = SignInState() // Reset signInState
//                    )
//                }
//            } else {
//                _authState.update {
//                    it.copy(signUpState = SignUpState(errorMessage = error))
//                }
//            }
//        }
//    }
//
//    fun signIn(email: String, password: String) {
//        _authState.update { it.copy(signInState = SignInState(isLoading = true)) }
//        repository.signIn(email, password) { success, error ->
//            _authState.update {
//                it.copy(
//                    signInState = SignInState(
//                        isSuccess = success,
//                        errorMessage = error
//                    )
//                )
//            }
//        }
//    }
//}
//
//data class AuthState(
//    val signUpState: SignUpState = SignUpState(),
//    val signInState: SignInState = SignInState()
//)
//
//data class SignUpState(
//    val isLoading: Boolean = false,
//    val isSuccess: Boolean = false,
//    val errorMessage: String? = null,
//    val email: String? = null,
//    val password: String? = null
//)
//
//data class SignInState(
//    val isLoading: Boolean = false,
//    val isSuccess: Boolean = false,
//    val errorMessage: String? = null
//)