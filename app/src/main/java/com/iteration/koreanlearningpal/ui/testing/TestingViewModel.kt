package com.iteration.koreanlearningpal.ui.testing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TestingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val _validScore = MutableLiveData<Int>().apply {
        value = 0
    }
    val validScore : MutableLiveData<Int> = _validScore

    private val _invalidScore = MutableLiveData<Int>().apply {
        value = 0
    }
    val invalidScore : MutableLiveData<Int> = _invalidScore

    private val _rewindCount = MutableLiveData<Int>().apply {
        value = 0
    }
    val rewindCount : MutableLiveData<Int> = _rewindCount
}