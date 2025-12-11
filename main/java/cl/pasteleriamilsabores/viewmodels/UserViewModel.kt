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

    fun actualizarPerfil(
        nombre: String,
        telefono: String,
        fechaNacimiento: String,
        recibirNewsletter: Boolean
    ): Boolean {
        val usuarioActualValue = usuarioActual.value ?: return false

        val usuarioActualizado = usuarioActualValue.copy(
            nombre = nombre,
            telefono = telefono,
            fechaNacimiento = fechaNacimiento,
            recibirNewsletter = recibirNewsletter
        )

        val exito = UserRepository.actualizarUsuario(usuarioActualizado)
        if (exito) {
            usuarioActual.value = usuarioActualizado
            mostrarMensajeInfo("Perfil actualizado correctamente")
        } else {
            errorMessage.value = "Error al actualizar el perfil"
        }

        return exito
    }

    // Función para cambiar contraseña
    fun cambiarPassword(
        passwordActual: String,
        nuevaPassword: String,
        confirmarPassword: String
    ): Boolean {
        val usuarioActual = usuarioActual.value ?: return false

        // Validaciones
        if (usuarioActual.password != passwordActual) {
            errorMessage.value = "La contraseña actual es incorrecta"
            return false
        }

        if (nuevaPassword != confirmarPassword) {
            errorMessage.value = "Las contraseñas no coinciden"
            return false
        }

        if (nuevaPassword.length < 6) {
            errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        val exito = UserRepository.cambiarPassword(usuarioActual.email, nuevaPassword)
        if (exito) {
            // Actualizar el usuario en el ViewModel
            this.usuarioActual.value = this.usuarioActual.value?.copy(password = nuevaPassword)
            mostrarMensajeInfo("Contraseña cambiada correctamente")
            errorMessage.value = ""
        } else {
            errorMessage.value = "Error al cambiar la contraseña"
        }

        return exito
    }
    fun actualizarPreferenciaNewsletter(recibirNewsletter: Boolean): Boolean {
        val usuarioActualValue = usuarioActual.value ?: return false

        val usuarioActualizado = usuarioActualValue.copy(
            recibirNewsletter = recibirNewsletter
        )

        val exito = UserRepository.actualizarUsuario(usuarioActualizado)
        if (exito) {
            usuarioActual.value = usuarioActualizado
            mostrarMensajeInfo(
                if (recibirNewsletter) "Newsletter activado"
                else "Newsletter desactivado"
            )
        } else {
            errorMessage.value = "Error al actualizar preferencias"
        }

        return exito
    }
}