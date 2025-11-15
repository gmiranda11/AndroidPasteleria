package cl.pasteleriamilsabores.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.pasteleriamilsabores.navigation.Screen
import cl.pasteleriamilsabores.ui.components.BottomNavigationBar
import cl.pasteleriamilsabores.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val userViewModel: UserViewModel = viewModel()
    val usuarioActual by userViewModel.usuarioActual
    var mostrarDialogoPromociones by remember { mutableStateOf(false) }
    var mostrarMenuUsuario by remember { mutableStateOf(false) }
    var dropdownAnchor by remember { mutableStateOf<Offset?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Pastelería Mil Sabores",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    // Mostrar menú de usuario si está logueado
                    if (usuarioActual != null) {
                        Box {
                            IconButton(
                                onClick = { mostrarMenuUsuario = true }
                            ) {
                                Icon(
                                    Icons.Filled.AccountCircle,
                                    contentDescription = "Usuario",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            // El DropdownMenu se posiciona automáticamente respecto al IconButton
                            DropdownMenu(
                                expanded = mostrarMenuUsuario,
                                onDismissRequest = { mostrarMenuUsuario = false }
                            ) {
                                // Información del usuario
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                "Hola, ${usuarioActual?.nombre?.split(" ")?.first()}",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                usuarioActual?.email ?: "",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    },
                                    onClick = { mostrarMenuUsuario = false }
                                )
                                Divider()
                                DropdownMenuItem(
                                    text = { Text("Mi Perfil") },
                                    onClick = {
                                        mostrarMenuUsuario = false
                                        navController.navigate(Screen.Profile.route)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Mis Pedidos") },
                                    onClick = {
                                        mostrarMenuUsuario = false
                                    }
                                )
                                Divider()
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Cerrar Sesión",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        userViewModel.cerrarSesion()
                                        mostrarMenuUsuario = false
                                        // Mostrar mensaje de confirmación
                                        userViewModel.mostrarMensajeInfo("Sesión cerrada correctamente")
                                    }
                                )
                            }
                        }
                    } else {
                        IconButton(onClick = {
                            navController.navigate(Screen.Login.route)
                        }) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Iniciar Sesión",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
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
                currentRoute = Screen.Home.route
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Banner de bienvenida
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (usuarioActual != null) "¡Bienvenido de vuelta, ${usuarioActual?.nombre?.split(" ")?.first()}!" else "¡Bienvenido!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Celebrando 50 años de dulzura",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }



            // Grid de opciones - PRIMERA FILA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureCard(
                    title = "Productos",
                    description = "Descubre nuestras delicias",
                    onClick = { navController.navigate(Screen.Products.route) },
                    modifier = Modifier.weight(1f)
                )
                FeatureCard(
                    title = "Blog",
                    description = "Tips y recetas",
                    onClick = { navController.navigate(Screen.Blog.route) },
                    modifier = Modifier.weight(1f)
                )
            }

            // SEGUNDA FILA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureCard(
                    title = "Contacto",
                    description = "Escríbenos",
                    onClick = { navController.navigate(Screen.Contact.route) },
                    modifier = Modifier.weight(1f)
                )
                FeatureCard(
                    title = if (usuarioActual != null) "Mi Cuenta" else "Login",
                    description = if (usuarioActual != null) "Gestiona tu cuenta" else "Ingresa a tu cuenta",
                    onClick = {
                        if (usuarioActual != null) {
                            navController.navigate(Screen.Profile.route)
                        } else {
                            navController.navigate(Screen.Login.route)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // TERCERA FILA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureCard(
                    title = "Registro",
                    description = "Crea tu cuenta",
                    onClick = {
                        if (usuarioActual == null) {
                            navController.navigate(Screen.Register.route)
                        } else {
                            userViewModel.mostrarMensajeInfo("Ya tienes una sesión activa")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                FeatureCard(
                    title = "Promociones",
                    description = "Ofertas especiales",
                    onClick = { mostrarDialogoPromociones = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Diálogo de Promociones
    if (mostrarDialogoPromociones) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPromociones = false },
            title = {
                Text(
                    text = "Promociones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Promociones",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "No hay promociones disponibles en este momento",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "¡Vuelve pronto para descubrir nuestras ofertas especiales!",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoPromociones = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    // Snackbar para mensajes informativos
    if (userViewModel.infoMessage.value.isNotEmpty()) {
        LaunchedEffect(userViewModel.infoMessage.value) {
            // El mensaje se autoelimina después de 3 segundos
            kotlinx.coroutines.delay(3000)
            userViewModel.limpiarMensajeInfo()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Text(userViewModel.infoMessage.value)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Usamos un height fijo para que todas las tarjetas tengan el mismo tamaño
    val cardHeight = 120.dp

    Card(
        modifier = modifier
            .height(cardHeight), // ALTURA FIJA para uniformidad
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center, // Centrar verticalmente
            horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center // Texto centrado
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center, // Texto centrado
                maxLines = 2 // Máximo 2 líneas para uniformidad
            )
        }
    }
}