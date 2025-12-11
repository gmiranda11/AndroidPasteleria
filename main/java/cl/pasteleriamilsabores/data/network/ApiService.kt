package cl.pasteleriamilsabores.network

import cl.pasteleriamilsabores.data.Producto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Modelos simples para Login (los usaremos luego, pero déjalos listos)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val id: String, val nombre: String, val email: String, val token: String)

interface ApiService {
    // Obtener lista de productos
    @GET("api/productos")
    suspend fun getProductos(): List<Producto>

    // Login (Lo usaremos en el siguiente paso)
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}