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
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.and04.naturealbum.background.workmanager.SynchronizationWorker
import com.and04.naturealbum.data.dto.FirebaseFriend
import com.and04.naturealbum.data.dto.FirebaseFriendRequest
import com.and04.naturealbum.data.dto.FirestoreUserWithStatus
import com.and04.naturealbum.data.dto.MyFriend
import com.and04.naturealbum.ui.component.PortraitTopAppBar
import com.and04.naturealbum.ui.component.ProgressIndicator
import com.and04.naturealbum.ui.component.RotatingButton
import com.and04.naturealbum.ui.friend.FriendViewModel
import com.and04.naturealbum.ui.model.UiState
import com.and04.naturealbum.ui.model.UserInfo
import com.and04.naturealbum.ui.theme.NatureAlbumTheme
import com.and04.naturealbum.utils.NetworkState
import com.and04.naturealbum.utils.NetworkState.CONNECTED_DATA
import com.and04.naturealbum.utils.NetworkState.CONNECTED_WIFI
import com.and04.naturealbum.utils.NetworkState.DISCONNECTED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    val allUsersInfo = friendViewModel.allUsersWithStatus.collectAsStateWithLifecycle()
    val recentSyncTime = myPageViewModel.recentSyncTime.collectAsStateWithLifecycle()
    val progressState = myPageViewModel.progressState.collectAsStateWithLifecycle()

    MyPageScreenContent(
        navigateToHome = navigateToHome,
        uiState = uiState,
        myFriendsState = myFriends,
        friendRequestsState = receivedFriendRequests,
        allUsersInfoState = allUsersInfo,
        signInWithGoogle = myPageViewModel::signInWithGoogle,
        fetchReceivedFriendRequests = friendViewModel::fetchReceivedFriendRequests,
        fetchFriends = friendViewModel::fetchFriends,
        fetchAllUsersInfo = friendViewModel::fetchAllUsersInfo,
        sendFriendRequest = friendViewModel::sendFriendRequest,
        acceptFriendRequest = friendViewModel::acceptFriendRequest,
        rejectFriendRequest = friendViewModel::rejectFriendRequest,
        recentSyncTime = recentSyncTime,
        progressState = progressState,
        setProgressState = myPageViewModel::setProgressState,
    )
}

@Composable
fun MyPageScreenContent(
    navigateToHome: () -> Unit,
    uiState: State<UiState<UserInfo>>,
    myFriendsState: State<List<FirebaseFriend>>,
    friendRequestsState: State<List<FirebaseFriendRequest>>,
    allUsersInfoState: State<List<FirestoreUserWithStatus>>,
    signInWithGoogle: (Context) -> Unit,
    fetchReceivedFriendRequests: (String) -> Unit,
    fetchFriends: (String) -> Unit,
    fetchAllUsersInfo: (String) -> Unit,
    sendFriendRequest: (String, String) -> Unit,
    acceptFriendRequest: (String, String) -> Unit,
    rejectFriendRequest: (String, String) -> Unit,
    recentSyncTime: State<String>,
    progressState: State<Boolean>,
    setProgressState: (Boolean) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
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
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        MyPageContent(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            uiState = uiState,
            myFriendsState = myFriendsState,
            friendRequestsState = friendRequestsState,
            allUsersInfoState = allUsersInfoState,
            recentSyncTime = recentSyncTime,
            signInWithGoogle = signInWithGoogle,
            fetchReceivedFriendRequests = fetchReceivedFriendRequests,
            fetchFriends = fetchFriends,
            fetchAllUsersInfo = fetchAllUsersInfo,
            sendFriendRequest = sendFriendRequest,
            acceptFriendRequest = acceptFriendRequest,
            rejectFriendRequest = rejectFriendRequest,
            snackBarHostState = snackBarHostState,
            progressState = progressState,
            setProgressState = setProgressState,
        )
    }
}

@Composable
private fun MyPageContent(
    modifier: Modifier,
    uiState: State<UiState<UserInfo>>,
    myFriendsState: State<List<FirebaseFriend>>,
    friendRequestsState: State<List<FirebaseFriendRequest>>,
    allUsersInfoState: State<List<FirestoreUserWithStatus>>,
    signInWithGoogle: (Context) -> Unit,
    fetchReceivedFriendRequests: (String) -> Unit,
    fetchFriends: (String) -> Unit,
    fetchAllUsersInfo: (String) -> Unit,
    sendFriendRequest: (String, String) -> Unit,
    acceptFriendRequest: (String, String) -> Unit,
    rejectFriendRequest: (String, String) -> Unit,
    recentSyncTime: State<String>,
    snackBarHostState: SnackbarHostState,
    progressState: State<Boolean>,
    setProgressState: (Boolean) -> Unit,
) {
    val context = LocalContext.current


    Box(modifier = modifier) {
        when (val success = uiState.value) {
            is UiState.Success -> {
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    val userEmail = success.data.userEmail
                    val userPhotoUri = success.data.userPhotoUri
                    val userDisplayName = success.data.userDisplayName
                    val userUid = success.data.userUid
                    UserProfileContent(
                        uriState = userPhotoUri,
                        emailState = userEmail,
                        displayNameState = userDisplayName,
                        snackBarHostState = snackBarHostState,
                        recentSyncTime = recentSyncTime
                    )

                    SocialContent(
                        modifier = Modifier.weight(1f),
                        userUidState = userUid,
                        myFriendsState = myFriendsState,
                        friendRequestsState = friendRequestsState,
                        allUsersInfoState = allUsersInfoState,
                        fetchReceivedFriendRequests = fetchReceivedFriendRequests,
                        fetchFriends = fetchFriends,
                        fetchAllUsersInfo = fetchAllUsersInfo,
                        sendFriendRequest = sendFriendRequest,
                        acceptFriendRequest = acceptFriendRequest,
                        rejectFriendRequest = rejectFriendRequest,
                    )
                }
            }

            else -> {
                // 비회원일 때
                Box {
                    ProgressIndicator(progressState.value)
                }
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    UserProfileContent(null, null, null)
                    LoginContent(
                        progressState = progressState,
                        setProgressState = setProgressState,
                    ) { signInWithGoogle(context) }
                }
            }
        }
    }
}

