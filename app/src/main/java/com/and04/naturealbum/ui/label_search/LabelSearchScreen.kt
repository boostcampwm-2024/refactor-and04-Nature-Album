package com.and04.naturealbum.ui.label_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.and04.naturealbum.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelSearchScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.label_search_title_topbar),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        SearchContent(innerPadding)
    }
}

@Composable
fun SearchContent(innerPadding: PaddingValues) {
    var query by rememberSaveable { mutableStateOf("") }
    var randomColor by rememberSaveable { mutableStateOf("") }
    //TODO DB에서 라벨 목록 가져오기
    val data = listOf("고양이", "강아지", "장수말벌")

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = {
                if(it.length > 100) return@TextField
                else query = it
            },
            placeholder = { Text(stringResource(R.string.label_search_label_search)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent
            )
        )

        Text(
            modifier = Modifier.padding(12.dp),
            text = stringResource(R.string.label_search_select_create),
            style = MaterialTheme.typography.bodyMedium
        )

        LazyColumn {
            val queryLabelList = data.filter { it.contains(query) }
            items(queryLabelList) { label ->
                //TODO 라벨 색상 지정
                AssistChipList(title = label, color = "")
            }
        }

        Row(
            modifier = Modifier.padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.label_search_create))
            Spacer(Modifier.size(4.dp))
            AssistChip(
                onClick = { }, //TODO 클릭 시 해당 라벨 선택 후 종료
                label = { Text(query) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(
                        android.graphics.Color.parseColor(
                            randomColor.ifBlank {
                                randomColor = getRandomColor()
                                randomColor
                            }
                        )
                    ),
                    labelColor = getLabelTextColor(randomColor)
                )
            )
        }
    }
}

@Composable
fun AssistChipList(
    title: String,
    color: String
) {
    AssistChip(
        modifier = Modifier
            .padding(start = 12.dp),
        onClick = { }, //TODO 클릭 시 해당 라벨 선택 후 종료
        label = { Text(title) },
    )

    Spacer(
        modifier = Modifier
            .height(0.5.dp)
            .fillMaxWidth()
            .background(Color.Gray)
    )
}

@Preview
@Composable
fun PreviewFunc() {
    LabelSearchScreen()
}