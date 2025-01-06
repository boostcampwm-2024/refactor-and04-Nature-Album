package com.and04.naturealbum.ui.add.labelsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.local.LocalDataRepository
import com.and04.naturealbum.data.room.Label
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelSearchViewModel @Inject constructor(
    private val repository: LocalDataRepository
) : ViewModel() {
    private val _labels = MutableStateFlow(emptyList<Label>())
    val labels: StateFlow<List<Label>> = _labels

    init {
        fetchLabels()
    }

    private fun fetchLabels() {
        viewModelScope.launch {
            _labels.emit(repository.getLabels())
        }
    }
}