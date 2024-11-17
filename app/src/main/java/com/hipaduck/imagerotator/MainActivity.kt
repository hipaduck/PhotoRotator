package com.hipaduck.imagerotator

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hipaduck.imagerotator.presentation.ui.MainScreen
import com.hipaduck.imagerotator.ui.theme.ImageRotatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var onImagesPicked: ((List<Uri>) -> Unit)? = null
    private val pickMultipleMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            onImagesPicked?.invoke(uris)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ImageRotator", "onCreate")

        setContent {
            ImageRotatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(onLaunchPickMultipleImages = {
                        Log.d("ImageRotator", "onLaunchPickMultipleImages")
                        pickMultipleMediaLauncher.launch(PickVisualMediaRequest())
                    }, onImagesPickedCallback = { callback ->
                        onImagesPicked = callback
                    })
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("ImageRotator", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ImageRotator", "onResume")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImageRotatorTheme {
        MainScreen()
    }
}