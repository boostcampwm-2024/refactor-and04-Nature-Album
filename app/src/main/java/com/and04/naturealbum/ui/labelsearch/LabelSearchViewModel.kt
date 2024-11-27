package com.and04.naturealbum.ui.labelsearch

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.ui.model.UiState
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelSearchViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {
    private val _labels = MutableStateFlow(emptyList<Label>())
    val labels: StateFlow<List<Label>> = _labels

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uiState: StateFlow<UiState<String>> = _uiState

    init {
        fetchLabels()
    }

    fun getGeneratedContent(bitmap: Bitmap?) {
        bitmap?.let { nonNullBitmap ->
            viewModelScope.launch {
                _uiState.emit(UiState.Loading)
                val model = Firebase.vertexAI.generativeModel(GEMINI_MODEL)
                val content = content {
                    image(nonNullBitmap)
                    text(GEMINI_PROMPT)
                }

                val result = model.generateContent(content)
                _uiState.emit(UiState.Success(result.text ?: ""))
            }
        }
    }

    private fun fetchLabels() {
        viewModelScope.launch {
            _labels.emit(repository.getLabels())
        }
    }

    companion object {
        private const val GEMINI_MODEL = "gemini-1.5-flash"
        private const val GEMINI_PROMPT =
            "이 이미지를 보고 어떤 생물인지 생물 도감의 이름(학명 또는 일반명)으로 가장 유사한 하나의 단어를 답해주세요. 학명이 있다면 학명을 우선 사용하세요. 추가 설명은 하지 마세요. 한국어를 사용하세요."
    }
}