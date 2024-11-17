package com.hipaduck.imagerotator.presentation.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.hipaduck.imagerotator.domain.model.Photo
import com.hipaduck.imagerotator.presentation.viewmodel.MainViewModel

@Composable
fun MainScreen(
    onLaunchPickMultipleImages: () -> Unit = {},
    onImagesPickedCallback: (callback: (List<Uri>) -> Unit) -> Unit = {},
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val onImagesPicked: (List<Uri>) -> Unit = { uris ->
        Log.d("ImageRotator", "onImagesPicked: $uris")

        if (uris.isNotEmpty()) {
            // 선택된 이미지 URI들을 리스트에 추가하기
            viewModel.fetchSelectedImage(uris)
            showSelectedImages(
                context,
                viewModel.countSelectedImages()
            ) // 가상의 함수이며, 실제로는 viewModel에서 수행하고 나서 별도의 이벤트를 받아 수행할 예정
        } else {
            Toast.makeText(
                context,
                "No images selected.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    onImagesPickedCallback(onImagesPicked)

    val photoList by viewModel.photoList.collectAsState()

    PhotoLister(
        photoList = photoList,
        modifier = Modifier.fillMaxWidth(),
        onLoadNewPhoto = {
            onLaunchPickMultipleImages()
            /*viewModel.loadRandomPhotos()*/
        }
    )
}

private fun showSelectedImages(context: Context, count: Int) {
    Toast.makeText(context, "Selected images are added.($count)", Toast.LENGTH_SHORT).show()
}

@Composable
fun PhotoLister(
    photoList: List<Photo>,
    modifier: Modifier,
    onLoadNewPhoto: () -> Unit
) {
    if (photoList.isEmpty()) {
        IconButton(onClick = { onLoadNewPhoto() }) {
            Icon(Icons.Filled.Add, "Add photos")
        }
    } else {
        Box(modifier = modifier) {
            LazyVerticalStaggeredGrid( // this is released as tester, so it may have issues
                columns = StaggeredGridCells.Fixed(3),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                content = {
                    items(photoList) { photo ->
                        // When data set is changed, it may cause problems when items doesn't have any status.
                        // So, user may lose it's scroll position. To fix this issue, get a stable and unique key for the item.
                        // https://developer.android.com/develop/ui/compose/lists?hl=ko#item-keys

                        // coil, async image
                        AsyncImage(
                            model = photo.url,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ActionButton(onClick: () -> Unit) {
    Column {
        Row {
            ExtendedFloatingActionButton(
                onClick = { onClick() }, // do something
                icon = { Icon(Icons.Filled.Add, "Action button to add gallery photos.") },
                text = { Text(text = "Add photos") },
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}