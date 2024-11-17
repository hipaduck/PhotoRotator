package com.hipaduck.imagerotator.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.hipaduck.imagerotator.R
import com.hipaduck.imagerotator.domain.model.Direction
import com.hipaduck.imagerotator.domain.model.MediaStoreFileType
import com.hipaduck.imagerotator.domain.model.Photo
import com.hipaduck.imagerotator.presentation.extension.inverseHorizontalBy
import com.hipaduck.imagerotator.presentation.extension.inverseVerticalBy
import com.hipaduck.imagerotator.presentation.extension.rotatedBy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext private val applicationContext: Context) :
    ViewModel() {
    private val _text = MutableStateFlow("Hello, Compose")
    val text: StateFlow<String>
        get() = _text

    private val _photoList = MutableStateFlow<List<Photo>>(emptyList())
    val photoList: StateFlow<List<Photo>>
        get() = _photoList

    // 외부에서 가져온 이미지들을 보관
    private val fetchedImages = mutableListOf<Uri>()

    private val directionControls = mutableListOf<Direction>()

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

    fun inputDirection(direction: Direction) {
        // input direction saves values from here
        directionControls.add(direction)
    }

    fun clearDirections() {
        // when complete or cancel, clear values from here
        directionControls.clear()
        // TODO: simplify control if we can
    }

    fun rotateSavePhotos() {
        // before dismissing,rotate photos

        for (photo in _photoList.value) {
            val bitmap = editPhotoByDirectionControls(Uri.parse(photo.url))
            processOnBitmapReady(bitmap)
        }

        // TODO: maybe show loading status to block user's interaction or etc

        // clear every thing when it's ready
        clearDirections()
        _photoList.value = emptyList()
    }

    private fun processOnBitmapReady(saveBitmap: Bitmap?): Uri {
        val stream = ByteArrayOutputStream()

        saveBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        val fileName = SimpleDateFormat(
            "yyyyMMdd_HHmmssSSS",
            Locale.getDefault()
        ).format(System.currentTimeMillis()) + ".jpg"
        val outUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createFile(fileName, MediaStoreFileType.IMAGE, byteArray)
        } else {
            createFileBeforeAndroidQ(fileName, MediaStoreFileType.IMAGE, byteArray)
        }

        saveBitmap?.recycle()
        return outUri
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun createFile(fileName: String, fileType: MediaStoreFileType, fileContents: ByteArray): Uri {
        val contentValues = ContentValues()

        with(contentValues) {
            when (fileType) {
                MediaStoreFileType.IMAGE -> {
                    put(
                        MediaStore.Files.FileColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + fileType.pathByPictures
                    )
                }
            }
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
            put(MediaStore.Files.FileColumns.MIME_TYPE, fileType.mimeType)
            put(MediaStore.Files.FileColumns.IS_PENDING, 1)
        }

        val uri = applicationContext.contentResolver.insert(
            fileType.externalContentUri,
            contentValues
        )

        val parcelFileDescriptor =
            applicationContext.contentResolver.openFileDescriptor(uri!!, "w", null)

        val fileOutputStream = FileOutputStream(parcelFileDescriptor!!.fileDescriptor)
        fileOutputStream.write(fileContents)
        fileOutputStream.close()

        with(contentValues) {
            clear()
            put(MediaStore.Files.FileColumns.IS_PENDING, 0)
        }
        applicationContext.contentResolver.update(uri, contentValues, null, null)

        return uri
    }

    private fun createFileBeforeAndroidQ(
        fileName: String,
        fileType: MediaStoreFileType,
        fileContents: ByteArray
    ): Uri {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .toString() + File.separator + fileType.pathByPictures
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }

        val imgFile = File(file, fileName)
        val fileOutputStream = FileOutputStream(imgFile)
        fileOutputStream.write(fileContents)
        fileOutputStream.close()

        val values = ContentValues()
        with(values) {
            put(MediaStore.Images.Media.TITLE, fileName)
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.Images.Media.DATA, imgFile.absolutePath)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val uri = applicationContext.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        return uri!!
    }

    private fun editPhotoByDirectionControls(uri: Uri): Bitmap? {
        var editedBitmap = getBitmapFromUri(uri) ?: return null
        for (direction in directionControls) {
            when (direction) {
                Direction.RIGHT -> {
                    editedBitmap = editedBitmap.rotatedBy(90.0f)
                }

                Direction.LEFT -> {
                    editedBitmap = editedBitmap.rotatedBy(-90.0f)
                }

                Direction.FLIP_UPSIDE_DOWN -> {
                    editedBitmap = editedBitmap.inverseVerticalBy()
                }

                else -> { // FLIP_LEFT_AND_RIGHT
                    editedBitmap = editedBitmap.inverseHorizontalBy()
                }
            }
        }
        return editedBitmap
    }

    fun getDefaultBitmap(): Bitmap {
        return BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.ic_launcher_foreground
        )
    }

    fun getBitmapFromUri(uri: Uri): Bitmap? {
        try {
            return if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(
                    applicationContext.contentResolver,
                    uri
                )
            } else {
                val source =
                    ImageDecoder.createSource(applicationContext.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: IOException) {
            return null
        }
    }

    private fun savePhotos() {}

    private fun showToastMessage(message: String) {}
}