package com.and04.naturealbum.ui.friend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.dto.FriendStatus
import com.and04.naturealbum.ui.mypage.NoNetworkSocialContent
import com.and04.naturealbum.utils.NetworkState.DISCONNECTED
import com.and04.naturealbum.utils.NetworkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendSearchScreen(
    onBack: () -> Unit,
    friendViewModel: FriendViewModel,
    networkViewModel: NetworkViewModel,
) {
    val userWithStatusList by friendViewModel.searchResults.collectAsStateWithLifecycle()
    val networkState = networkViewModel.networkState.collectAsStateWithLifecycle()
    var textFieldState by remember { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val currentUid = friendViewModel.uid!!

    LaunchedEffect(Unit) {
        textFieldState = ""
        friendViewModel.updateSearchQuery("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.friend_search_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = textFieldState,
                        onSearch = { expanded = false },
                        expanded = expanded,
                        onExpandedChange = { expandedChange -> expanded = expandedChange },
                        placeholder = {
                            Text(text = stringResource(R.string.my_page_search_hint))
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.my_page_search_bar_search_icon)
                            )
                        },
                        onQueryChange = { query ->
                            textFieldState = query
                            friendViewModel.updateSearchQuery(query)
                        },
                    )
                },
                expanded = false,
                onExpandedChange = {
                },
            ) {
            }

            if (networkState.value == DISCONNECTED) {
                NoNetworkSocialContent()
            } else {
                RequestedList(
                    userWithStatusList = userWithStatusList,
                    currentUid = currentUid,
                    sendFriendRequest = friendViewModel::sendFriendRequest
                )
            }
        }
    }
}

@Composable
fun RequestedList(
    userWithStatusList: Map<String, FirestoreUserWithStatus>,
    currentUid: String,
    sendFriendRequest: (String, String) -> Unit,
) {
    if (userWithStatusList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.freind_search_screen_no_result),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        LazyColumn {
            userWithStatusList.forEach { (uid, userWithStatus) ->
                item(key = uid) {
                    RequestedItem(
                        userWithStatus = userWithStatus,
                        currentUid = currentUid,
                        sendFriendRequest = sendFriendRequest
                    )
                }
            }
        }
    }
}

@Composable
fun RequestedItem(
    userWithStatus: FirestoreUserWithStatus,
    currentUid: String,
    sendFriendRequest: (String, String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            model = userWithStatus.user.photoUrl,
            contentDescription = stringResource(R.string.my_page_user_profile_image),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = userWithStatus.user.email,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            SuggestionChip(
                onClick = {
                    if (userWithStatus.status == FriendStatus.NORMAL) {
                        sendFriendRequest(currentUid, userWithStatus.user.uid)
                    }
                },
                enabled = userWithStatus.status == FriendStatus.NORMAL,
                label = {
                    val text = when (userWithStatus.status) {
                        FriendStatus.SENT -> stringResource(R.string.my_page_friend_requested)
                        FriendStatus.RECEIVED -> stringResource(R.string.my_page_friend_request_received)
                        FriendStatus.FRIEND -> stringResource(R.string.my_page_friend)
                        else -> stringResource(R.string.my_page_friend_request)
                    }
                    Text(text = text)
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = when (userWithStatus.status) {
                        FriendStatus.NORMAL -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondary
                    },
                    labelColor = when (userWithStatus.status) {
                        FriendStatus.NORMAL -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSecondary
                    }
                )
            )
        }
    }

    HorizontalDivider(thickness = 1.dp)
}
