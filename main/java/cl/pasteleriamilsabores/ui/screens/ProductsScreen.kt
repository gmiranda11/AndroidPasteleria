package cl.pasteleriamilsabores.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.pasteleriamilsabores.data.Producto
import cl.pasteleriamilsabores.data.ProductosRepository
import cl.pasteleriamilsabores.navigation.Screen
import cl.pasteleriamilsabores.ui.components.BottomNavigationBar
import cl.pasteleriamilsabores.viewmodels.CarritoViewModel
import cl.pasteleriamilsabores.ui.components.ModalCarrito
import androidx.compose.ui.platform.LocalContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(navController: NavController, carritoViewModel: CarritoViewModel) {
    // Estado para la lista de productos y categorías
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    // Repositorio instanciado (idealmente debería venir inyectado o usar un Singleton)
    val repository = remember { ProductosRepository() }

    // Cargar productos desde la API al iniciar la pantalla
    LaunchedEffect(Unit) {
        try {
            productos = repository.getProductos()
        } catch (e: Exception) {
            // Manejar error de conexión aquí si es necesario
            productos = emptyList()
        } finally {
            cargando = false
        }
    }

    // Lógica de filtrado en el cliente
    val productosFiltrados = if (categoriaSeleccionada == "Todos") {
        productos
    } else {
        productos.filter { it.categoria == categoriaSeleccionada }
    }

    // Obtener categorías únicas dinámicamente
    val categorias = listOf("Todos") + productos.map { it.categoria }.distinct()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Nuestros Productos",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.Products.route
            )
        },
        floatingActionButton = {
            BadgedBox(
                badge = {
                    if (carritoViewModel.getCantidadTotal() > 0) {
                        Badge {
                            Text(carritoViewModel.getCantidadTotal().toString())
                        }
                    }
                }
            ) {
                FloatingActionButton(
                    onClick = { carritoViewModel.toggleCarrito() },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Selector de categorías
                    var expanded by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = categoriaSeleccionada,
                                onValueChange = { },
                                label = { Text("Filtrar por categoría") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                categorias.forEach { categoria ->
                                    DropdownMenuItem(
                                        text = { Text(categoria) },
                                        onClick = {
                                            categoriaSeleccionada = categoria
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Lista de productos
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(productosFiltrados) { producto ->
                            ProductoItem(
                                producto = producto,
                                onProductClick = {
                                    navController.navigate(Screen.ProductDetail.createRoute(producto.id))
                                },
                                onAddToCart = { mensaje ->
                                    carritoViewModel.agregarAlCarrito(producto, mensaje)
                                },
                                carritoViewModel = carritoViewModel
                            )
                        }
                    }
                }
            }

            // Modal del carrito
            if (carritoViewModel.mostrarCarrito.value) {
                ModalCarrito(
                    carritoViewModel = carritoViewModel,
                    onClose = { carritoViewModel.toggleCarrito() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoItem(
    producto: Producto,
    onProductClick: () -> Unit,
    onAddToCart: (String) -> Unit,
    carritoViewModel: CarritoViewModel
) {
    var mostrarDialogoPersonalizacion by remember { mutableStateOf(false) }
    var mensajePersonalizado by remember { mutableStateOf("") }

    // Usamos la función auxiliar para convertir el String imagen al ID de recurso
    val imagenId = obtenerIdImagen(producto.imagen)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onProductClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen producto
            if (imagenId != 0) {
                Image(
                    painter = painterResource(id = imagenId),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🍰", style = MaterialTheme.typography.headlineSmall)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = {
                    if (producto.personalizable) {
                        mostrarDialogoPersonalizacion = true
                    } else {
                        onAddToCart("")
                    }
                }
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Agregar al carrito")
            }
        }
    }

    if (mostrarDialogoPersonalizacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPersonalizacion = false },
            title = { Text("Personalizar ${producto.nombre}") },
            text = {
                Column {
                    Text("Este producto es personalizable. Ingresa el mensaje que deseas incluir:")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = mensajePersonalizado,
                        onValueChange = { mensajePersonalizado = it },
                        label = { Text("Mensaje personalizado") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ej: Feliz Cumpleaños") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddToCart(mensajePersonalizado)
                        mostrarDialogoPersonalizacion = false
                        mensajePersonalizado = ""
                    }
                ) {
                    Text("Agregar al Carrito")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoPersonalizacion = false
                    mensajePersonalizado = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Función auxiliar para buscar imágenes por nombre (String) en la carpeta drawable
@Composable
fun obtenerIdImagen(nombreImagen: String): Int {
    val context = LocalContext.current
    // Si el nombre viene vacío o nulo, retornar logo o 0
    if (nombreImagen.isEmpty()) return cl.pasteleriamilsabores.R.drawable.logo

    val id = context.resources.getIdentifier(
        nombreImagen,
        "drawable",
        context.packageName
    )
    return if (id != 0) id else cl.pasteleriamilsabores.R.drawable.logo
}