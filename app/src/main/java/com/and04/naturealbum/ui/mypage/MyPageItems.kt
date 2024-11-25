package com.and04.naturealbum.ui.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.dto.FriendStatus

@Composable
fun MyPageSocialList(myFriends: List<FirebaseFriend>) {
    LazyColumn {
        items(items = myFriends, key = { myFriend -> myFriend.user.email }) { myFriend ->
            MyPageSocialItem(myFriend)
        }
    }
}

@Composable
fun MyPageSocialItem(myFriend: FirebaseFriend) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            model = myFriend.user.photoUrl,
            contentDescription = stringResource(R.string.my_page_user_profile_image),
        )

        Text(text = myFriend.user.displayName)
    }

    HorizontalDivider(thickness = 1.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageSearch(
    onSearchQueryChange: (String) -> Unit,
    userWithStatusList: List<FirestoreUserWithStatus>,
    currentUid: String,
    sendFriendRequest: (String, String) -> Unit,
) {
    var textFieldState by remember { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize()
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
                        onSearchQueryChange(query) // 검색 쿼리 변경 시 호출
                    },
                )
            },
            expanded = false,
            onExpandedChange = {
            },
        ) {
            // 검색 결과 리스트
            RequestedList(
                userWithStatusList = userWithStatusList,
                currentUid = currentUid,
                sendFriendRequest = sendFriendRequest
            )
        }

        // 요청된 친구 리스트
        RequestedList(
            userWithStatusList = userWithStatusList,
            currentUid = currentUid,
            sendFriendRequest = sendFriendRequest
        )
    }
}

@Composable
fun RequestedList(
    userWithStatusList: List<FirestoreUserWithStatus>,
    currentUid: String,
    sendFriendRequest: (String, String) -> Unit,
) {
    LazyColumn {
        items(
            items = userWithStatusList,
            key = { myFriend -> myFriend.user.email }) { userWithStatus ->
            RequestedItem(
                userWithStatus = userWithStatus,
                currentUid = currentUid,
                sendFriendRequest = sendFriendRequest
            )
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = userWithStatus.user.photoUrl,
                contentDescription = stringResource(R.string.my_page_user_profile_image),
            )

            Text(text = userWithStatus.user.email)
        }


        SuggestionChip(
            onClick = {
                if (userWithStatus.status == FriendStatus.NORMAL) {
                    sendFriendRequest(currentUid, userWithStatus.user.uid)
                }
            },
            label = {
                val text = when (userWithStatus.status) {
                    // 현재 uid 기준 상대방에게 [SENT: 요청 보낸 상태, RECEIVED: 요청 받은 상태, FRIEND: 친구 상태]
                    FriendStatus.SENT -> stringResource(R.string.my_page_friend_requested)
                    FriendStatus.RECEIVED -> stringResource(R.string.my_page_friend_request_received)
                    FriendStatus.FRIEND -> stringResource(R.string.my_page_friend)
                    else -> stringResource(R.string.my_page_friend_request)
                }
                Text(text = text)
            },
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = when (userWithStatus.status) {
                    FriendStatus.SENT -> Color.LightGray
                    FriendStatus.RECEIVED -> Color.Cyan
                    FriendStatus.FRIEND -> Color.Green
                    else -> MaterialTheme.colorScheme.primary
                },
                labelColor = when (userWithStatus.status) {
                    FriendStatus.SENT -> Color.Black
                    FriendStatus.RECEIVED -> Color.White
                    FriendStatus.FRIEND -> Color.White
                    else -> Color.White
                }
            )
        )
    }

    HorizontalDivider(thickness = 1.dp)
}

@Composable
fun MyPageAlarm(
    myAlarms: List<FirebaseFriendRequest>,
    acceptFriendRequest: (String, String) -> Unit,
    rejectFriendRequest: (String, String) -> Unit,
    currentUid: String,
) {
    LazyColumn {
        items(items = myAlarms, key = { myFriend -> myFriend.user.email }) { myFriend ->
            MyPageAlarmItem(
                myFriend = myFriend,
                onAccept = { acceptFriendRequest(currentUid, myFriend.user.uid) },
                onDenied = { rejectFriendRequest(currentUid, myFriend.user.uid) }
            )
        }
    }
}

@Composable
fun MyPageAlarmItem(myFriend: FirebaseFriendRequest, onDenied: () -> Unit, onAccept: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = myFriend.user.photoUrl,
                contentDescription = stringResource(R.string.my_page_user_profile_image),
            )

            Text(text = "${myFriend.user.displayName}${stringResource(R.string.my_page_alarm_txt)}")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(onClick = onAccept) {
                Text(text = stringResource(R.string.my_page_request_accept))
            }

            Spacer(modifier = Modifier.width(32.dp))

            Button(onClick = onDenied) {
                Text(text = stringResource(R.string.my_page_request_deny))
            }
        }
    }

    HorizontalDivider(thickness = 1.dp)
}
