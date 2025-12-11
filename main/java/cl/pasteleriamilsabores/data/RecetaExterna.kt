package cl.pasteleriamilsabores.data

import com.google.gson.annotations.SerializedName

// La API devuelve una lista llamada "meals"
data class RecetaResponse(
    val meals: List<Meal>
)

// Cada comida tiene estos datos (mapeamos solo lo que nos sirve)
data class Meal(
    @SerializedName("strMeal") val nombre: String,
    @SerializedName("strMealThumb") val imagenUrl: String,
    @SerializedName("strCategory") val categoria: String,
    @SerializedName("strArea") val origen: String
)