package com.example.teamhubapp.feature_users.presentation.viewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamhubapp.feature_users.data.remote.api.EmployeeApi
import com.example.teamhubapp.feature_users.domain.repository.UserRepository
import com.example.teamhubapp.feature_users.presentation.state.UsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UsersUiState>(UsersUiState.Loading)

    val uiState: StateFlow<UsersUiState> = _uiState

    init {
        observeUsers()
    }

    private fun observeUsers() {
        viewModelScope.launch {
            repository.observeUsers().collect { users ->
                _uiState.value =
                    if (users.isEmpty()) {
                        UsersUiState.Empty
                    } else {
                        UsersUiState.Success(users)
                    }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                repository.refreshUsers()
            } catch (e: Exception) {
                _uiState.value =
                    UsersUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }


}

@HiltViewModel
class UsersViewModel1 @Inject constructor(
    private val employeeApi: EmployeeApi
) : ViewModel() {

    init {
        viewModelScope.launch {
            try {
                val response = employeeApi.getEmployees()
                Log.d("API_TEST", response.toString())
            } catch (e: Exception) {
                Log.e("API_TEST", e.message ?: "Error")
            }
        }
    }
}


