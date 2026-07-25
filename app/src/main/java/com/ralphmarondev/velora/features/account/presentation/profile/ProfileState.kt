package com.ralphmarondev.velora.features.account.presentation.profile

data class ProfileState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null,
    val showErrorMessage: Boolean = false,

    val displayName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val imagePath: Int = 1,

    val showDisplayNameDialog: Boolean = false,
    val showEmailDialog: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val selectImage: Boolean = false
)