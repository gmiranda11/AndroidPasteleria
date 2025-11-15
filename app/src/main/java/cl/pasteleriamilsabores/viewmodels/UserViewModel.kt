package cl.pasteleriamilsabores.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cl.pasteleriamilsabores.data.UserRepository
import cl.pasteleriamilsabores.data.Usuario
import java.util.*

class UserViewModel : ViewModel() {
    val usuarioActual = mutableStateOf<Usuario?>(null)
    val errorMessage = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val infoMessage = mutableStateOf("")

    init {
        // Cargar usuario actual al iniciar
        usuarioActual.value = UserRepository.obtenerUsuarioActual()
    }

    fun registrarUsuario(
        nombre: String,
        email: String,
        password: String,
        fechaNacimiento: String,
        telefono: String,
        esEstudianteDuoc: Boolean,
        recibirNewsletter: Boolean
    ): Boolean {
        isLoading.value = true
        errorMessage.value = ""

        return try {
            val nuevoUsuario = Usuario(
                id = UUID.randomUUID().toString(),
                nombre = nombre,
                email = email,
                password = password, // En una app real, esto debería estar encriptado
                fechaNacimiento = fechaNacimiento,
                telefono = telefono,
                esEstudianteDuoc = esEstudianteDuoc,
                recibirNewsletter = recibirNewsletter
            )

            val exito = UserRepository.registrarUsuario(nuevoUsuario)
            if (!exito) {
                errorMessage.value = "El email ya está registrado"
            }
            isLoading.value = false
            exito
        } catch (e: Exception) {
            errorMessage.value = "Error al registrar usuario: ${e.message}"
            isLoading.value = false
            false
        }
    }

    fun login(email: String, password: String): Boolean {
        isLoading.value = true
        errorMessage.value = ""

        return try {
            val usuario = UserRepository.login(email, password)
            if (usuario != null) {
                usuarioActual.value = usuario
                UserRepository.guardarUsuarioActual(usuario)
                true
            } else {
                errorMessage.value = "Email o contraseña incorrectos"
                false
            }
        } catch (e: Exception) {
            errorMessage.value = "Error al iniciar sesión: ${e.message}"
            false
        } finally {
            isLoading.value = false
        }
    }

    fun cerrarSesion() {
        usuarioActual.value = null
        UserRepository.cerrarSesion()
    }

    fun limpiarError() {
        errorMessage.value = ""
    }
    fun mostrarMensajeInfo(mensaje: String) {
        infoMessage.value = mensaje
    }

    fun limpiarMensajeInfo() {
        infoMessage.value = ""
    }
}