package com.and04.naturealbum.ui.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.and04.naturealbum.data.dto.MyFriend

@Composable
fun MyPageSocialList(myFriends: List<MyFriend>) {
    LazyColumn {
        items(items = myFriends, key = { myFriend -> myFriend.email }) { myFriend ->
            MyPageSocialItem(myFriend)
        }
    }
}

@Composable
fun MyPageSocialItem(myFriend: MyFriend) {
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
            model = myFriend.uri,
            contentDescription = stringResource(R.string.my_page_user_profile_image),
        )

        Text(text = myFriend.email)
    }

    HorizontalDivider(thickness = 1.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageSearch(myFriends: List<MyFriend>) {
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
            RequestedList(myFriends)
        }

        // 요청된 친구 리스트
        RequestedList(myFriends)
    }
}

@Composable
fun RequestedList(myFriends: List<MyFriend>) {
    LazyColumn {
        items(items = myFriends, key = { myFriend -> myFriend.email }) { myFriend ->
            RequestedItem(myFriend)
        }
    }
}

@Composable
fun RequestedItem(myFriend: MyFriend) {
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
                model = myFriend.uri,
                contentDescription = stringResource(R.string.my_page_user_profile_image),
            )

            Text(text = myFriend.email)
        }


        SuggestionChip(
            onClick = { /* TODO */ },
            label = {
                val text = if (myFriend.isRequest)
                    stringResource(R.string.my_page_friend_requested)
                else
                    stringResource(R.string.my_page_friend_request)

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
fun MyPageAlarm(myAlarms: List<MyFriend>, onDenied: () -> Unit, onAccept: () -> Unit) {
    LazyColumn {
        items(items = myAlarms, key = { myFriend -> myFriend.email }) { myFriend ->
            MyPageAlarmItem(myFriend, onDenied, onAccept)
        }
    }
}

@Composable
fun MyPageAlarmItem(myFriend: MyFriend, onDenied: () -> Unit, onAccept: () -> Unit) {
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
                model = myFriend.uri,
                contentDescription = stringResource(R.string.my_page_user_profile_image),
            )

            Text(text = "${myFriend.email}${stringResource(R.string.my_page_alarm_txt)}")
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
