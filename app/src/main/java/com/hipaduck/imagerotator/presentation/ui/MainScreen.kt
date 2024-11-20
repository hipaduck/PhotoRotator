package com.hipaduck.imagerotator.presentation.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.hipaduck.imagerotator.R
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
        viewModel = viewModel,
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
    viewModel: MainViewModel,
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

        val shouldShowRotateButton = remember {
            mutableStateOf(photoList.isNotEmpty())
        }
        val shouldShowDialog = remember {
            mutableStateOf(false)
        }

        RotateButton(shouldShowRotateButton, onClick = {
            shouldShowDialog.value = true
        })

        val bitmap = viewModel.getBitmapFromUri(Uri.parse(photoList.last().url))
            ?: viewModel.getDefaultBitmap()

        RotateDialog(
            shouldShowDialog = shouldShowDialog,
            viewModel = viewModel,
            bitmap = bitmap,
            onDismiss = {
                shouldShowDialog.value = false
            }, onConfirm = {
                shouldShowDialog.value = false
                viewModel.rotateSavePhotos()
            })
    }
}

@Composable
fun RotateButton(shouldShowRotateButton: MutableState<Boolean>, onClick: () -> Unit) {
    if (shouldShowRotateButton.value) {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 32.dp),
                onClick = { onClick() },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.rotate_right),
                        "Action button to rotate photos."
                    )
                },
                text = { Text(text = "Rotate photos") },
            )
        }
    }

}