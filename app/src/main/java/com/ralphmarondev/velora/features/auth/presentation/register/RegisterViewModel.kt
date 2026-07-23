package com.ralphmarondev.velora.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralphmarondev.velora.core.domain.model.Result
import com.ralphmarondev.velora.features.auth.domain.model.User
import com.ralphmarondev.velora.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.DisplayNameChange -> {
                _state.update { it.copy(displayName = action.displayName) }
            }

            is RegisterAction.EmailChange -> {
                _state.update { it.copy(email = action.email) }
            }

            is RegisterAction.PasswordChange -> {
                _state.update { it.copy(password = action.password) }
            }

            is RegisterAction.ConfirmPasswordChange -> {
                _state.update { it.copy(confirmPassword = action.confirmPassword) }
            }

            RegisterAction.Login -> {
                _state.update { it.copy(navigateToLogin = true) }
            }

            RegisterAction.Register -> {
                register(
                    displayName = _state.value.displayName.trim(),
                    email = _state.value.email.trim(),
                    password = _state.value.password.trim(),
                    confirmPassword = _state.value.confirmPassword.trim()
                )
            }
        }
    }

    private fun register(
        displayName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        isRegistering = true,
                        isRegistered = false,
                        errorMessage = null,
                        showErrorMessage = false
                    )
                }

                if (displayName.isBlank()) {
                    _state.update {
                        it.copy(
                            errorMessage = "Display name cannot be blank.",
                            showErrorMessage = true
                        )
                    }
                    return@launch
                }
                if (email.isBlank()) {
                    _state.update {
                        it.copy(
                            errorMessage = "Email cannot be blank.",
                            showErrorMessage = true
                        )
                    }
                    return@launch
                }
                if (password.isBlank()) {
                    _state.update {
                        it.copy(
                            errorMessage = "Password cannot be blank.",
                            showErrorMessage = true
                        )
                    }
                    return@launch
                }
                if (confirmPassword != password) {
                    _state.update {
                        it.copy(
                            errorMessage = "Passwords did not match.",
                            showErrorMessage = true
                        )
                    }
                    return@launch
                }

                // return 1 - 4 (5 is exclusive)
                val randomImagePath = Random.nextInt(1, 5)
                val user = User(
                    email = email,
                    password = password,
                    displayName = displayName,
                    imagePath = randomImagePath
                )
                when (val result: Result<User> = repository.register(user)) {
                    is Result.Success -> {
                        _state.update { it.copy(isRegistered = true) }
                    }

                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                errorMessage = result.message ?: "Registration failed.",
                                showErrorMessage = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        errorMessage = e.localizedMessage ?: "Registration failed.",
                        showErrorMessage = true
                    )
                }
            } finally {
                _state.update { it.copy(isRegistering = false) }
            }
        }
    }
}