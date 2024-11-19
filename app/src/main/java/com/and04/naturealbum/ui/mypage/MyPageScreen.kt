package com.and04.naturealbum.ui.mypage

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
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
import com.and04.naturealbum.data.dto.MyFriend
import com.and04.naturealbum.ui.component.PortraitTopAppBar
import com.and04.naturealbum.ui.savephoto.UiState
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun MyPageScreen(
    navigateToHome: () -> Unit,
    myPageViewModel: MyPageViewModel = hiltViewModel(),
) {
    val uiState = myPageViewModel.uiState.collectAsStateWithLifecycle()
    val myFriends = myPageViewModel.myFriend.collectAsStateWithLifecycle()

    MyPageScreen(
        navigateToHome = navigateToHome,
        uiState = uiState,
        myFriends = myFriends,
        signInWithGoogle = myPageViewModel::signInWithGoogle
    )
}

@Composable
fun MyPageScreen(
    navigateToHome: () -> Unit,
    uiState: State<UiState>,
    myFriends: State<List<MyFriend>>,
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
            uiState = uiState,
            myFriends = myFriends,
            signInWithGoogle = signInWithGoogle,
        )
    }
}

@Composable
private fun MyPageContent(
    modifier: Modifier,
    uiState: State<UiState>,
    myFriends: State<List<MyFriend>>,
    signInWithGoogle: (Context) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        when (uiState.value) {
            is UiState.Success -> {
                val user = UserManager.getUser()
                val email = user?.email
                val photoUri = user?.photoUrl

                UserProfileContent(uri = photoUri, email = email)

                SocialContent(
                    modifier = Modifier.weight(1f),
                    myFriends = myFriends,
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
private fun UserProfileContent(uri: Uri? = null, email: String? = null) {
    UserProfileImage(
        uri = uri?.toString() ?: "",
        modifier = Modifier
            .fillMaxHeight(0.2f)
            .aspectRatio(1f)
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
    myFriends: State<List<MyFriend>>,
) {
    var tabState by remember { mutableIntStateOf(0) }
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
                MyPageCustomTab(tabState, index, title) { tabState = index }
            }
        }

        when (tabState) {
            0 -> MyPageSocialList(myFriends.value)
            1 -> MyPageSearch(myFriends.value)
            2 -> MyPageAlarm(myFriends.value, {}, {})
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
                    if (index == 2 && itemCount > 0) {
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

    NatureAlbumTheme {
        MyPageScreen(
            navigateToHome = {},
            uiState = uiState,
            myFriends = myFriends,
            signInWithGoogle = {}
        )
    }
}
