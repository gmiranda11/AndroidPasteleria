package cl.pasteleriamilsabores.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cl.pasteleriamilsabores.viewmodels.CarritoViewModel
import kotlinx.coroutines.delay

@Composable
fun ModalCarrito(
    carritoViewModel: CarritoViewModel,
    onClose: () -> Unit
) {
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var totalCompra by remember { mutableStateOf(0) }
    var procesandoCompra by remember { mutableStateOf(false) }

    // Efecto para procesar la compra
    LaunchedEffect(procesandoCompra) {
        if (procesandoCompra) {
            delay(2000) // Simular 2 segundos de procesamiento
            // Finalizar compra: limpiar carrito y mostrar confirmación
            carritoViewModel.vaciarCarrito()
            procesandoCompra = false
            mostrarConfirmacion = true
        }
    }

    // Efecto para ocultar la confirmación después de 2 segundos
    LaunchedEffect(mostrarConfirmacion) {
        if (mostrarConfirmacion) {
            delay(2000)
            mostrarConfirmacion = false
            onClose() // Cerrar el modal después de la confirmación
        }
    }

    Dialog(onDismissRequest = { if (!procesandoCompra) onClose() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tu Carrito de Compras",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { if (!procesandoCompra) onClose() },
                        enabled = !procesandoCompra
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar diferentes estados
                when {
                    procesandoCompra -> {
                        // ANIMACIÓN DE CARGA
                        ProcessingOrderAnimation(totalCompra)
                    }
                    mostrarConfirmacion -> {
                        // Mensaje de compra finalizada
                        OrderSuccessAnimation(totalCompra)
                    }
                    carritoViewModel.carritoItems.value.isEmpty() -> {
                        // Carrito vacío
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "El carrito está vacío",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    else -> {
                        // Lista de items del carrito
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(carritoViewModel.carritoItems.value) { item ->
                                ItemCarritoRow(
                                    item = item,
                                    onUpdateCantidad = { nuevaCantidad ->
                                        carritoViewModel.actualizarCantidad(item.producto.id, nuevaCantidad)
                                    },
                                    onUpdateMensaje = { nuevoMensaje ->
                                        carritoViewModel.actualizarMensajePersonalizado(item.producto.id, nuevoMensaje)
                                    },
                                    onEliminar = {
                                        carritoViewModel.eliminarDelCarrito(item.producto.id)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Total y acciones
                        Column {
                            Text(
                                text = "Total: $${carritoViewModel.getTotalCarrito()}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { carritoViewModel.vaciarCarrito() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ),
                                    enabled = carritoViewModel.carritoItems.value.isNotEmpty() && !procesandoCompra
                                ) {
                                    Text("Vaciar Carrito")
                                }

                                Button(
                                    onClick = {
                                        // Guardar el total ANTES de vaciar el carrito
                                        totalCompra = carritoViewModel.getTotalCarrito()
                                        // Iniciar animación de procesamiento
                                        procesandoCompra = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = carritoViewModel.carritoItems.value.isNotEmpty() && !procesandoCompra
                                ) {
                                    Text("Finalizar Compra")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ANIMACIÓN DE PROCESANDO COMPRA
@Composable
fun ProcessingOrderAnimation(total: Int) {
    var currentStep by remember { mutableStateOf(0) }
    val steps = listOf("⏳", "📦", "💰", "✅")
    val messages = listOf(
        "Procesando tu pedido...",
        "Preparando productos...",
        "Confirmando pago...",
        "¡Listo!"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(800)
            currentStep = (currentStep + 1) % steps.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Emoji animado que cambia
        Text(
            text = steps[currentStep],
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Texto que cambia
        Text(
            text = messages[currentStep],
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Total: $$total",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Indicador de puntos animados
        DotIndicator()
    }
}

@Composable
fun DotIndicator() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(0, 1, 2).forEach { index ->
            DotAnimation(delay = index * 200L)
        }
    }
}

@Composable
fun DotAnimation(delay: Long) {
    var scale by remember { mutableStateOf(0.5f) }

    LaunchedEffect(Unit) {
        delay(delay)
        while (true) {
            scale = 1.2f
            delay(600)
            scale = 0.5f
            delay(600)
        }
    }

    Box(
        modifier = Modifier
            .size(8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            )
    )
}

// ANIMACIÓN DE COMPRA EXITOSA
@Composable
fun OrderSuccessAnimation(total: Int) {
    var scale by remember { mutableStateOf(0.5f) }

    LaunchedEffect(Unit) {
        scale = 1.2f
        delay(200)
        scale = 1.0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono con animación de escala
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = "Compra exitosa",
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Texto con animación de fade in
        var opacity by remember { mutableStateOf(0f) }

        LaunchedEffect(Unit) {
            delay(300)
            opacity = 1f
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { alpha = opacity }
        ) {
            Text(
                text = "¡Compra Finalizada!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Total: $$total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary
            )
            Text(
                text = "Gracias por tu compra",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ItemCarritoRow(
    item: cl.pasteleriamilsabores.data.ItemCarrito,
    onUpdateCantidad: (Int) -> Unit,
    onUpdateMensaje: (String) -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarEditarMensaje by remember { mutableStateOf(false) }
    var mensajeTemporal by remember { mutableStateOf(item.mensajePersonalizado) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Información del producto
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.producto.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$${item.producto.precio} x ${item.cantidad} = $${item.producto.precio * item.cantidad}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onUpdateCantidad(item.cantidad - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Disminuir",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = item.cantidad.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { onUpdateCantidad(item.cantidad + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Aumentar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Botón eliminar
                    IconButton(
                        onClick = onEliminar,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Mensaje personalizado
            if (item.producto.personalizable) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mensaje: ${item.mensajePersonalizado.ifEmpty { "Sin mensaje" }}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { mostrarEditarMensaje = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Editar mensaje",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    // Diálogo para editar mensaje
    if (mostrarEditarMensaje) {
        AlertDialog(
            onDismissRequest = {
                mostrarEditarMensaje = false
                mensajeTemporal = item.mensajePersonalizado
            },
            title = {
                Text(
                    "Editar Mensaje",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                OutlinedTextField(
                    value = mensajeTemporal,
                    onValueChange = { mensajeTemporal = it },
                    label = {
                        Text(
                            "Mensaje personalizado",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Ingresa tu mensaje...") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateMensaje(mensajeTemporal)
                        mostrarEditarMensaje = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarEditarMensaje = false
                        mensajeTemporal = item.mensajePersonalizado
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}