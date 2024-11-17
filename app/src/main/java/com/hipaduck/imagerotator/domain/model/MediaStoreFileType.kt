package com.hipaduck.imagerotator.domain.model

import android.net.Uri
import android.provider.MediaStore

enum class MediaStoreFileType(
    val externalContentUri: Uri,
    val mimeType: String,
    val pathByPictures: String
) {
    IMAGE(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*", "/PhotoRotator"),
}