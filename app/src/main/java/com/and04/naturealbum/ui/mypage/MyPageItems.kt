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
import com.and04.naturealbum.ui.friend.FriendViewModel

@Composable
fun MyPageSocialList(myFriends: List<FirebaseFriend>) {
    LazyColumn {
        // TODO: Friend에 email도 저장되도록 수정해서 이메일 표시하기
        items(items = myFriends, key = { myFriend -> myFriend.addedAt }) { myFriend ->
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
            model = "", // ,myFriend.uri, // TODO: 친구에 user 기본 정보 필요 데이터 구조 수정하기
            contentDescription = stringResource(R.string.my_page_user_profile_image),
        )

        Text(text = myFriend.id) // TODO: email or 닉네임 둘 중 하나로 표시하거나 둘 다 표시하기
    }

    HorizontalDivider(thickness = 1.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageSearch(
    userWithStatusList: List<FirestoreUserWithStatus>,
    currentUid: String,
    friendViewModel: FriendViewModel,
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
                    onQueryChange = { textField ->
                        textFieldState = textField
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expandedChange ->
                expanded = expandedChange
            },
        ) {
            // 검색 결과 리스트
            RequestedList(
                userWithStatusList = userWithStatusList,
                currentUid = currentUid,
                friendViewModel= friendViewModel,
            )
        }

        // 요청된 친구 리스트
        RequestedList(
            userWithStatusList = userWithStatusList,
            currentUid = currentUid,
            friendViewModel= friendViewModel,
        )
    }
}

@Composable
fun RequestedList(
    userWithStatusList: List<FirestoreUserWithStatus>,
    currentUid: String,
    friendViewModel: FriendViewModel,
) {
    LazyColumn {
        items(items = userWithStatusList, key = { myFriend -> myFriend.email }) { userWithStatus ->
            RequestedItem(
                userWithStatus = userWithStatus,
                currentUid = currentUid,
                friendViewModel= friendViewModel,
            )
        }
    }
}

@Composable
fun RequestedItem(
    userWithStatus: FirestoreUserWithStatus,
    currentUid: String,
    friendViewModel: FriendViewModel,
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
                model = "", // userWithStatus.uri,// TODO: 데이터 구조 수정 필요
                contentDescription = stringResource(R.string.my_page_user_profile_image),
            )

            Text(text = userWithStatus.email)
        }


        SuggestionChip(
            onClick = {
                when (userWithStatus.friendStatus) {
                    "normal" -> {
                        // ViewModel의 sendFriendRequest 호출
                        friendViewModel.sendFriendRequest(
                            uid = currentUid,
                            targetUid = userWithStatus.uid
                        )
                    }

                    else -> {  /* 다른 상태는 여기에서 처리 x */
                    }
                }
            },
            label = {
                val text = when (userWithStatus.friendStatus) {
                    "sent" -> stringResource(R.string.my_page_friend_requested) // 친구 요청을 보낸 상태
                    "received" -> stringResource(R.string.my_page_friend_request_received) // 친구 요청을 받은 상태
                    "friend" -> stringResource(R.string.my_page_friend) // 친구 요청이 수락된 상태 = 친구
                    else -> stringResource(R.string.my_page_friend_request) // 기본 상태
                }

                Text(text = text)
            },
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                labelColor = Color.White
            )
        )
    }

    HorizontalDivider(thickness = 1.dp)
}

@Composable
fun MyPageAlarm(myAlarms: List<FirebaseFriendRequest>, onDenied: () -> Unit, onAccept: () -> Unit) {
    LazyColumn {
        // TODO: id 이외의 email도 데이터 구조에 추가하기
        items(items = myAlarms, key = { myFriend -> myFriend.id }) { myFriend ->
            MyPageAlarmItem(myFriend, onDenied, onAccept)
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
                model = "", // myFriend.uri, // TODO: 요청에 user 정보 넣기
                contentDescription = stringResource(R.string.my_page_user_profile_image),
            )

            Text(text = "${myFriend.id}${stringResource(R.string.my_page_alarm_txt)}")
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
