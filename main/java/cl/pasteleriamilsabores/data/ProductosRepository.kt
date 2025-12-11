package cl.pasteleriamilsabores.data

import cl.pasteleriamilsabores.network.RetrofitClient
import android.util.Log

class ProductosRepository {

    // Ahora esta función va a buscar los datos reales al Backend
    suspend fun getProductos(): List<Producto> {
        return try {
            val respuesta = RetrofitClient.instance.getProductos()
            respuesta
        } catch (e: Exception) {
            Log.e("ProductosRepo", "Error de conexión: ${e.message}")
            emptyList()
        }
    }

    // Función para filtrar en el cliente (opcional, ya lo hace la pantalla)
    suspend fun getProductosPorCategoria(categoria: String): List<Producto> {
        val todos = getProductos()
        return if (categoria == "Todos") todos else todos.filter { it.categoria == categoria }
    }

    // Buscar por ID (reutilizando la lista para no llamar de nuevo a la API por ahora)
    suspend fun getProductoById(id: String): Producto? {
        val todos = getProductos()
        return todos.find { it.id == id }
    }

    // Método estático auxiliar para categorías (hardcodeado por ahora para el filtro)
    companion object {
        fun getCategorias(): List<String> {
            return listOf("Todos", "Tortas", "Pasteles", "Postres", "Cóctel")
        }
    }
}