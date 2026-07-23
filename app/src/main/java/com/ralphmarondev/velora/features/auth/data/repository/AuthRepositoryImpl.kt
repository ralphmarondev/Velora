package com.ralphmarondev.velora.features.auth.data.repository

import android.util.Log
import com.ralphmarondev.velora.core.data.local.preferences.AppPreferences
import com.ralphmarondev.velora.core.data.network.FirebaseAuthService
import com.ralphmarondev.velora.core.data.network.FirebaseFirestoreService
import com.ralphmarondev.velora.core.domain.model.Result
import com.ralphmarondev.velora.features.auth.domain.model.User
import com.ralphmarondev.velora.features.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val preferences: AppPreferences,
    private val firebaseAuthService: FirebaseAuthService,
    private val firebaseFirestoreService: FirebaseFirestoreService
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        val userUid = firebaseAuthService.login(email, password)

        if (userUid.isNullOrEmpty()) {
            Log.e("Auth", "Login Failed.")
            return Result.Error("Login Failed.")
        }
        val user = firebaseFirestoreService.readUser(userUid)
        Log.d("Auth", "Login uid: ${user.uid}, displayName: ${user.displayName}")
        preferences.setUid(user.uid)
        preferences.setDisplayName(user.displayName)
        preferences.setEmail(user.email)
        preferences.setImagePath(user.imagePath)
        preferences.setPassword(password)
        return Result.Success(user)
    }

    override suspend fun register(user: User): Result<User> {
        val userUid = firebaseAuthService.register(user.email, user.password)
        if (userUid.isNullOrEmpty()) {
            Log.e("Auth", "Registration failed.")
            return Result.Error("Registration failed.")
        }

        firebaseFirestoreService.createUser(
            user = user.copy(uid = userUid)
        )
        Log.d("Auth", "Register uid: ${user.uid}, displayName: ${user.displayName}")
        preferences.setUid(userUid)
        preferences.setDisplayName(user.displayName)
        preferences.setEmail(user.email)
        preferences.setImagePath(user.imagePath)
        preferences.setPassword(user.password)
        return Result.Success(user.copy(uid = userUid))

    }
}