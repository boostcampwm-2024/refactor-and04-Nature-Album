package com.and04.naturealbum.ui.album

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.and04.naturealbum.R
import com.and04.naturealbum.ui.component.AlbumLabel
import com.and04.naturealbum.ui.component.MyTopAppBar
import com.and04.naturealbum.ui.theme.NatureAlbumTheme

@Composable
fun AlbumFolderScreen() {
    Scaffold(topBar = { MyTopAppBar() }) { innerPadding ->
        ItemContainer(
            items = tmpItems,
            innerPaddingValues = innerPadding,
        )
    }
}

@Composable
fun ItemContainer(
    items: List<TmpItem>,
    innerPaddingValues: PaddingValues,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlbumLabel(
                modifier = Modifier
                    .background(color = Color(0xFFC5E1A5), shape = CircleShape)
                    .fillMaxWidth(0.9f),
                text = "라벨",
                backgroundColor = Color(0xFFC5E1A5)
            )
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
                        Item(item)
                    }
                }
            }
        }
    }
}

@Composable
fun Item(item: TmpItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = item.img,
                contentDescription = "", // TODO:
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .fillMaxWidth()
            )
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
        descroption = "사진 6666",
    ),
    TmpItem(
        img = R.drawable.sample_image_06,
        descroption = "사진 6666",
    ),
    TmpItem(
        img = R.drawable.sample_image_07,
        descroption = "사진 6666",
    ),
)
