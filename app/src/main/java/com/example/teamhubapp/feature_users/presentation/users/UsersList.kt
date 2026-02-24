package com.example.teamhubapp.feature_users.presentation.users

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.teamhubapp.feature_users.domain.model.User
import com.example.teamhubapp.feature_users.presentation.components.user.UserCard

@Composable
fun UsersList(users: List<User>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(users) { index, user ->

            UserCard(user)

            // Add divider except after last item
            if (index < users.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    thickness = 0.8.dp,
                    color = Color(0xFFDCE2EA)
                )
            }
        }
    }
}
