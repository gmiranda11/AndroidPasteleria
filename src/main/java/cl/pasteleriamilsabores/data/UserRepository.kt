package cl.pasteleriamilsabores.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import cl.pasteleriamilsabores.network.LoginRequest
import cl.pasteleriamilsabores.network.RetrofitClient
import com.google.gson.Gson

object UserRepository {
    private const val PREFS_NAME = "pasteleria_users"
    private const val CURRENT_USER_KEY = "current_user"

    // Prefijo para las claves de fotos en SharedPreferences
    private const val PHOTO_KEY_PREFIX = "photo_"

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ==========================================
    // 1. CONEXIÓN AL BACKEND (Login y Registro)
    // ==========================================

    suspend fun login(email: String, password: String): Usuario? {
        return try {
            val response = RetrofitClient.instance.loginUser(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // AHORA SÍ: Usamos los datos que vienen del backend
                val usuario = Usuario(
                    id = body.id,
                    nombre = body.nombre,
                    email = body.email,
                    password = "",
                    // Si viene nulo, ponemos texto vacío
                    fechaNacimiento = body.fechaNacimiento ?: "",
                    telefono = body.telefono ?: "",
                    esEstudianteDuoc = body.esEstudianteDuoc,
                    recibirNewsletter = body.recibirNewsletter
                )
                guardarUsuarioActual(usuario)
                usuario
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error login: ${e.message}")
            null
        }
    }

    suspend fun registrarUsuario(usuario: Usuario): Boolean {
        return try {
            // AHORA SÍ: Creamos el objeto completo con TODOS los datos
            val request = cl.pasteleriamilsabores.network.RegisterRequest(
                nombre = usuario.nombre,
                email = usuario.email,
                password = usuario.password,
                fechaNacimiento = usuario.fechaNacimiento,
                telefono = usuario.telefono,
                esEstudianteDuoc = usuario.esEstudianteDuoc,
                recibirNewsletter = usuario.recibirNewsletter
            )

            // Enviamos la caja grande
            val response = RetrofitClient.instance.registerUser(request)

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("UserRepository", "Error registro: ${e.message}")
            false
        }
    }

    // ==========================================
    // 2. MANEJO DE SESIÓN LOCAL
    // ==========================================

    fun obtenerUsuarioActual(): Usuario? {
        if (!::sharedPreferences.isInitialized) return null
        val json = sharedPreferences.getString(CURRENT_USER_KEY, null) ?: return null
        return try {
            gson.fromJson(json, Usuario::class.java)
        } catch (e: Exception) { null }
    }

    fun guardarUsuarioActual(usuario: Usuario) {
        if (!::sharedPreferences.isInitialized) return
        val json = gson.toJson(usuario)
        sharedPreferences.edit().putString(CURRENT_USER_KEY, json).apply()
    }

    fun cerrarSesion() {
        if (!::sharedPreferences.isInitialized) return
        sharedPreferences.edit().remove(CURRENT_USER_KEY).apply()
    }

    fun guardarFotoPerfil(email: String, uri: String) {
        if (!::sharedPreferences.isInitialized) return
        sharedPreferences.edit().putString(PHOTO_KEY_PREFIX + email, uri).apply()
    }

    fun obtenerFotoPerfil(email: String): String? {
        if (!::sharedPreferences.isInitialized) return null
        return sharedPreferences.getString(PHOTO_KEY_PREFIX + email, null)
    }

    fun eliminarFotoPerfil(email: String) {
        if (!::sharedPreferences.isInitialized) return
        sharedPreferences.edit().remove(PHOTO_KEY_PREFIX + email).apply()
    }
}