package com.and04.naturealbum.ui.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest

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

@Composable
fun MyPageAlarm(
    myAlarms: List<FirebaseFriendRequest>,
    acceptFriendRequest: (String, String) -> Unit,
    rejectFriendRequest: (String, String) -> Unit,
    currentUid: String,
) {
    if (myAlarms.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.my_page_no_friend_requests_message),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn {
            items(
                items = myAlarms,
                key = { friendRequest -> friendRequest.user.email }) { friendRequest ->
                MyPageAlarmItem(
                    friendRequest = friendRequest,
                    onAccept = { acceptFriendRequest(currentUid, friendRequest.user.uid) },
                    onDenied = { rejectFriendRequest(currentUid, friendRequest.user.uid) }
                )
            }
        }
    }
}

@Composable
fun MyPageAlarmItem(
    friendRequest: FirebaseFriendRequest,
    onDenied: () -> Unit,
    onAccept: () -> Unit
) {
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
                model = friendRequest.user.photoUrl,
                contentDescription = stringResource(R.string.my_page_user_profile_image),
            )

            Text(text = "${friendRequest.user.displayName}${stringResource(R.string.my_page_alarm_txt)}")
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
