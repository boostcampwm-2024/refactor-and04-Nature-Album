package com.and04.naturealbum.ui.friend

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

// TODO: 친구 기능 테스트용 Screen. UI 통합 후 제거 예정
@Composable
fun FriendTestScreen(
    viewModel: FriendViewModel = hiltViewModel()
) {

    val friendRequests by viewModel.friendRequests.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val operationStatus by viewModel.operationStatus.collectAsState()
    val allUsersInfo by viewModel.allUsersWithStatus.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 버튼 그룹
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        viewModel.setupTestData()
                        Toast.makeText(context, "초기 데이터가 설정되었습니다.", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("초기 데이터 설정")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        viewModel.fetchAllUsersInfo("jeong")
                    }) {
                        Text("Get All Users Info")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { viewModel.fetchFriends("jeong") }) {
                        Text("친구 목록 보기")
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { viewModel.fetchFriendRequests("jeong") }) {
                        Text("친구 요청 목록 보기")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        viewModel.sendFriendRequest("jeong", "and04")
                        viewModel.sendFriendRequest("jeong", "yujin")
                        viewModel.sendFriendRequest("cat", "jeong")
                    }) {
                        Text("요청 보내기")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { viewModel.acceptFriendRequest("jeong", "and04") }) {
                        Text("요청 수락")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { viewModel.rejectFriendRequest("jeong", "yujin") }) {
                        Text("요청 거절")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = operationStatus,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("All Users Info", textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allUsersInfo) { user ->
                    Text(
                        text = "Name: ${user.user.displayName}, Email: ${user.user.email}, Status: ${user.status}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Friend Requests", textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(friendRequests) { request ->
                    Text(
                        text = "Name: ${request.user.displayName}, Status: ${request.status}, Requested At: ${request.requestedAt}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Friends", textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(friends) { friend ->
                    Text(
                        text = "Name: ${friend.user.displayName}, Added At: ${friend.addedAt}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FriendTestScreenPreview() {
    FriendTestScreen()
}
