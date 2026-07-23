package com.ralphmarondev.velora.features.auth.domain.model

data class User(
    val uid: String = "",
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val imagePath: Int = 1,
    val createDate: Long = System.currentTimeMillis()
)