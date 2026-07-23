package com.ralphmarondev.velora.features.auth.presentation.register

sealed interface RegisterAction {
    data object Register : RegisterAction
    data object Login : RegisterAction
    data class DisplayNameChange(val displayName: String) : RegisterAction
    data class EmailChange(val email: String) : RegisterAction
    data class PasswordChange(val password: String) : RegisterAction
    data class ConfirmPasswordChange(val confirmPassword: String) : RegisterAction
}