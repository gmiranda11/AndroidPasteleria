package cl.pasteleriamilsabores.ui.components

import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.pasteleriamilsabores.viewmodels.CameraViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraViewModel: CameraViewModel = viewModel()

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.getInstance(context).get()
        cameraProvider = provider
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tomar Foto") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón para cambiar cámara
                FloatingActionButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            CameraSelector.LENS_FACING_BACK
                        } else {
                            CameraSelector.LENS_FACING_FRONT
                        }
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Cambiar cámara")
                }

                // Botón para capturar foto
                FloatingActionButton(
                    onClick = {
                        takePhoto(
                            imageCapture = imageCapture,
                            executor = cameraExecutor,
                            onImageCaptured = onImageCaptured,
                            context = context
                        )
                    }
                ) {
                    Icon(Icons.Default.Face, contentDescription = "Tomar foto")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CameraPreview(
                preview = preview,
                imageCapture = imageCapture,
                lensFacing = lensFacing,
                lifecycleOwner = lifecycleOwner,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

@Composable
fun CameraPreview(
    preview: Preview,
    imageCapture: ImageCapture,
    lensFacing: Int,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                preview.setSurfaceProvider(previewView.surfaceProvider)

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}

private fun takePhoto(
    imageCapture: ImageCapture,
    executor: ExecutorService,
    onImageCaptured: (Uri) -> Unit,
    context: Context
) {
    val photoFile = File(
        context.externalCacheDir?.absolutePath ?: context.cacheDir.absolutePath,
        "${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let { uri ->
                    onImageCaptured(uri)
                } ?: run {
                    onImageCaptured(Uri.fromFile(photoFile))
                }
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}