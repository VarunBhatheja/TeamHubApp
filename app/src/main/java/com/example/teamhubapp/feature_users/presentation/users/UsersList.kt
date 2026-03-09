package com.example.teamhubapp.feature_users.presentation.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.teamhubapp.feature_users.domain.model.User
import com.example.teamhubapp.feature_users.presentation.components.user.UserCard

@Composable
fun UsersList(
    users       : List<User>,
    onUserClick : (String) -> Unit,
    modifier    : Modifier = Modifier,
    listState   : LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state   = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start  = 16.dp,
            end    = 16.dp,
            top    = 4.dp,
            bottom = 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(
            items = users,
            key   = { _, user -> user.id }  // stable keys = smooth animations
        ) { _, user ->
            UserCard(
                user    = user,
                onClick = { onUserClick(user.id) }
            )
        }
    }
}