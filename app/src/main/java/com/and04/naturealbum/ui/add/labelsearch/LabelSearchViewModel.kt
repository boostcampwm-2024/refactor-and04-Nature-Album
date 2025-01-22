package com.and04.naturealbum.ui.add.labelsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.repository.local.LabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelSearchViewModel @Inject constructor(
    private val labelRepository: LabelRepository,
) : ViewModel() {
    private val _labels = MutableStateFlow(emptyList<Label>())
    val labels: StateFlow<List<Label>> = _labels

    init {
        fetchLabels()
    }

    private fun fetchLabels() {
        viewModelScope.launch {
            _labels.emit(labelRepository.getLabels())
        }
    }
}
