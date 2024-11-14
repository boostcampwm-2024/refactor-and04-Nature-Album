package com.and04.naturealbum.ui.labelsearch

import android.widget.Toast
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
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.Label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelSearchScreen(
    onSelected: (Label) -> Unit = {}
) {
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
        SearchContent(innerPadding, onSelected)
    }
}

@Composable
private fun SearchContent(
    innerPadding: PaddingValues,
    onSelected: (Label) -> Unit,
    labelSearchViewModel: LabelSearchViewModel = hiltViewModel()
) {
    var query by rememberSaveable { mutableStateOf("") }
    var randomColor by rememberSaveable { mutableStateOf("") }
    val labelsState by labelSearchViewModel.labels.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = { changeQuery ->
                if (changeQuery.length > 100) return@TextField
                else query = changeQuery
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
            val queryLabelList = labelsState.filter { label -> label.name.contains(query) }
            items(queryLabelList) { label ->
                UnderLineSuggestionChip(label, onSelected)
            }
        }

        Row(
            modifier = Modifier.padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val blankToastText = stringResource(R.string.label_search_blank_query_toast)
            val nestToastText = stringResource(R.string.label_search_nest_label_toast)
            Text(stringResource(R.string.label_search_create))
            Spacer(Modifier.size(4.dp))
            SuggestionChip(
                onClick = {
                    if (query.isBlank()) {
                        Toast.makeText(context, blankToastText, Toast.LENGTH_LONG).show()
                        return@SuggestionChip
                    }
                    else if(labelsState.find { it.name == query } != null){
                        Toast.makeText(context, nestToastText, Toast.LENGTH_LONG).show()
                        return@SuggestionChip
                    }

                    onSelected(
                        Label(
                            backgroundColor = randomColor,
                            name = query
                        )
                    )
                },
                label = { Text(query) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = Color(
                        randomColor.ifBlank {
                            randomColor = getRandomColor()
                            randomColor
                        }.toLong(16)
                    ),
                    labelColor = if (Color(randomColor.toLong(16)).luminance() > 0.5f) Color.Black else Color.White
                )
            )
        }
    }
}

@Composable
fun UnderLineSuggestionChip(
    label: Label,
    onSelected: (Label) -> Unit
) {
    SuggestionChip(
        modifier = Modifier
            .padding(start = 12.dp),
        onClick = { onSelected(label) },
        label = { Text(label.name) },

        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(label.backgroundColor.toLong(16)),
            labelColor = if (Color(label.backgroundColor.toLong(16)).luminance() > 0.5f) Color.Black else Color.White
        )
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