package cl.pasteleriamilsabores.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cl.pasteleriamilsabores.data.UserRepository
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraViewModel : ViewModel() {
    val capturedImage = mutableStateOf<Uri?>(null)
    val shouldShowCamera = mutableStateOf(false)

    // Cargar foto al inicializar
    init {
        cargarFotoGuardada()
    }

    private fun cargarFotoGuardada() {
        val usuarioActual = UserRepository.obtenerUsuarioActual()
        usuarioActual?.email?.let { email ->
            val fotoUriString = UserRepository.obtenerFotoPerfil(email)
            fotoUriString?.let { uriString ->
                capturedImage.value = Uri.parse(uriString)
            }
        }
    }

    fun captureImage(uri: Uri) {
        capturedImage.value = uri
        shouldShowCamera.value = false

        // Guardar la foto en SharedPreferences
        val usuarioActual = UserRepository.obtenerUsuarioActual()
        usuarioActual?.email?.let { email ->
            UserRepository.guardarFotoPerfil(email, uri.toString())
        }
    }

    fun startCamera() {
        shouldShowCamera.value = true
    }

    fun cancelCamera() {
        shouldShowCamera.value = false
    }

    fun clearImage() {
        capturedImage.value = null
        // También eliminar de SharedPreferences al limpiar
        val usuarioActual = UserRepository.obtenerUsuarioActual()
        usuarioActual?.email?.let { email ->
            UserRepository.eliminarFotoPerfil(email)
        }
    }

    // Crear archivo para guardar la foto
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir("profile_pictures")
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    // Convertir Bitmap a Uri (opcional, si necesitas procesar la imagen)
    fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val file = createImageFile(context)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun selectImageFromGallery(uri: Uri) {
        capturedImage.value = uri

        // Guardar la foto seleccionada
        val usuarioActual = UserRepository.obtenerUsuarioActual()
        usuarioActual?.email?.let { email ->
            UserRepository.guardarFotoPerfil(email, uri.toString())
        }
    }

    // Función para abrir selector (cámara o galería)
    val shouldShowImagePicker = mutableStateOf(false)
}