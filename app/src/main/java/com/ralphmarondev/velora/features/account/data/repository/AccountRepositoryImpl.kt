package com.ralphmarondev.velora.features.account.data.repository

import com.ralphmarondev.velora.core.data.local.preferences.AppPreferences
import com.ralphmarondev.velora.core.data.network.FirebaseAuthService
import com.ralphmarondev.velora.core.data.network.FirebaseFirestoreService
import com.ralphmarondev.velora.features.account.domain.repository.AccountRepository
import com.ralphmarondev.velora.features.auth.domain.model.User
import kotlinx.coroutines.flow.first

class AccountRepositoryImpl(
    private val preferences: AppPreferences,
    private val firebaseAuthService: FirebaseAuthService,
    private val firebaseFirestoreService: FirebaseFirestoreService
) : AccountRepository {
    override suspend fun loadAccountInformation(): User {
        val user = User(
            uid = preferences.uid.first() ?: "",
            displayName = preferences.displayName.first() ?: "",
            email = preferences.email.first() ?: "",
            password = preferences.password.first() ?: "",
            imagePath = preferences.imagePath.first()
        )
        return user
    }

    override suspend fun updatePassword(newPassword: String) {
        val email = preferences.email.first()
            ?: throw Exception("No email found.")
        val password = preferences.password.first()
            ?: throw Exception("No password found.")

        firebaseAuthService.updatePassword(
            currentEmail = email,
            currentPassword = password,
            newPassword = newPassword
        )
        preferences.setPassword(newPassword)
    }

    override suspend fun updateImagePath(newPath: Int) {
        val uid = preferences.uid.first()
            ?: throw Exception("No uid found.")
        val user = firebaseFirestoreService.readUser(uid)
        val newUser = user.copy(imagePath = newPath)

        firebaseFirestoreService.updateUser(newUser)
        preferences.setImagePath(newUser.imagePath)
    }

    override suspend fun updateDisplayName(newDisplayName: String) {
        val uid = preferences.uid.first()
            ?: throw Exception("No uid found.")
        val user = firebaseFirestoreService.readUser(uid)
        val newUser = user.copy(displayName = newDisplayName)

        firebaseFirestoreService.updateUser(newUser)
        preferences.setDisplayName(newUser.displayName)
    }

    override suspend fun logout() {
        firebaseAuthService.logout()
        preferences.clear()
    }
}