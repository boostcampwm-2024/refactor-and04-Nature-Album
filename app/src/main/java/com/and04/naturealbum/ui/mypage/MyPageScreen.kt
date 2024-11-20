package com.and04.naturealbum.ui.mypage

import android.content.Context
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
import com.and04.naturealbum.ui.savephoto.UiState
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

const val SOCIAL_LIST_TAB_INDEX = 0
const val SOCIAL_SEARCH_TAB_INDEX = 1
const val SOCIAL_ALARM_TAB_INDEX = 2

@Composable
fun MyPageScreen(
    navigateToHome: () -> Unit,
    myPageViewModel: MyPageViewModel = hiltViewModel(),
    friendViewModel: FriendViewModel = hiltViewModel(),
) {
    val uiState = myPageViewModel.uiState.collectAsStateWithLifecycle()
    val myFriends = friendViewModel.friends.collectAsStateWithLifecycle()
    val friendRequests = friendViewModel.friendRequests.collectAsStateWithLifecycle()
    val allUsersInfo = friendViewModel.allUsersWithStatus.collectAsStateWithLifecycle()

// TODO: 현재는 userEmail, userPhotoUrl, userDisplayName을 개별적으로 StateFlow로 관리하지만,
//       추후 UiState를 개선하여 사용자 정보를 포함하도록 구조를 변경할 필요가 있다.
//       - UiState.Success에 사용자 정보(email, photoUrl, displayName)를 포함시켜 단일 상태로 관리.
//       - UI는 UiState만 구독하도록 변경하여 코드 복잡도를 줄이고 상태 관리를 단순화.
//       - 현재 로직은 친구 추가 기능 테스트를 위한 임시 구현이며, 이후 사용자 상태 관리 구조 재설계 시 수정해야 함.

    val userEmail = myPageViewModel.userEmail.collectAsStateWithLifecycle()
    val userPhotoUrl = myPageViewModel.userPhotoUrl.collectAsStateWithLifecycle()
    val userDisplayName = myPageViewModel.userDisplayName.collectAsStateWithLifecycle()

    MyPageScreen(
        friendViewModel = friendViewModel,
        navigateToHome = navigateToHome,
        uiState = uiState,
        myFriends = myFriends.value,
        friendRequests = friendRequests.value,
        allUsersInfo = allUsersInfo.value,
        userEmail = userEmail.value,
        userPhotoUrl = userPhotoUrl.value,
        userDisplayName = userDisplayName.value,
        signInWithGoogle = myPageViewModel::signInWithGoogle
    )
}

@Composable
fun MyPageScreen(
    friendViewModel: FriendViewModel,
    navigateToHome: () -> Unit,
    uiState: State<UiState>,
    myFriends: List<FirebaseFriend>,
    friendRequests: List<FirebaseFriendRequest>,
    allUsersInfo: List<FirestoreUserWithStatus>,
    userPhotoUrl: String?,
    userEmail: String?,
    userDisplayName: String?,
    signInWithGoogle: (Context) -> Unit,
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
            friendViewModel = friendViewModel,
            uiState = uiState,
            myFriends = myFriends,
            friendRequests = friendRequests,
            allUsersInfo = allUsersInfo,
            userPhotoUrl = userPhotoUrl,
            userEmail = userEmail,
            userDisplayName = userDisplayName,
            signInWithGoogle = signInWithGoogle,
        )
    }
}

@Composable
private fun MyPageContent(
    modifier: Modifier,
    friendViewModel: FriendViewModel,
    uiState: State<UiState>,
    myFriends: List<FirebaseFriend>,
    friendRequests: List<FirebaseFriendRequest>,
    allUsersInfo: List<FirestoreUserWithStatus>,
    userPhotoUrl: String?,
    userEmail: String?,
    userDisplayName: String?,
    signInWithGoogle: (Context) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        // TODO: STATE 전환 로직을 개선하여 로그아웃 및 상태 초기화 후 UI 갱신을 명확히 구현할 필요가 있음
        when (uiState.value) {
            is UiState.Success -> {
                UserProfileContent(
                    uri = userPhotoUrl,
                    email = userEmail,
                    displayName = userDisplayName,
                )

                SocialContent(
                    modifier = Modifier.weight(1f),
                    friendViewModel = friendViewModel,
                    myFriends = myFriends,
                    friendRequests = friendRequests,
                    allUsersInfo = allUsersInfo,
                    currentUid = UserManager.getUser()?.uid ?: "",
                )
            }

            else -> {
                UserProfileContent()
                LoginContent { signInWithGoogle(context) }
            }
        }
    }
}

@Composable
private fun UserProfileContent(
    uri: String? = null,
    email: String? = null,
    displayName: String? = null
) {
    UserProfileImage(
        uri = uri ?: "",
        modifier = Modifier
            .fillMaxHeight(0.2f)
            .aspectRatio(1f)
    )
    Text(
        text = displayName ?: "",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Text(
        text = email ?: stringResource(R.string.my_page_default_user_email),
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
    friendViewModel: FriendViewModel,
    myFriends: List<FirebaseFriend>,
    friendRequests: List<FirebaseFriendRequest>,
    allUsersInfo: List<FirestoreUserWithStatus>,
    currentUid: String,
) {
    // TODO: 테스트용 친구 요청
    friendViewModel.sendFriendRequest(currentUid, "yujin")

    // TODO: 테스트용 친구 요청하고 수락해두기
    friendViewModel.sendFriendRequest("and04", currentUid)
    friendViewModel.sendFriendRequest("cat", currentUid)
    friendViewModel.acceptFriendRequest("and04", currentUid)
    friendViewModel.acceptFriendRequest("cat", currentUid)

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
                MyPageCustomTab(tabState, index, title) {
                    tabState = index
                    when (index) {
                        SOCIAL_LIST_TAB_INDEX -> friendViewModel.fetchFriends(currentUid) // 친구 목록 가져오기
                        SOCIAL_SEARCH_TAB_INDEX -> friendViewModel.fetchAllUsersInfo(currentUid) // 전체 사용자 정보 가져오기
                        SOCIAL_ALARM_TAB_INDEX -> friendViewModel.fetchFriendRequests(currentUid) // 친구 요청 목록 가져오기
                    }
                }
            }
        }

        when (tabState) {
            SOCIAL_LIST_TAB_INDEX -> MyPageSocialList(myFriends) // 친구 목록
            SOCIAL_SEARCH_TAB_INDEX -> MyPageSearch(allUsersInfo, currentUid, friendViewModel) // 전체 사용자 정보
            SOCIAL_ALARM_TAB_INDEX -> MyPageAlarm(friendRequests, {}, {}) // 친구 요청
        }
    }
}

@Composable
private fun MyPageCustomTab(tabState: Int, index: Int, title: String, onClick: () -> Unit) {
    val itemCount by remember { mutableIntStateOf(5) } // TODO : FIREBASE 알람 개수

    Tab(
        selected = tabState == index,
        onClick = onClick,
        text = {
            Row {
                Text(
                    text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                )

                Box {
                    if (index == SOCIAL_ALARM_TAB_INDEX && itemCount > 0) {
                        Badge(
                            modifier = Modifier.padding(start = 8.dp),
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ) {
                            Text("$itemCount")
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
