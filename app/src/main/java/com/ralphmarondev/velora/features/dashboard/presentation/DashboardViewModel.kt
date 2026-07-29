package com.ralphmarondev.velora.features.dashboard.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralphmarondev.velora.features.account.domain.repository.AccountRepository
import com.ralphmarondev.velora.features.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val accountRepository: AccountRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadInformation()
        observeTrafficRecord()
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
                val user = accountRepository.loadAccountInformation()
                if (isRefreshing) {
                    delay(1000)
                }
                _state.update { it.copy(imagePath = user.imagePath) }
            } catch (e: Exception) {
                Log.e("Dashboard", "Loading information error. ${e.message}")
            } finally {
                _state.update {
                    it.copy(isLoading = false, isRefreshing = false)
                }
            }
        }
    }

    private fun observeTrafficRecord() {
        viewModelScope.launch {
            dashboardRepository
                .observeLatestTrafficRecord()
                .collect { trafficRecord ->
                    Log.d("DashboardVM", "Traffic: $trafficRecord")
                    trafficRecord?.let {
                        _state.update { state ->
                            state.copy(
                                trafficRecord = it
                            )
                        }
                    }
                }
        }
    }
}