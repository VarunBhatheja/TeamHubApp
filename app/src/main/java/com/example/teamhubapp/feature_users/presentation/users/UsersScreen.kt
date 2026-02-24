package com.example.teamhubapp.feature_users.presentation.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teamhubapp.feature_users.presentation.components.common.EmptyView
import com.example.teamhubapp.feature_users.presentation.components.common.ErrorView
import com.example.teamhubapp.feature_users.presentation.components.common.LoadingView
import com.example.teamhubapp.feature_users.presentation.components.user.SegmentedSearchBar
import com.example.teamhubapp.feature_users.presentation.state.UsersUiState
import com.example.teamhubapp.feature_users.presentation.viewModel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEAF3FF),
                            Color(0xFFD6E8FF)
                        )
                    )
                )
                .padding(paddingValues)
                .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {

            val selectedRole by viewModel.selectedRole.collectAsState()
            val availableRoles by viewModel.availableRoles.collectAsState(initial = emptyList())
            val searchQuery by viewModel.searchQuery.collectAsState()
            Text(
                text = "Employees",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 24.dp)
            )


            Spacer(modifier = Modifier.height(8.dp))



            SegmentedSearchBar(
                searchText = searchQuery,
                onSearchChange = { viewModel.onSearchQueryChange(it) },
                selectedRole = selectedRole,
                availableRoles = availableRoles,
                onRoleSelected = { viewModel.onRoleSelected(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = Color(0xFFD0D7E2) // soft blue-gray
            )


            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F9FC)) // light grey-blue
            ) {
                when (val state = uiState) {
                    is UsersUiState.Loading -> LoadingView()
                    is UsersUiState.Empty -> EmptyView()
                    is UsersUiState.Error -> ErrorView(state.message)
                    is UsersUiState.Success -> UsersList(
                        users = state.users,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            end = 4.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                    )
                }
            }
        }
    }
}