package cl.pasteleriamilsabores.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberCameraPermissionState(): PermissionState {
    return rememberPermissionState(android.Manifest.permission.CAMERA)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionHandler(
    permissionState: PermissionState,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            onPermissionGranted()
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    // Mostrar diálogo si el permiso fue denegado permanentemente
    if (!permissionState.status.isGranted && !permissionState.status.shouldShowRationale) {
        AlertDialog(
            onDismissRequest = onPermissionDenied,
            title = { Text("Permiso de Cámara Requerido") },
            text = {
                Text("La aplicación necesita acceso a la cámara para tomar fotos de perfil. Por favor, habilita el permiso en Configuración.")
            },
            confirmButton = {
                Button(onClick = onPermissionDenied) {
                    Text("Entendido")
                }
            }
        )
    }
}