package com.and04.naturealbum.ui.mypage

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.dto.MyFriend
import com.and04.naturealbum.ui.component.PortraitTopAppBar
import com.and04.naturealbum.ui.friend.FriendViewModel
import com.and04.naturealbum.ui.model.UiState
import com.and04.naturealbum.ui.model.UserInfo
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

private const val SOCIAL_LIST_TAB_INDEX = 0
private const val SOCIAL_SEARCH_TAB_INDEX = 1
private const val SOCIAL_ALARM_TAB_INDEX = 2

@Composable
fun MyPageScreen(
    navigateToHome: () -> Unit,
    myPageViewModel: MyPageViewModel = hiltViewModel(),
    friendViewModel: FriendViewModel = hiltViewModel(),
) {
    val uiState = myPageViewModel.uiState.collectAsStateWithLifecycle()
    val myFriends = friendViewModel.friends.collectAsStateWithLifecycle()
    val receivedFriendRequests =
        friendViewModel.receivedFriendRequests.collectAsStateWithLifecycle()

    val searchResults = friendViewModel.searchResults.collectAsStateWithLifecycle()

    MyPageScreenContent(
        navigateToHome = navigateToHome,
        uiState = uiState,
        myFriendsState = myFriends,
        friendRequestsState = receivedFriendRequests,
        searchResults = searchResults,
        signInWithGoogle = myPageViewModel::signInWithGoogle,
        sendFriendRequest = friendViewModel::sendFriendRequest,
        acceptFriendRequest = friendViewModel::acceptFriendRequest,
        rejectFriendRequest = friendViewModel::rejectFriendRequest,
        onSearchQueryChange = friendViewModel::updateSearchQuery,

        )
}

@Composable
fun MyPageScreenContent(
    navigateToHome: () -> Unit,
    uiState: State<UiState<UserInfo>>,
    myFriendsState: State<List<FirebaseFriend>>,
    friendRequestsState: State<List<FirebaseFriendRequest>>,
    signInWithGoogle: () -> Unit,
    searchResults: State<List<FirestoreUserWithStatus>>,
    onSearchQueryChange: (String) -> Unit,
    sendFriendRequest: (String, String) -> Unit,
    acceptFriendRequest: (String, String) -> Unit,
    rejectFriendRequest: (String, String) -> Unit,
) {

    Scaffold(topBar = {
        PortraitTopAppBar(
            navigationIcon = {
                IconButton(onClick = { navigateToHome() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.my_page_arrow_back_icon_content_description)
                    )
                }
            }
        )
    }) { innerPadding ->
        MyPageContent(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            uiState = uiState,
            myFriendsState = myFriendsState,
            friendRequestsState = friendRequestsState,
            signInWithGoogle = signInWithGoogle,
            sendFriendRequest = sendFriendRequest,
            acceptFriendRequest = acceptFriendRequest,
            rejectFriendRequest = rejectFriendRequest,
            searchResults = searchResults,
            onSearchQueryChange = onSearchQueryChange,
        )
    }
}

@Composable
private fun MyPageContent(
    modifier: Modifier,
    uiState: State<UiState<UserInfo>>,
    myFriendsState: State<List<FirebaseFriend>>,
    friendRequestsState: State<List<FirebaseFriendRequest>>,
    signInWithGoogle: () -> Unit,

    sendFriendRequest: (String, String) -> Unit,
    acceptFriendRequest: (String, String) -> Unit,
    rejectFriendRequest: (String, String) -> Unit,
    searchResults: State<List<FirestoreUserWithStatus>>,
    onSearchQueryChange: (String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        // TODO: STATE 전환 로직을 개선하여 로그아웃 및 상태 초기화 후 UI 갱신을 명확히 구현할 필요가 있음
        when (val success = uiState.value) {
            is UiState.Success -> {
                val userEmail = success.data.userEmail
                val userPhotoUri = success.data.userPhotoUri
                val userDisplayName = success.data.userDisplayName
                val userUid = success.data.userUid

                UserProfileContent(
                    uriState = userPhotoUri,
                    emailState = userEmail,
                    displayNameState = userDisplayName,
                )
                SocialContent(
                    modifier = Modifier.weight(1f),
                    userUidState = userUid,
                    myFriendsState = myFriendsState,
                    friendRequestsState = friendRequestsState,

                    sendFriendRequest = sendFriendRequest,
                    acceptFriendRequest = acceptFriendRequest,
                    rejectFriendRequest = rejectFriendRequest,
                    searchResults = searchResults,
                    onSearchQueryChange = onSearchQueryChange,
                )
            }

            else -> {
                // 비회원일 때
                UserProfileContent(null, null, null)
                LoginContent { signInWithGoogle() }
            }
        }
    }
}

