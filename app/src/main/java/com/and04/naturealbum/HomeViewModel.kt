package com.and04.naturealbum

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _dialogData = MutableStateFlow(DialogData())
    val dialogData: StateFlow<DialogData> = _dialogData

    fun showDialog(dialogData: DialogData) {
        _dialogData.value = dialogData
    }
    fun closeDialog(){
        _dialogData.value = _dialogData.value.copy(isShow = false)
    }
}
