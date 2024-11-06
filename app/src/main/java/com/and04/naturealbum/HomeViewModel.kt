package com.and04.naturealbum

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _uri = MutableStateFlow<Uri?>(null)
    val uri: StateFlow<Uri?> = _uri

    private val _dialogData = MutableStateFlow(DialogData())
    val dialogData: StateFlow<DialogData> = _dialogData

    fun showDialog(dialogData: DialogData) {
        _dialogData.value = dialogData
    }

    fun dismissDialog() {
        _dialogData.value = DialogData()
    }
}
