package cl.pasteleriamilsabores.data

data class Producto(
    val id: String,
    val nombre: String,
    val precio: Int,
    val categoria: String,
    val descripcion: String = "",
    val imagen: String = "",
    val personalizable: Boolean = false
)

data class ItemCarrito(
    val producto: Producto,
    var cantidad: Int = 1,
    var mensajePersonalizado: String = ""
)