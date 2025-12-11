@file:OptIn(ExperimentalMaterial3Api::class)

package cl.pasteleriamilsabores.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.pasteleriamilsabores.data.Usuario
import cl.pasteleriamilsabores.navigation.Screen
import cl.pasteleriamilsabores.ui.components.BottomNavigationBar
import cl.pasteleriamilsabores.viewmodels.CarritoViewModel
import cl.pasteleriamilsabores.viewmodels.UserViewModel
import cl.pasteleriamilsabores.R
// Imports API
import cl.pasteleriamilsabores.data.Meal
import cl.pasteleriamilsabores.network.ExternalApiClient
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, carritoViewModel: CarritoViewModel) {
    val userViewModel: UserViewModel = viewModel()
    val usuarioActual by userViewModel.usuarioActual
    var mostrarDialogoPromociones by remember { mutableStateOf(false) }
    var mostrarMenuUsuario by remember { mutableStateOf(false) }

    // --- LÓGICA DE API EXTERNA (SOLO POSTRES) ---
    var listaPostres by remember { mutableStateOf<List<Meal>>(emptyList()) } // Guardamos la lista completa
    var postreActual by remember { mutableStateOf<Meal?>(null) }             // El que mostramos
    var cargando by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Función para obtener un postre random de la lista que ya bajamos
    val randomizarPostre = {
        if (listaPostres.isNotEmpty()) {
            postreActual = listaPostres.random()
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            cargando = true
            try {
                // 1. Bajamos TODOS los postres de una vez
                val response = ExternalApiClient.instance.getListaPostres()
                if (response.meals.isNotEmpty()) {
                    listaPostres = response.meals
                    // 2. Elegimos el primero al azar
                    randomizarPostre()
                }
            } catch (e: Exception) {
                // Manejo de error
            } finally {
                cargando = false
            }
        }
    }
    // ----------------------------------------------------

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
                            DropdownMenu(
                                expanded = mostrarMenuUsuario,
                                onDismissRequest = { mostrarMenuUsuario = false }
                            ) {
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
                                        userViewModel.mostrarMensajeInfo("Estamos trabajando en esta funcion.")
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
                .fillMaxSize()
        ) {
            ImageLogo()
            // 1. Header mejorado
            EnhancedWelcomeHeader(usuarioActual)

            // 2. Banner promocional simple
            SimplePromoBanner()

            // --- AQUÍ INSERTAMOS LA API EXTERNA (INICIO) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨ Inspiración: Postres del Mundo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        // Botón Randomizador
                        IconButton(onClick = { randomizarPostre() }) {
                            if (cargando) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Filled.Refresh, contentDescription = "Nuevo postre")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (postreActual != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Imagen de Internet
                            AsyncImage(
                                model = postreActual!!.imagenUrl,
                                contentDescription = postreActual!!.nombre,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = postreActual!!.nombre,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Categoría: Postre",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else if (!cargando) {
                        Text("No se pudo cargar la inspiración de hoy.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            // --- AQUÍ INSERTAMOS LA API EXTERNA (FIN) ---


            // 4. Grid de features mejorado
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // PRIMERA FILA
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EnhancedFeatureCard(
                        title = "Productos",
                        description = "Descubre nuestras delicias",
                        icon = Icons.Default.ShoppingCart,
                        onClick = { navController.navigate(Screen.Products.route) },
                        modifier = Modifier.weight(1f)
                    )
                    EnhancedFeatureCard(
                        title = "Blog",
                        description = "Tips y recetas",
                        icon = Icons.Default.Face,
                        onClick = { navController.navigate(Screen.Blog.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // SEGUNDA FILA
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EnhancedFeatureCard(
                        title = "Contacto",
                        description = "Escríbenos",
                        icon = Icons.Default.Email,
                        onClick = { navController.navigate(Screen.Contact.route) },
                        modifier = Modifier.weight(1f)
                    )
                    EnhancedFeatureCard(
                        title = if (usuarioActual != null) "Mi Perfil" else "Login",
                        description = if (usuarioActual != null) "Gestiona tu cuenta" else "Ingresa a tu cuenta",
                        icon = Icons.Default.Person,
                        onClick = {
                            if (usuarioActual != null) navController.navigate(Screen.Profile.route)
                            else navController.navigate(Screen.Login.route)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // TERCERA FILA
                if (usuarioActual == null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EnhancedFeatureCard(
                            title = "Registro",
                            description = "Crea tu cuenta",
                            icon = Icons.Default.Person,
                            onClick = {
                                navController.navigate(Screen.Register.route)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        EnhancedFeatureCard(
                            title = "Promociones",
                            description = "Ofertas especiales",
                            icon = Icons.Default.Star,
                            onClick = { mostrarDialogoPromociones = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        EnhancedFeatureCard(
                            title = "Promos",
                            description = "Ofertas especiales",
                            icon = Icons.Default.Star,
                            onClick = { mostrarDialogoPromociones = true },
                            modifier = Modifier.weight(2f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
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
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = R.drawable.buhflipexplode,
                            imageLoader = ImageLoader.Builder(LocalContext.current)
                                .components {
                                    add(GifDecoder.Factory())
                                }
                                .build()
                        ),
                        contentDescription = "Buh flip exploding",
                        modifier = Modifier.size(180.dp)
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

    // Snackbar
    if (userViewModel.infoMessage.value.isNotEmpty()) {
        LaunchedEffect(userViewModel.infoMessage.value) {
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

@Composable
fun CategoryChipWithWeight(
    name: String,
    emoji: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
@Composable
fun EnhancedFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con fondo
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }
        }
    }
}
@Composable
fun SimplePromoBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Promoción",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Promo Estudiante Duoc UC",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Torta gratis en tu cumpleaños",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
@Composable
fun EnhancedWelcomeHeader(usuarioActual: Usuario?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍰",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = if (usuarioActual != null) "¡Hola, ${usuarioActual.nombre.split(" ")[0]}!" else "¡Bienvenido!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Bienvenido a Pastelería Mil Sabores",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
@Composable
fun ImageLogo() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Tu imagen en resources
            contentDescription = "Logo Pastelería Mil Sabores",
            modifier = Modifier
                .size(120.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Fit
        )
    }
}