package com.ralphmarondev.velora.features.account.presentation.profile


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralphmarondev.velora.features.account.domain.repository.AccountRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        loadAccountInformation()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.ShowEmailDialog -> showEmailDialog(action.value)
            is ProfileAction.ShowPasswordDialog -> showPasswordDialog(action.value)
            is ProfileAction.ShowDisplayNameDialog -> showDisplayNameDialog(action.value)
            is ProfileAction.UpdateDisplayName -> updateDisplayName(action.updatedDisplayName)
            is ProfileAction.UpdateEmail -> updateEmail(action.updatedEmail)
            is ProfileAction.UpdatePassword -> updatePassword(action.updatedPassword)
            ProfileAction.SelectImage -> selectImage()
            ProfileAction.ClearNavigation -> clearNavigation()
            ProfileAction.Refresh -> loadAccountInformation(isRefreshing = true)
            ProfileAction.Logout -> logout()
            ProfileAction.LoadInformation -> loadAccountInformation()
        }
    }

    private fun showEmailDialog(value: Boolean) {
        _state.update { it.copy(showEmailDialog = value) }
    }

    private fun showPasswordDialog(value: Boolean) {
        _state.update { it.copy(showPasswordDialog = value) }
    }

    private fun showDisplayNameDialog(value: Boolean) {
        _state.update { it.copy(showDisplayNameDialog = value) }
    }

    private fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            try {
                accountRepository.updateDisplayName(displayName)
                Log.d("Account", "Display name updated successfully.")
                _state.update { it.copy(displayName = displayName, showDisplayNameDialog = false) }
            } catch (e: Exception) {
                Log.e("Account", "Display name update failed. Error: ${e.message}")
            }
        }
    }

    private fun updateEmail(email: String) {
        _state.update { it.copy(email = email) }
    }

    private fun updatePassword(password: String) {
        viewModelScope.launch {
            try {
                accountRepository.updatePassword(password)
                Log.d("Account", "Password updated successfully.")
                _state.update { it.copy(password = password, showPasswordDialog = false) }
            } catch (e: Exception) {
                Log.e("Account", "Update password failed. Error: ${e.message}")
            }
        }
    }

    private fun selectImage() {
        _state.update { it.copy(selectImage = true) }
    }

    private fun clearNavigation() {
        _state.update { it.copy(selectImage = false) }
    }

    private fun loadAccountInformation(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isRefreshing = isRefreshing) }
                val user = accountRepository.loadAccountInformation()
                if (isRefreshing) {
                    delay(1000)
                }
                Log.d("Community", "Display Name: '${user.displayName}`")
                _state.update {
                    it.copy(
                        email = user.email,
                        password = user.password,
                        displayName = user.displayName,
                        imagePath = user.imagePath
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    "Community",
                    "Error loading account information on AccountViewModel. ${e.message}"
                )
            } finally {
                _state.update { it.copy(isRefreshing = false, isLoading = false) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                accountRepository.logout()
                Log.d("Community", "Logout successfully.")
                _state.update { it.copy(isLoggedOut = true) }
            } catch (e: Exception) {
                Log.e("Community", "Logout error. ${e.message}")
            }
        }
    }
}