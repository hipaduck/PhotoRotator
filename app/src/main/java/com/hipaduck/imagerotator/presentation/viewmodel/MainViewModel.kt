package com.hipaduck.imagerotator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.hipaduck.imagerotator.domain.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _text = MutableStateFlow("Hello, Compose")
    val text: StateFlow<String>
        get() = _text

    private val _photoList = MutableStateFlow<List<Photo>>(emptyList())
    val photoList: StateFlow<List<Photo>>
        get() = _photoList

    fun updateText(newText: String) {
        _text.value = newText
    }

    fun loadRandomPhotos() {
        val photoList = _photoList.value.toMutableList()
        for (i in 0..20) {
            photoList.add(
                Photo(
                    Math.random().toString(),
                    "https://picsum.photos/" + (Random().nextInt(5) + 1) * 100 + "/" + (Random().nextInt(
                        5
                    ) + 1) * 100
                )
            )
        }
        _photoList.value = photoList
    }
}