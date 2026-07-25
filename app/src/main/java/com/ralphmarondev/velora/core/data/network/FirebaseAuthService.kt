package com.ralphmarondev.velora.core.data.network

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val firebaseAuth: FirebaseAuth
) {
    // return User.uid
    suspend fun login(email: String, password: String): String? {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user
        Log.d(
            "FirebaseAuthService",
            "Login UID: `${firebaseUser?.uid}`, email: `${firebaseUser?.email}`"
        )
        return firebaseUser?.uid
    }

    // return User.uid
    suspend fun register(email: String, password: String): String? {
        val result = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
        val firebaseUser = result.user
        Log.d(
            "FirebaseAuthService",
            "Register UID: `${firebaseUser?.uid}`, email: `${firebaseUser?.email}`"
        )
        return firebaseUser?.uid
    }

    suspend fun updatePassword(
        currentEmail: String,
        currentPassword: String,
        newPassword: String
    ) {
        val user = firebaseAuth.currentUser
            ?: throw Exception("No authenticated user.")

        val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)
        user.reauthenticate(credential).await()
        user.updatePassword(newPassword).await()
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}