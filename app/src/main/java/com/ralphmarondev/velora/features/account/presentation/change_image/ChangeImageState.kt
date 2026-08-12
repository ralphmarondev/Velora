package com.ralphmarondev.velora.features.account.presentation.change_image

data class ChangeImageState(
    val selectedImage: Int = 1,
    val images: List<Int> = emptyList()
)