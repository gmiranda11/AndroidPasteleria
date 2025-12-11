package cl.pasteleriamilsabores.network

import cl.pasteleriamilsabores.data.Producto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Caja pequeña para Login
data class LoginRequest(val email: String, val password: String)

// NUEVO: Caja grande para Registro (Coincide con el Backend)
data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val fechaNacimiento: String,
    val telefono: String,
    val esEstudianteDuoc: Boolean,
    val recibirNewsletter: Boolean
)

data class LoginResponse(
    val id: String,
    val nombre: String,
    val email: String,
    val token: String,
    // Agregamos los que faltaban:
    val fechaNacimiento: String?, // Puede venir nulo, por seguridad ponle ?
    val telefono: String?,
    val esEstudianteDuoc: Boolean,
    val recibirNewsletter: Boolean
)

interface ApiService {
    @GET("api/productos")
    suspend fun getProductos(): List<Producto>

    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    // CAMBIO: Ahora usa RegisterRequest
    @POST("api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<LoginResponse>
}