package cl.pasteleriamilsabores.viewmodels

import cl.pasteleriamilsabores.data.Producto
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CarritoViewModelTest {

    private lateinit var viewModel: CarritoViewModel

    @Before
    fun setup() {
        viewModel = CarritoViewModel()
    }

    @Test
    fun agregarProducto_AumentaCantidadTotal() {
        val producto = Producto("1", "Torta", 10000, "Tortas", "", "", false)

        viewModel.agregarAlCarrito(producto)

        assertEquals(1, viewModel.getCantidadTotal())
    }

    @Test
    fun calcularTotal_SumaCorrecta() {
        val p1 = Producto("1", "Torta", 10000, "Tortas", "", "", false)
        val p2 = Producto("2", "Pie", 5000, "Postres", "", "", false)

        viewModel.agregarAlCarrito(p1) // 10.000
        viewModel.agregarAlCarrito(p2) // 5.000
        viewModel.agregarAlCarrito(p2) // 5.000 (Cantidad 2)

        // Total esperado: 20.000
        assertEquals(20000, viewModel.getTotalCarrito())
    }

    @Test
    fun vaciarCarrito_DejaListaVacia() {
        val p1 = Producto("1", "Torta", 10000, "Tortas", "", "", false)
        viewModel.agregarAlCarrito(p1)

        viewModel.vaciarCarrito()

        assertEquals(0, viewModel.getCantidadTotal())
    }
}