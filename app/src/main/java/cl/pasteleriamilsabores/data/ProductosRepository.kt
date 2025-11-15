package cl.pasteleriamilsabores.data

object ProductosRepository {
    val productos = listOf(
        Producto(
            id = "TC001",
            nombre = "Torta Cuadrada de Chocolate",
            precio = 45000,
            categoria = "Tortas Cuadradas",
            descripcion = "Deliciosa torta de chocolate con capas de ganache y un toque de avellanas. Personalizable con mensajes especiales.",
            personalizable = true
        ),
        Producto(
            id = "TC002",
            nombre = "Torta Cuadrada de Frutas",
            precio = 50000,
            categoria = "Tortas Cuadradas",
            descripcion = "Una mezcla de frutas frescas y crema chantilly sobre un suave bizcocho de vainilla, ideal para celebraciones."
        ),
        Producto(
            id = "TT001",
            nombre = "Torta Circular de Vainilla",
            precio = 40000,
            categoria = "Tortas Circulares",
            descripcion = "Bizcocho de vainilla clásico relleno con crema pastelera y cubierto con un glaseado dulce, perfecto para cualquier ocasión."
        ),
        Producto(
            id = "TT002",
            nombre = "Torta Circular de Manjar",
            precio = 42000,
            categoria = "Tortas Circulares",
            descripcion = "Torta tradicional chilena con manjar y nueces, un deleite para los amantes de los sabores dulces y clásicos."
        ),
        Producto(
            id = "PI001",
            nombre = "Mousse de Chocolate",
            precio = 5000,
            categoria = "Postres Individuales",
            descripcion = "Postre individual cremoso y suave, hecho con chocolate de alta calidad, ideal para los amantes del chocolate."
        ),
        Producto(
            id = "PI002",
            nombre = "Tiramisú Clásico",
            precio = 5500,
            categoria = "Postres Individuales",
            descripcion = "Un postre italiano individual con capas de café, mascarpone y cacao, perfecto para finalizar cualquier comida."
        ),
        Producto(
            id = "PSA001",
            nombre = "Torta Sin Azúcar de Naranja",
            precio = 48000,
            categoria = "Productos Sin Azúcar",
            descripcion = "Torta ligera y deliciosa, endulzada naturalmente, ideal para quienes buscan opciones más saludables."
        ),
        Producto(
            id = "PSA002",
            nombre = "Cheesecake Sin Azúcar",
            precio = 47000,
            categoria = "Productos Sin Azúcar",
            descripcion = "Suave y cremoso, este cheesecake es una opción perfecta para disfrutar sin culpa."
        )
    )

    fun getProductosPorCategoria(categoria: String? = null): List<Producto> {
        return if (categoria == null || categoria == "Todos") {
            productos
        } else {
            productos.filter { it.categoria == categoria }
        }
    }

    fun getCategorias(): List<String> {
        return listOf("Todos") + productos.map { it.categoria }.distinct()
    }

    fun getProductoPorId(id: String): Producto? {
        return productos.find { it.id == id }
    }
}