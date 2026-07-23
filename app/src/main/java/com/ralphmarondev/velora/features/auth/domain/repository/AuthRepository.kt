package com.ralphmarondev.velora.features.auth.domain.repository

import com.ralphmarondev.velora.core.domain.model.Result
import com.ralphmarondev.velora.features.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(user: User): Result<User>
}