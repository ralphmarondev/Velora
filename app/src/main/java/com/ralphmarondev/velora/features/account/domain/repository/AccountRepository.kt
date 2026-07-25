package com.ralphmarondev.velora.features.account.domain.repository

import com.ralphmarondev.velora.features.auth.domain.model.User

interface AccountRepository {
    suspend fun loadAccountInformation(): User
    suspend fun updatePassword(newPassword: String)
    suspend fun updateImagePath(newPath: Int)
    suspend fun updateDisplayName(newDisplayName: String)
    suspend fun logout()
}