package com.hipaduck.imagerotator.presentation.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.hipaduck.imagerotator.domain.model.Photo
import com.hipaduck.imagerotator.presentation.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val photoList by viewModel.photoList.collectAsState()
    PhotoLister(
        photoList = photoList,
        modifier = Modifier.fillMaxWidth(),
        onLoadNewPhoto = { viewModel.loadRandomPhotos() }
    )
}

@Composable
fun PhotoLister(
    photoList: List<Photo>,
    modifier: Modifier,
    onLoadNewPhoto: () -> Unit
) {
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
        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
            ActionButton {
                onLoadNewPhoto()
            }
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