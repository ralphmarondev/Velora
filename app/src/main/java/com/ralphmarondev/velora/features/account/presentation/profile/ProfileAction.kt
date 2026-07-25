package com.ralphmarondev.velora.features.account.presentation.profile

interface ProfileAction {
    data object LoadInformation : ProfileAction
    data object Refresh : ProfileAction
    data object Logout : ProfileAction
    data object SelectImage : ProfileAction
    data object ClearNavigation : ProfileAction
    data class ShowDisplayNameDialog(val value: Boolean) : ProfileAction
    data class UpdateDisplayName(val updatedDisplayName: String) : ProfileAction
    data class ShowEmailDialog(val value: Boolean) : ProfileAction
    data class UpdateEmail(val updatedEmail: String) : ProfileAction
    data class ShowPasswordDialog(val value: Boolean) : ProfileAction
    data class UpdatePassword(val updatedPassword: String) : ProfileAction
}