@Composable
private fun UserProfileContent(
    uriState: String?,
    emailState: String?,
    displayNameState: String?,
    snackBarHostState: SnackbarHostState? = null,
    recentSyncTime: State<String>? = null,
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        if (snackBarHostState != null) {
            SyncContent(
                snackBarHostState = snackBarHostState,
                recentSyncTime = recentSyncTime!!
            )
        }
    }
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
private fun LoginContent(
    progressState: State<Boolean>,
    setProgressState: (Boolean) -> Unit,
    loginHandle: () -> Unit,
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.my_page_login_txt),
            textAlign = TextAlign.Left
        )

        Button(
            onClick = {
                loginHandle()
                setProgressState(true)
            },
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
    allUsersInfoState: State<List<FirestoreUserWithStatus>>,
    fetchReceivedFriendRequests: (String) -> Unit,
    fetchFriends: (String) -> Unit,
    fetchAllUsersInfo: (String) -> Unit,
    sendFriendRequest: (String, String) -> Unit,
    acceptFriendRequest: (String, String) -> Unit,
    rejectFriendRequest: (String, String) -> Unit,
) {
    val currentUid = userUidState ?: return
    val myFriends = myFriendsState.value
    val friendRequests = friendRequestsState.value
    val allUsersInfo = allUsersInfoState.value

    var tabState by remember { mutableIntStateOf(SOCIAL_LIST_TAB_INDEX) }

    val titles = listOf(
        stringResource(R.string.my_page_social_list),
        stringResource(R.string.my_page_social_search),
        stringResource(R.string.my_page_social_alarm)
    )

    LaunchedEffect(Unit) {
        fetchFriends(currentUid)
    }

    Column(
        modifier = modifier
    ) {
        PrimaryTabRow(selectedTabIndex = tabState) {
            titles.forEachIndexed { index, title ->
                MyPageCustomTab(tabState, index, title) {
                    tabState = index
                    when (index) {
                        SOCIAL_LIST_TAB_INDEX -> fetchFriends(currentUid)
                        SOCIAL_SEARCH_TAB_INDEX -> fetchAllUsersInfo(currentUid)
                        SOCIAL_ALARM_TAB_INDEX -> fetchReceivedFriendRequests(currentUid)
                    }
                }
            }
        }

        when (tabState) {
            SOCIAL_LIST_TAB_INDEX -> MyPageSocialList(myFriends) // 친구 목록
            SOCIAL_SEARCH_TAB_INDEX -> MyPageSearch(
                allUsersInfo,
                currentUid,
                sendFriendRequest
            )

            SOCIAL_ALARM_TAB_INDEX -> MyPageAlarm(
                friendRequests,
                acceptFriendRequest,
                rejectFriendRequest,
                currentUid
            )
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

@Composable
private fun SyncContent(
    snackBarHostState: SnackbarHostState,
    recentSyncTime: State<String>,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val rotatingState = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(recentSyncTime.value) {
        rotatingState.value = false
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(stringResource(R.string.my_page_sync))
        IconButton(
            onClick = {
                when (NetworkState.getNetWorkCode()) {
                    CONNECTED_WIFI -> {
                        SynchronizationWorker.runImmediately(context)
                        rotatingState.value = true
                    }

                    CONNECTED_DATA -> {
                        startSnackBar(
                            context = context,
                            coroutineScope = coroutineScope,
                            snackBarHostState = snackBarHostState,
                            message = context.getString(R.string.my_page_snackbar_network_state_data_keep_going),
                            actionLabel = context.getString(R.string.my_page_snackbar_confirm_button),
                            rotatingState = rotatingState,
                        )
                    }

                    DISCONNECTED -> {
                        startSnackBar(
                            context = context,
                            coroutineScope = coroutineScope,
                            snackBarHostState = snackBarHostState,
                            message = context.getString(R.string.my_page_snackbar_network_state_disconnect),
                            actionLabel = null
                        )
                    }
                }
            }
        ) {
            RotatingButton(
                rotatingState = rotatingState.value,
                imageVector = Icons.Default.Sync,
                contentDescription = stringResource(R.string.my_page_sync_icon_content_description)
            )
        }
    }
    Text(
        style = MaterialTheme.typography.bodySmall,
        text = recentSyncTime.value
    )

}

private fun startSnackBar(
    context: Context,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    message: String,
    actionLabel: String?,
    rotatingState: MutableState<Boolean> = mutableStateOf(false),
) {
    coroutineScope.launch {
        val result = snackBarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long,
        )

        if (result == SnackbarResult.ActionPerformed) {
            SynchronizationWorker.runImmediately(context)
            rotatingState.value = true
        }
    }
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
    val recentSyncTime = remember { mutableStateOf("2024-01-01") }

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
