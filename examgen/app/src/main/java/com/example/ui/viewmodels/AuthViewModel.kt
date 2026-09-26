package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datastore.UserPreferencesRepository
import com.example.data.models.RegisterRequest
import com.example.data.models.UserDto
import com.example.data.repository.ExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(
    private val repository: ExamRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = preferencesRepository.isLoggedInFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val currentUser: StateFlow<UserDto> = preferencesRepository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserDto(username = "admin", role = "Admin"))

    val serverUrl: StateFlow<String> = preferencesRepository.serverUrlFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "http://10.0.2.2:5000")

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Username and password cannot be blank")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.login(username.trim(), password)
            result.fold(
                onSuccess = { res ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = res.message ?: "Logged in successfully"
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = err.localizedMessage ?: "Login failed"
                    )
                }
            )
        }
    }

    fun register(username: String, email: String, password: String, fullName: String, role: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "All fields are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val request = RegisterRequest(
                username = username.trim(),
                email = email.trim(),
                password = password,
                fullName = fullName.trim().ifEmpty { username.trim() },
                role = role
            )
            val result = repository.register(request)
            result.fold(
                onSuccess = { res ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = res.message ?: "Registration successful"
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = err.localizedMessage ?: "Registration failed"
                    )
                }
            )
        }
    }

    fun updateServerUrl(newUrl: String) {
        viewModelScope.launch {
            preferencesRepository.saveServerUrl(newUrl.trim())
            _uiState.value = _uiState.value.copy(successMessage = "Backend server URL updated")
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesRepository.logout()
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
