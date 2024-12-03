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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.and04.naturealbum.R
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.ui.model.UiState
import com.and04.naturealbum.ui.savephoto.SavePhotoViewModel
import com.and04.naturealbum.utils.toColor

@Composable
fun LabelSearchScreen(
    onSelected: (Label) -> Unit,
    viewModel: LabelSearchViewModel = hiltViewModel(),
    savePhotoViewModel: SavePhotoViewModel,
) {
    val uiState = savePhotoViewModel.uiState.collectAsStateWithLifecycle()
    val labelsState = viewModel.labels.collectAsStateWithLifecycle()

    val query = rememberSaveable { mutableStateOf("") }
    val randomColor = rememberSaveable { mutableStateOf("") }

    LabelSearchScreen(
        uiState = uiState,
        query = query,
        labelsState = labelsState,
        randomColor = randomColor,
        onSelected = onSelected,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelSearchScreen(
    uiState: State<UiState<String>>,
    query: MutableState<String>,
    labelsState: State<List<Label>>,
    randomColor: MutableState<String>,
    onSelected: (Label) -> Unit,
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
        SearchContent(
            innerPadding = innerPadding,
            uiState = uiState,
            query = query,
            labelsState = labelsState,
            randomColor = randomColor,
            onSelected = onSelected,
        )
    }
}


@Composable
private fun SearchContent(
    innerPadding: PaddingValues,
    uiState: State<UiState<String>>,
    query: MutableState<String>,
    labelsState: State<List<Label>>,
    randomColor: MutableState<String>,
    onSelected: (Label) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {

        LabelTextField(query = query)

        Text(
            modifier = Modifier.padding(12.dp),
            text = stringResource(R.string.label_search_select_create),
            style = MaterialTheme.typography.bodyMedium
        )

        LabelChipList(
            query = query,
            labelsState = labelsState,
            onSelected = onSelected,
        )

        if (query.value.isNotEmpty()) {
            CreateLabelContent(
                query = query,
                labelsState = labelsState,
                randomColor = randomColor,
                onSelected = onSelected,
            )
        }

//        TODO 추후 시연할 때 주석 해제
//        GeminiLabelContent(
//            uiState = uiState,
//            labelsState = labelsState,
//            randomColor = randomColor,
//            onSelected = onSelected,
//        )


    }
}

@Composable
fun LabelTextField(
    query: MutableState<String>,
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = query.value,
        onValueChange = { changeQuery ->
            if (changeQuery.length > 100) return@TextField
            else query.value = changeQuery
        },
        placeholder = { Text(stringResource(R.string.label_search_label_search)) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
    )
}

@Composable
fun UnderLineSuggestionChip(
    label: Label,
    onSelected: (Label) -> Unit,
) {
    SuggestionChip(
        modifier = Modifier
            .padding(start = 12.dp),
        onClick = { onSelected(label) },
        label = { Text(label.name) },

        colors = AssistChipDefaults.assistChipColors(
            containerColor = label.backgroundColor.toColor(),
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

@Composable
fun LabelChipList(
    query: State<String>,
    labelsState: State<List<Label>>,
    onSelected: (Label) -> Unit,
) {
    LazyColumn {
        val queryLabelList =
            labelsState.value.filter { label -> label.name.contains(query.value) }

        items(
            items = queryLabelList,
            key = { label -> label.id }
        ) { label ->
            UnderLineSuggestionChip(label, onSelected)
        }
    }
}

@Composable
fun CreateLabelContent(
    query: MutableState<String>,
    labelsState: State<List<Label>>,
    randomColor: MutableState<String>,
    onSelected: (Label) -> Unit,
) {
    val context = LocalContext.current

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
                if (query.value.isBlank()) {
                    Toast.makeText(context, blankToastText, Toast.LENGTH_LONG).show()
                    return@SuggestionChip
                } else if (labelsState.value.any { label -> label.name == query.value }) {
                    Toast.makeText(context, nestToastText, Toast.LENGTH_LONG).show()
                    return@SuggestionChip
                }

                onSelected(
                    Label(
                        backgroundColor = randomColor.value,
                        name = query.value
                    )
                )
            },
            label = { Text(query.value) },
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor =
                randomColor.value.ifBlank {
                    randomColor.value = getRandomColor()
                    randomColor.value
                }.toColor(),
                labelColor = if (Color(randomColor.value.toLong(16)).luminance() > 0.5f) Color.Black else Color.White
            )
        )
    }
}

@Composable
fun GeminiLabelContent(
    uiState: State<UiState<String>>,
    labelsState: State<List<Label>>,
    randomColor: MutableState<String>,
    onSelected: (Label) -> Unit,
) {
    Row(
        modifier = Modifier.padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.label_by_gemini_search_create))

        Spacer(Modifier.size(4.dp))

        GeminiLabelChip(
            uiState = uiState,
            labelsState = labelsState,
            randomColor = randomColor,
            onSelected = onSelected,
        )
    }
}

@Composable
fun GeminiLabelChip(
    uiState: State<UiState<String>>,
    labelsState: State<List<Label>>,
    randomColor: MutableState<String>,
    onSelected: (Label) -> Unit,
) {
    val context = LocalContext.current
    val nestToastText = stringResource(R.string.label_search_nest_label_toast)

    when (val success = uiState.value) {
        is UiState.Success -> {
            val labelByGemini = success.data.trim()
            SuggestionChip(
                onClick = {
                    if (labelsState.value.any { label -> label.name == labelByGemini }) {
                        Toast.makeText(context, nestToastText, Toast.LENGTH_LONG).show()
                        return@SuggestionChip
                    }

                    onSelected(
                        Label(
                            backgroundColor = randomColor.value,
                            name = labelByGemini
                        )
                    )
                },
                label = {
                    Text(
                        text = labelByGemini,
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = Color(
                        randomColor.value.ifBlank {
                            randomColor.value = getRandomColor()
                            randomColor.value
                        }.toLong(16)
                    ),
                    labelColor = if (Color(randomColor.value.toLong(16)).luminance() > 0.5f) Color.Black else Color.White
                )
            )
        }

        is UiState.Idle, UiState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.width(32.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        is UiState.Error<*> -> {
            /* TODO ERROR */
        }
    }
}

@Preview
@Composable
fun PreviewFunc() {
    val uiState = remember { mutableStateOf(UiState.Success("새")) }
    val dummy = remember { mutableStateOf("") }
    val listDummy = remember { mutableStateOf<List<Label>>(listOf()) }

    LabelSearchScreen(
        uiState = uiState,
        query = dummy,
        labelsState = listDummy,
        randomColor = dummy,
        onSelected = {},
    )
}
