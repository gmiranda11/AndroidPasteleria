package cl.pasteleriamilsabores.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.pasteleriamilsabores.data.UserRepository
import cl.pasteleriamilsabores.data.Usuario
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    var usuarioActual = mutableStateOf<Usuario?>(null)
    var errorMessage = mutableStateOf("")
    var infoMessage = mutableStateOf("")
    var isLoading = mutableStateOf(false) // Necesario para tu UI

    init {
        UserRepository.initialize(application)
        usuarioActual.value = UserRepository.obtenerUsuarioActual()
    }

    // --- LOGIN CONECTADO AL BACKEND ---
    // Cambio importante: Ahora recibe un 'onSuccess' en lugar de retornar Boolean
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        isLoading.value = true
        errorMessage.value = ""

        viewModelScope.launch {
            val usuario = UserRepository.login(email, password)
            isLoading.value = false

            if (usuario != null) {
                usuarioActual.value = usuario
                onSuccess() // Llamamos a la navegación solo si fue éxito
            } else {
                errorMessage.value = "Credenciales incorrectas o error de conexión"
            }
        }
    }

    // --- REGISTRO CONECTADO AL BACKEND ---
    fun registrarUsuario(
        nombre: String, email: String, password: String,
        fechaNacimiento: String, telefono: String,
        esEstudianteDuoc: Boolean, recibirNewsletter: Boolean,
        onSuccess: () -> Unit // Nuevo parámetro callback
    ) {
        isLoading.value = true
        errorMessage.value = ""

        val nuevoUsuario = Usuario(
            id = "",
            nombre = nombre,
            email = email,
            password = password,
            fechaNacimiento = fechaNacimiento,
            telefono = telefono,
            esEstudianteDuoc = esEstudianteDuoc,
            recibirNewsletter = recibirNewsletter
        )

        viewModelScope.launch {
            val exito = UserRepository.registrarUsuario(nuevoUsuario)
            isLoading.value = false

            if (exito) {
                mostrarMensajeInfo("Cuenta creada con éxito")
                onSuccess()
            } else {
                errorMessage.value = "Error al registrar. El email podría estar en uso."
            }
        }
    }

    // --- MÉTODOS PENDIENTES (Para que no fallen las otras pantallas) ---
    // Estos por ahora retornan false o simulan éxito localmente para no romper EditProfile/ChangePass

    fun actualizarPerfil(nombre: String, telefono: String, fechaNacimiento: String, recibirNewsletter: Boolean): Boolean {
        // TODO: Conectar con PUT /api/usuarios en el futuro
        // Por ahora actualizamos solo localmente para la demo
        val current = usuarioActual.value ?: return false
        val updated = current.copy(
            nombre = nombre, telefono = telefono,
            fechaNacimiento = fechaNacimiento, recibirNewsletter = recibirNewsletter
        )
        UserRepository.guardarUsuarioActual(updated)
        usuarioActual.value = updated
        mostrarMensajeInfo("Perfil actualizado (Local)")
        return true
    }

    fun cambiarPassword(passwordActual: String, nuevaPassword: String, confirmarPassword: String): Boolean {
        // TODO: Conectar con backend
        mostrarMensajeInfo("Función no disponible en modo online aún")
        return true
    }

    fun actualizarPreferenciaNewsletter(activa: Boolean) {
        val current = usuarioActual.value ?: return
        val updated = current.copy(recibirNewsletter = activa)
        UserRepository.guardarUsuarioActual(updated)
        usuarioActual.value = updated
    }

    fun cerrarSesion() {
        UserRepository.cerrarSesion()
        usuarioActual.value = null
    }

    fun limpiarError() {
        errorMessage.value = ""
    }

    fun mostrarMensajeInfo(msg: String) {
        infoMessage.value = msg
    }

    fun limpiarMensajeInfo() {
        infoMessage.value = ""
    }
}