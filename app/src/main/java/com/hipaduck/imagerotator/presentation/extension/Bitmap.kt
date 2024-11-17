package com.hipaduck.imagerotator.presentation.extension

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotatedBy(angle: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(angle) }
    val originalCopy = this.copy(Bitmap.Config.ARGB_8888, true)
    return Bitmap.createBitmap(originalCopy, 0, 0, width, height, matrix, true)
}

fun Bitmap.inverseVerticalBy(): Bitmap {
    val matrix = Matrix().apply { setScale(1f, -1f) }
    val originalCopy = this.copy(Bitmap.Config.ARGB_8888, true)
    return Bitmap.createBitmap(originalCopy, 0, 0, width, height, matrix, true)
}

fun Bitmap.inverseHorizontalBy(): Bitmap {
    val matrix = Matrix().apply { setScale(-1f, 1f) }
    val originalCopy = this.copy(Bitmap.Config.ARGB_8888, true)
    return Bitmap.createBitmap(originalCopy, 0, 0, width, height, matrix, true)
}