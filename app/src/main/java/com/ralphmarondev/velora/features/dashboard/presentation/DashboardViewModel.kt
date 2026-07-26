package com.ralphmarondev.velora.features.dashboard.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadInformation()
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            DashboardAction.NavigateToProfile -> navigateToProfile()
            DashboardAction.Refresh -> loadInformation(isRefreshing = true)
            DashboardAction.ClearNavigation -> clearNavigation()
        }
    }

    private fun navigateToProfile() {
        _state.update { it.copy(navigateToProfile = true) }
    }

    private fun clearNavigation() {
        _state.update { it.copy(navigateToProfile = false) }
    }

    private fun loadInformation(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(isLoading = true, isRefreshing = isRefreshing)
                }

                if (isRefreshing) {
                    delay(1000)
                }
            } catch (e: Exception) {
                Log.e("Dashboard", "Loading information error. ${e.message}")
            } finally {
                _state.update {
                    it.copy(isLoading = false, isRefreshing = false)
                }
            }
        }
    }
}