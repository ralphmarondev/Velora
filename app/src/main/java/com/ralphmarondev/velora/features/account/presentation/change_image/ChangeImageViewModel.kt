package com.ralphmarondev.velora.features.account.presentation.change_image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralphmarondev.velora.features.account.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangeImageViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChangeImageState())
    val state = _state.asStateFlow()

    init {
        loadImages()
    }

    fun onAction(action: ChangeImageAction) {
        when (action) {
            is ChangeImageAction.SelectedImageChanged -> selectedImageChange(action.newImage)
        }
    }

    private fun selectedImageChange(newImage: Int) {
        viewModelScope.launch {
            _state.update { it.copy(selectedImage = newImage) }
            accountRepository.updateImagePath(newImage)
        }
    }

    private fun loadImages() {
        viewModelScope.launch {
            val account = accountRepository.loadAccountInformation()
            val images = listOf(1, 2, 3, 4, 5)
            _state.update {
                it.copy(
                    images = images,
                    selectedImage = account.imagePath
                )
            }
        }
    }
}