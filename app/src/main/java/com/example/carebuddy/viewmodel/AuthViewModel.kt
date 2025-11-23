package com.example.carebuddy.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val currentUser = MutableStateFlow(auth.currentUser)

    fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        _error.value = null
        _isLoading.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    currentUser.value = auth.currentUser
                    onSuccess()
                } else {
                    _error.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }

    fun registerWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        _error.value = null
        _isLoading.value = true

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    currentUser.value = auth.currentUser
                    onSuccess()
                } else {
                    _error.value = task.exception?.localizedMessage ?: "Signup failed"
                }
            }
    }

    fun handleGoogleToken(
        idToken: String,
        onSuccess: () -> Unit
    ) {
        _error.value = null
        _isLoading.value = true

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    currentUser.value = auth.currentUser
                    onSuccess()
                } else {
                    _error.value = task.exception?.localizedMessage ?: "Google sign-in failed"
                }
            }
    }

    fun logout(onLoggedOut: () -> Unit) {
        auth.signOut()
        currentUser.value = null
        onLoggedOut()
    }

    fun clearError() {
        _error.value = null
    }
}


