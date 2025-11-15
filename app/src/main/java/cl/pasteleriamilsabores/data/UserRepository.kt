package cl.pasteleriamilsabores.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object UserRepository {
    private const val PREFS_NAME = "pasteleria_users"
    private const val USERS_KEY = "users"
    private const val CURRENT_USER_KEY = "current_user"

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun registrarUsuario(usuario: Usuario): Boolean {
        val usuarios = obtenerUsuarios().toMutableList()

        // Verificar si el email ya existe
        if (usuarios.any { it.email == usuario.email }) {
            return false
        }

        usuarios.add(usuario)
        guardarUsuarios(usuarios)
        return true
    }

    fun login(email: String, password: String): Usuario? {
        val usuarios = obtenerUsuarios()
        return usuarios.find { it.email == email && it.password == password }
    }

    fun obtenerUsuarioPorEmail(email: String): Usuario? {
        val usuarios = obtenerUsuarios()
        return usuarios.find { it.email == email }
    }

    fun guardarUsuarioActual(usuario: Usuario?) {
        val usuarioJson = if (usuario != null) gson.toJson(usuario) else ""
        sharedPreferences.edit().putString(CURRENT_USER_KEY, usuarioJson).apply()
    }

    fun obtenerUsuarioActual(): Usuario? {
        val usuarioJson = sharedPreferences.getString(CURRENT_USER_KEY, "")
        return if (usuarioJson.isNullOrEmpty()) {
            null
        } else {
            gson.fromJson(usuarioJson, Usuario::class.java)
        }
    }

    fun cerrarSesion() {
        sharedPreferences.edit().remove(CURRENT_USER_KEY).apply()
    }

    private fun obtenerUsuarios(): List<Usuario> {
        val usuariosJson = sharedPreferences.getString(USERS_KEY, "[]")
        val listType = object : TypeToken<List<Usuario>>() {}.type
        return gson.fromJson(usuariosJson, listType)
    }

    private fun guardarUsuarios(usuarios: List<Usuario>) {
        val usuariosJson = gson.toJson(usuarios)
        sharedPreferences.edit().putString(USERS_KEY, usuariosJson).apply()
    }
}