package com.and04.naturealbum.ui.maps

import android.app.Dialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirestoreUser
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun FriendDialog(
    friends: State<List<FirebaseFriend>> = remember { mutableStateOf(emptyList()) },
    selectedFriends: State<List<FirebaseFriend>> = remember { mutableStateOf(emptyList()) },
    userSelectMax: Int = 4,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: (List<FirebaseFriend>) -> Unit = {}
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var checkedFriends by remember { mutableStateOf<List<FirebaseFriend>>(selectedFriends.value) }
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(maxHeight = screenHeight * 0.7f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.map_friend_dialog_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.map_friend_dialog_body, userSelectMax),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (friends.value.isNotEmpty()) {
                LazyColumn(
                    modifier = modifier
                        .weight(weight = 1f, fill = false)
                        .padding(horizontal = 16.dp),
                ) {
                    items(friends.value) { friend ->
                        FriendDialogItem(friend = friend,
                            isSelect = checkedFriends.contains(friend),
                            onSelect = {
                                if (checkedFriends.contains(friend)) {
                                    checkedFriends = checkedFriends.filter { it != friend }
                                } else if (checkedFriends.size < userSelectMax) {
                                    checkedFriends = checkedFriends + friend
                                }
                            })
                        HorizontalDivider()
                    }
                }
            } else {
                Column (
                    modifier = modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        imageVector = Icons.Default.GroupOff,
                        contentDescription = stringResource(R.string.map_friend_dialog_no_friend_icon),
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = stringResource(R.string.map_friend_dialog_no_friend),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { onDismiss() }
                ) {
                    Text(
                        text = stringResource(R.string.map_friend_dialog_cancel_btn),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                TextButton(
                    onClick = { onConfirm(checkedFriends) }
                ) {
                    Text(
                        text = stringResource(R.string.map_friend_dialog_confirm_btn),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }

}

@Composable
fun FriendDialogItem(
    friend: FirebaseFriend,
    isSelect: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            model = friend.user.photoUrl,
            contentDescription = friend.user.displayName
        )
        Text(
            modifier = modifier.weight(1f),
            text = friend.user.displayName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
        Checkbox(
            checked = isSelect,
            colors = CheckboxDefaults.colors().copy(
                uncheckedBoxColor = MaterialTheme.colorScheme.primary,
                uncheckedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            onCheckedChange = { onSelect() }
        )
    }
}

@Preview
@Composable
fun EmptyDialogPreView() {
    val friends = remember { mutableStateOf<List<FirebaseFriend>>(emptyList()) }
    val selectedFriends = remember { mutableStateOf<List<FirebaseFriend>>(emptyList()) }

    NatureAlbumTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FriendDialog(
                friends = friends,
                selectedFriends = selectedFriends,
            )
        }
    }
}

@Preview
@Composable
fun MinimumDialogPreView() {
    val friends = remember { mutableStateOf<List<FirebaseFriend>>(listOf(FirebaseFriend(
        FirestoreUser(displayName = "test")))) }
    val selectedFriends = remember { mutableStateOf<List<FirebaseFriend>>(emptyList()) }

    NatureAlbumTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FriendDialog(
                friends = friends,
                selectedFriends = selectedFriends,
            )
        }
    }
}

@Preview
@Composable
fun FullDialogPreView() {
    val friends = remember { mutableStateOf<List<FirebaseFriend>>(List(10){(FirebaseFriend(
        FirestoreUser(displayName = "test${it+1}")))}) }
    val selectedFriends = remember { mutableStateOf<List<FirebaseFriend>>(emptyList()) }

    NatureAlbumTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FriendDialog(
                friends = friends,
                selectedFriends = selectedFriends,
            )
        }
    }
}
