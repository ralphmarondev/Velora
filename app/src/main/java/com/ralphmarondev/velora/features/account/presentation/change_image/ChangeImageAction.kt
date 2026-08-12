package com.ralphmarondev.velora.features.account.presentation.change_image

sealed interface ChangeImageAction {
    data class SelectedImageChanged(val newImage: Int) : ChangeImageAction
}