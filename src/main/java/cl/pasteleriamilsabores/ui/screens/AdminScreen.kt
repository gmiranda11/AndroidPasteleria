@file:OptIn(ExperimentalMaterial3Api::class)

package cl.pasteleriamilsabores.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Panel de Administración",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Productos") },
                    label = { Text("Productos") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Pedidos") },
                    label = { Text("Pedidos") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // Información del admin
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Admin",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Administrador",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "admin@pasteleriamilsabores.cl",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Estadísticas
            Text(
                text = "Resumen General",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Pedidos",
                    value = "42",
                    subtitle = "+5 desde la semana pasada",
                    icon = Icons.Default.MailOutline,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Ingresos",
                    value = "$1.2M",
                    subtitle = "+15% desde el mes pasado",
                    icon = Icons.Default.Info,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Usuarios",
                    value = "156",
                    subtitle = "+12 este mes",
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Productos",
                    value = "16",
                    subtitle = "Todos disponibles",
                    icon = Icons.Default.Check,
                    modifier = Modifier.weight(1f)
                )
            }

            // Pedidos recientes
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pedidos Recientes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de pedidos
                    PedidoItem(
                        id = "#00125",
                        cliente = "María González",
                        productos = "Torta Chocolate (1)",
                        total = "$45.000",
                        estado = "En preparación"
                    )

                    PedidoItem(
                        id = "#00124",
                        cliente = "Carlos Méndez",
                        productos = "Tiramisú (2), Cupcakes (6)",
                        total = "$68.000",
                        estado = "Entregado"
                    )

                    PedidoItem(
                        id = "#00123",
                        cliente = "Ana Silva",
                        productos = "Torta 3 Leches (1), Galletas (12)",
                        total = "$52.500",
                        estado = "Pendiente"
                    )
                }
            }

            // Acciones rápidas
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionCard(
                    title = "Agregar Producto",
                    icon = Icons.Default.Add,
                    onClick = { /* Agregar producto */ },
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    title = "Ver Reportes",
                    icon = Icons.Default.List,
                    onClick = { /* Ver reportes */ },
                    modifier = Modifier.weight(1f)
                )
            }

            // Formulario para agregar producto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Agregar Nuevo Producto",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Formulario simplificado
                    var nombreProducto by remember { mutableStateOf("") }
                    var precioProducto by remember { mutableStateOf("") }
                    var categoriaProducto by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = nombreProducto,
                        onValueChange = { nombreProducto = it },
                        label = { Text("Nombre del Producto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = precioProducto,
                        onValueChange = { precioProducto = it },
                        label = { Text("Precio") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    var expanded by remember { mutableStateOf(false) }
                    val categorias = listOf("Tortas", "Postres", "Galletas", "Cupcakes", "Otros")

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = categoriaProducto,
                            onValueChange = { categoriaProducto = it },
                            label = { Text("Categoría") },
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
                                        categoriaProducto = categoria
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Lógica para agregar producto
                            nombreProducto = ""
                            precioProducto = ""
                            categoriaProducto = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = nombreProducto.isNotEmpty() && precioProducto.isNotEmpty() && categoriaProducto.isNotEmpty()
                    ) {
                        Text("Agregar Producto")
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Icon(
                    icon,
                    contentDescription = title,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun PedidoItem(
    id: String,
    cliente: String,
    productos: String,
    total: String,
    estado: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = id,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(text = cliente, style = MaterialTheme.typography.bodySmall)
                Text(text = productos, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = total,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = estado,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (estado) {
                        "Entregado" -> MaterialTheme.colorScheme.primary
                        "En preparación" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    IconButton(onClick = { /* Ver detalles */ }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Ver", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { /* Completar pedido */ }) {
                        Icon(Icons.Default.Check, contentDescription = "Completar", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}