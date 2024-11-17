package com.hipaduck.imagerotator.presentation.viewmodel

import android.net.Uri
import android.util.Log
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

    // 외부에서 가져온 이미지들을 보관
    private val fetchedImages = mutableListOf<Uri>()

    fun fetchSelectedImage(uris: List<Uri>) {
        fetchedImages.clear()
        fetchedImages.addAll(uris)

        val photoList = _photoList.value.toMutableList()
        for (uri in uris) {
            photoList.add(
                Photo(
                    "$uri.png",
                    uri.toString()
                )
            )
        }
        _photoList.value = photoList
    }

    // 선택된 이미지의 개수를 반환: 현재 특별한 용도는 없음
    fun countSelectedImages(): Int {
        Log.d("ImageRotator", "countSelectedImages: ${fetchedImages.size}")
        for (uri in fetchedImages) {
            Log.d("ImageRotator", "uri: $uri")
        }
        return fetchedImages.size
    }

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