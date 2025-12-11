package cl.pasteleriamilsabores.network

import cl.pasteleriamilsabores.data.RecetaResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ExternalApiService {
    // CAMBIO: Usamos filter.php para pedir SOLO postres (Dessert)
    @GET("filter.php?c=Dessert")
    suspend fun getListaPostres(): RecetaResponse
}

object ExternalApiClient {
    // Asegúrate de que la URL termine en "/"
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val instance: ExternalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApiService::class.java)
    }
}