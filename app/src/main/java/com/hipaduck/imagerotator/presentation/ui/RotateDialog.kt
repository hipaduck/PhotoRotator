package com.hipaduck.imagerotator.presentation.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hipaduck.imagerotator.R
import com.hipaduck.imagerotator.domain.model.Direction
import com.hipaduck.imagerotator.presentation.extension.inverseHorizontalBy
import com.hipaduck.imagerotator.presentation.extension.inverseVerticalBy
import com.hipaduck.imagerotator.presentation.extension.rotatedBy
import com.hipaduck.imagerotator.presentation.viewmodel.MainViewModel

@Composable
fun RotateDialog(
    shouldShowDialog: MutableState<Boolean>,
    viewModel: MainViewModel, // is this right thing to do? cuz hiltViewModel should be called on specific scope i guess
    bitmap: Bitmap,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (shouldShowDialog.value) {
        val sampleBitmap: MutableState<Bitmap> = remember {
            mutableStateOf(Bitmap.createBitmap(bitmap))
        }

        // is this alright? dismiss is called on confirmed too so it's clearing on init.
        // need to check if this is safe to do
        viewModel.clearDirections()
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SampleImage(sampleBitmap.value)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                    ) {
                        Button(
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(),
                            onClick = {
                                viewModel.inputDirection(Direction.RIGHT)
                                sampleBitmap.value = sampleBitmap.value.rotatedBy(90f)
                            }) {
                            Image(
                                painter = painterResource(id = R.drawable.rotate_right),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                        Button(
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(),
                            onClick = {
                                viewModel.inputDirection(Direction.LEFT)
                                sampleBitmap.value = sampleBitmap.value.rotatedBy(-90f)
                            }) {
                            Image(
                                painter = painterResource(id = R.drawable.rotate_left),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            )

                        }
                        Button(
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(),
                            onClick = {
                                viewModel.inputDirection(Direction.FLIP_LEFT_AND_RIGHT)
                                sampleBitmap.value = sampleBitmap.value.inverseHorizontalBy()
                            }) {
                            Image(
                                painter = painterResource(id = R.drawable.flip_left_right),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            )

                        }
                        Button(
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(),
                            onClick = {
                                viewModel.inputDirection(Direction.FLIP_UPSIDE_DOWN)
                                sampleBitmap.value = sampleBitmap.value.inverseVerticalBy()
                            }) {
                            Image(
                                painter = painterResource(id = R.drawable.flip_upside_down),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            )

                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween
                    ) {
                        TextButton(onClick = { onDismiss() }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = { onConfirm() }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SampleImage(sampleBitmap: Bitmap) {
    Log.d("GAEGUL", "sampleBitmap size : ${sampleBitmap.width}, ${sampleBitmap.height}")
    Image(
        bitmap = sampleBitmap.asImageBitmap(),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier
            .height(150.dp)
            .width(((sampleBitmap.width.toFloat() / sampleBitmap.height.toFloat()) * 150).dp)
    )
}

// no can do with viewmodel
//@Preview
//@Composable
//fun AlertDialogExamplePreview() {
//    RotateDialog(
//        shouldShowDialog = remember { mutableStateOf(true) },
//        onDismiss = {},
//        onConfirm = {},
//        viewModel = hiltViewModel(),
//        photo = Photo("tmp", "https://picsum.photos/")
//    )
//}