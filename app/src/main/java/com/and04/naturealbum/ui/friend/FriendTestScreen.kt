package com.and04.naturealbum.ui.friend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

            Button(onClick = { viewModel.setupTestData() }) {
                Text("Set up Test Data")
            }

            Button(onClick = { viewModel.fetchAllUsers() }) {
                Text("Fetch All Users")
            }

            Button(onClick = { viewModel.fetchFriendRequests("jeong") }) {
                Text("Fetch Friend Requests")
            }

            Button(onClick = { viewModel.fetchFriends("jeong") }) {
                Text("Fetch Friends")
            }

            Button(onClick = {
                viewModel.sendFriendRequest("jeong", "yujin")
                viewModel.sendFriendRequest("jeong", "and04")

                // TODO: 동시에 firebase에 친구 요청한다면? => 최근 것으로 업데이트 됨. 이 부분은 firebase가 순차적으로 처리하니까 나중에 생각하기
                viewModel.sendFriendRequest("cat", "jeong")
                viewModel.sendFriendRequest("jeong", "cat")
            }) {
                Text("Send Friend Requests")
            }

            Button(onClick = { viewModel.acceptFriendRequest("yujin", "jeong") }) {
                Text("Yujin Accepts")
            }

            Button(onClick = { viewModel.rejectFriendRequest("and04", "jeong") }) {
                Text("And04 Rejects")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display operation status
            Text(
                text = operationStatus,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Friend Requests", textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(friendRequests) { request ->
                    Text(
                        text = "jeong이 => ${request.id} 에게 ${request.requestedAt} 에, Status: ${request.status} 했음",
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
                        text = "Friend : ${friend.id} added at: ${friend.addedAt}",
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
