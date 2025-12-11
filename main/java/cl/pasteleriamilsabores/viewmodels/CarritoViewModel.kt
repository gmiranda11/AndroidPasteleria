package cl.pasteleriamilsabores.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cl.pasteleriamilsabores.data.ItemCarrito
import cl.pasteleriamilsabores.data.Producto

class   CarritoViewModel : ViewModel() {
    val carritoItems = mutableStateOf(emptyList<ItemCarrito>())
    val mostrarCarrito = mutableStateOf(false)

    fun agregarAlCarrito(producto: Producto, mensajePersonalizado: String = "") {
        val itemsActuales = carritoItems.value.toMutableList()
        val itemExistente = itemsActuales.find { it.producto.id == producto.id }

        if (itemExistente != null) {
            itemExistente.cantidad++
            if (producto.personalizable) {
                itemExistente.mensajePersonalizado = mensajePersonalizado
            }
        } else {
            itemsActuales.add(
                ItemCarrito(
                    producto = producto,
                    cantidad = 1,
                    mensajePersonalizado = if (producto.personalizable) mensajePersonalizado else ""
                )
            )
        }

        carritoItems.value = itemsActuales
    }

    fun eliminarDelCarrito(productoId: String) {
        carritoItems.value = carritoItems.value.filter { it.producto.id != productoId }
    }

    fun actualizarCantidad(productoId: String, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarDelCarrito(productoId)
            return
        }

        carritoItems.value = carritoItems.value.map { item ->
            if (item.producto.id == productoId) {
                item.copy(cantidad = nuevaCantidad)
            } else {
                item
            }
        }
    }

    fun actualizarMensajePersonalizado(productoId: String, nuevoMensaje: String) {
        carritoItems.value = carritoItems.value.map { item ->
            if (item.producto.id == productoId) {
                item.copy(mensajePersonalizado = nuevoMensaje)
            } else {
                item
            }
        }
    }

    fun vaciarCarrito() {
        carritoItems.value = emptyList()
    }

    fun toggleCarrito() {
        mostrarCarrito.value = !mostrarCarrito.value
    }

    fun getTotalCarrito(): Int {
        return carritoItems.value.sumOf { it.producto.precio * it.cantidad }
    }

    fun getCantidadTotal(): Int {
        return carritoItems.value.sumOf { it.cantidad }
    }
}