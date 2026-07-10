package com.kaushalprajapati.ecoscan.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaushalprajapati.ecoscan.data.pref.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = userPreferences.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userName: StateFlow<String?> = userPreferences.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userEmail: StateFlow<String?> = userPreferences.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val profilePictureUri: StateFlow<String?> = userPreferences.profilePictureUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notificationsEnabled: StateFlow<Boolean> = userPreferences.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val darkModeEnabled: StateFlow<Boolean> = userPreferences.darkModeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun updateProfile(name: String, email: String, photoUri: String?) {
        viewModelScope.launch {
            userPreferences.updateProfile(name, email, photoUri)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.toggleNotifications(enabled)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.toggleDarkMode(enabled)
        }
    }

    fun register(name: String, email: String) {
        viewModelScope.launch {
            userPreferences.saveUser(name, email)
        }
    }

    fun login(email: String) {
        viewModelScope.launch {
            // Simple login simulation
            userPreferences.saveUser("Demo User", email)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearUser()
        }
    }

    companion object {
        fun factory(userPreferences: UserPreferences) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(userPreferences) as T
            }
        }
    }
}
