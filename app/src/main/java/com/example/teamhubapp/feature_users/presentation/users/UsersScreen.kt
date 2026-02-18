package com.example.teamhubapp.feature_users.presentation.users

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teamhubapp.feature_users.presentation.viewModel.UsersViewModel1

@Composable
fun UsersScreen(
    viewModel: UsersViewModel1 = hiltViewModel()
) {
    // Nothing needed here yet
    // ViewModel init will trigger API call
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading...")
    }
}