@Composable
private fun UserProfileContent(
    uriState: String?,
    emailState: String?,
    displayNameState: String?,
) {
    val uri = uriState ?: ""
    val email = emailState ?: stringResource(R.string.my_page_default_user_email)
    val displayName = displayNameState ?: ""

    UserProfileImage(
        uri = uri,
        modifier = Modifier
            .fillMaxHeight(0.2f)
            .aspectRatio(1f)
    )
    Text(
        text = displayName,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Text(
        text = email,
        modifier = Modifier.fillMaxWidth(),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun UserProfileImage(uri: String?, modifier: Modifier) {
    uri?.let {
        AsyncImage(
            model = uri,
            contentDescription = stringResource(R.string.my_page_user_profile_image),
            modifier = modifier.clip(CircleShape)
        )
    } ?: Image(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = stringResource(R.string.my_page_user_profile_image),
        modifier = modifier
    )

}

@Composable
private fun LoginContent(loginHandle: () -> Unit) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.my_page_login_txt),
            textAlign = TextAlign.Left
        )

        Button(
            onClick = { loginHandle() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30)
        ) {
            Text(text = stringResource(R.string.my_page_google_login_btn))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SocialContent(
    modifier: Modifier,
    userUidState: String?,
    myFriendsState: State<List<FirebaseFriend>>,
    friendRequestsState: State<List<FirebaseFriendRequest>>,


    sendFriendRequest: (String, String) -> Unit,
    acceptFriendRequest: (String, String) -> Unit,
    rejectFriendRequest: (String, String) -> Unit,
    searchResults: State<List<FirestoreUserWithStatus>>,
    onSearchQueryChange: (String) -> Unit,
) {
    val currentUid = userUidState ?: return
    val myFriends = myFriendsState.value
    val friendRequests = friendRequestsState.value
    val friendRequestsCount = friendRequests.size
    val searchResultsList = searchResults.value

    var tabState by remember { mutableIntStateOf(SOCIAL_LIST_TAB_INDEX) }

    val titles = listOf(
        stringResource(R.string.my_page_social_list),
        stringResource(R.string.my_page_social_search),
        stringResource(R.string.my_page_social_alarm)
    )


    Column(
        modifier = modifier
    ) {
        PrimaryTabRow(selectedTabIndex = tabState) {
            titles.forEachIndexed { index, title ->
                MyPageCustomTab(tabState, index, title, friendRequestsCount) {
                    tabState = index
                }
            }
        }

        when (tabState) {
            SOCIAL_LIST_TAB_INDEX -> MyPageSocialList(myFriends) // 친구 목록
            SOCIAL_SEARCH_TAB_INDEX -> MyPageSearch(
                userWithStatusList = searchResultsList,
                currentUid = currentUid,
                sendFriendRequest = sendFriendRequest,
                onSearchQueryChange = onSearchQueryChange
            )

            SOCIAL_ALARM_TAB_INDEX -> MyPageAlarm(
                myAlarms = friendRequests,
                acceptFriendRequest = acceptFriendRequest,
                rejectFriendRequest = rejectFriendRequest,
                currentUid = currentUid
            )
        }
    }
}

@Composable
private fun MyPageCustomTab(
    tabState: Int,
    index: Int,
    title: String,
    friendRequestsCount: Int,
    onClick: () -> Unit
) {
    Tab(
        selected = tabState == index,
        onClick = onClick,
        text = {
            Row {
                Text(
                    text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                )

                Box {
                    if (index == SOCIAL_ALARM_TAB_INDEX && friendRequestsCount > 0) {
                        Badge(
                            modifier = Modifier.padding(start = 8.dp),
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ) {
                            Text("$friendRequestsCount")
                        }
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun MyPageScreenPreview() {
    val uiState = remember { mutableStateOf(UiState.Idle) }
    val myFriends = remember {
        mutableStateOf(
            listOf(
                MyFriend("", "grand2181@gmail.com", true),
                MyFriend("", "도윤@gmail.com", true),
                MyFriend("", "정호@gmail.com", true)
            )
        )
    }

    // 테스트용 사용자 정보
    val userEmail = remember { mutableStateOf("test@example.com") }
    val userPhotoUrl = remember { mutableStateOf("https://via.placeholder.com/150") }
    val userDisplayName = remember { mutableStateOf("Test User") }

    NatureAlbumTheme {
//        MyPageScreen(
//            navigateToHome = {},
//            uiState = uiState,
//            myFriends = myFriends,
//            userEmail = userEmail.value,
//            userPhotoUrl = userPhotoUrl.value,
//            userDisplayName = userDisplayName.value,
//            signInWithGoogle = {}
//        )
    }
}
