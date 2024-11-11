package com.and04.naturealbum.ui.album

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.component.MyTopAppBar
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun AlbumFolderScreen() {
    val editMode = remember { mutableStateOf(false) }
    BackHandler(enabled = editMode.value) {
        if (editMode.value) {
            editMode.value = false
        }
    }
    Scaffold(topBar = { MyTopAppBar() }) { innerPadding ->
        ItemContainer(
            items = tmpItems,
            editMode = editMode,
            innerPaddingValues = innerPadding,
        )
    }

}

@Composable
fun ItemContainer(
    items: List<TmpItem>,
    editMode: MutableState<Boolean>,
    innerPaddingValues: PaddingValues,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                state = rememberLazyStaggeredGridState(),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(24.dp),
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalItemSpacing = 16.dp
            ) {
                itemsIndexed(
                    items = items,
                    key = { _: Int, item: TmpItem -> item.hashCode() }
                ) { _, item ->
                    Item(item, editMode)
                }
            }
            // 편집 모드가 활성화되었을 때, 오른쪽 아래에 버튼 표시
            if (editMode.value) {
                Button(
                    onClick = { /* TODO: 저장 동작 추가 */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp) // 버튼 여백 설정
                ) {
                    Text("선택한 사진 저장")
                }
            }
        }
    }
}

@Composable
fun Item(item: TmpItem, editMode: MutableState<Boolean>) {
    var isClicked by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { editMode.value = !editMode.value },
                            onTap = { isClicked = !isClicked })
                    }
            ) {
                AsyncImage(
                    model = item.img,
                    contentDescription = "", // TODO:
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()

                )
                if (editMode.value && isClicked) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(color = Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "", // TODO:
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
            Text(
                text = item.descroption,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
@Preview
fun ContentPreview() {
    NatureAlbumTheme {
        AlbumFolderScreen(
            //TmpItem(img = R.drawable.cat_dummy, descroption = "dsafasd")
        )
    }
}

data class TmpItem(
    val img: Int,
    val descroption: String,
)

val tmpItems = listOf(
    TmpItem(
        img = R.drawable.cat_dummy,
        descroption = "사진 11111111111111111111111111111111111111111111",
    ),
    TmpItem(
        img = R.drawable.sample_image_01,
        descroption = "사진 22222",
    ),
    TmpItem(
        img = R.drawable.sample_image_02,
        descroption = "사진 3333",
    ),
    TmpItem(
        img = R.drawable.sample_image_03,
        descroption = "사진 4444",
    ),
    TmpItem(
        img = R.drawable.sample_image_04,
        descroption = "사진 5555",
    ),
    TmpItem(
        img = R.drawable.sample_image_05,
        descroption = "6",
    ),
    TmpItem(
        img = R.drawable.sample_image_06,
        descroption = "7",
    ),
    TmpItem(
        img = R.drawable.sample_image_07,
        descroption = "8",
    ),
    TmpItem(
        img = R.drawable.sample_image_07,
        descroption = "9",
    ),
    TmpItem(
        img = R.drawable.sample_image_07,
        descroption = "10",
    ),
    TmpItem(
        img = R.drawable.sample_image_07,
        descroption = "11",
    ),
    TmpItem(
        img = R.drawable.sample_image_07,
        descroption = "12",
    ),
    TmpItem(
        img = R.drawable.sample_image_07,
        descroption = "13",
    ),
    TmpItem(
        img = R.drawable.sample_image_07,
        descroption = "14",
    ),
    TmpItem(
        img = R.drawable.sample_image_07,
        descroption = "15",
    ),
)
