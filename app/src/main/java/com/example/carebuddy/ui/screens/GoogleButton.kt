package com.example.carebuddy.ui.screens


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GoogleSignInButton() {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            // Abhi UI only — Google logic next step me add karenge
        }
    ) {
        Text("Sign in with Google")
    }
}
