package cl.pasteleriamilsabores.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.pasteleriamilsabores.navigation.Screen
import cl.pasteleriamilsabores.ui.components.BottomNavigationBar
import cl.pasteleriamilsabores.ui.components.CameraScreen
import cl.pasteleriamilsabores.utils.CameraPermissionHandler
import cl.pasteleriamilsabores.utils.rememberCameraPermissionState
import cl.pasteleriamilsabores.viewmodels.CameraViewModel
import cl.pasteleriamilsabores.viewmodels.UserViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val userViewModel: UserViewModel = viewModel()
    val cameraViewModel: CameraViewModel = viewModel()
    val usuarioActual by userViewModel.usuarioActual

    val cameraPermissionState = rememberCameraPermissionState()
    var showCamera by remember { mutableStateOf(false) }
    var pendingCameraRequest by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            cameraViewModel.selectImageFromGallery(it)
        }
    }



    // Observar cambios en el estado de permisos
    LaunchedEffect(cameraPermissionState.status) {
        if (pendingCameraRequest && cameraPermissionState.status.isGranted) {
            showCamera = true
            pendingCameraRequest = false
        }
    }

    // Si no hay usuario logueado, redirigir al login
    LaunchedEffect(usuarioActual) {
        if (usuarioActual == null) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
        }
    }

    // Mostrar cámara solo si showCamera es true
    if (showCamera) {
        CameraScreen(
            onImageCaptured = { uri ->
                cameraViewModel.captureImage(uri)
                showCamera = false
            },
            onCancel = {
                showCamera = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mi Cuenta",
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
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.Profile.route
            )
        }
    ) { innerPadding ->
        if (usuarioActual == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                ProfileHeader(
                    usuario = usuarioActual!!,
                    profileImageUri = cameraViewModel.capturedImage.value,
                    onEditPhoto = {showImageSourceDialog = true
                    }
                )

                PersonalInfoSection(usuario = usuarioActual!!)
                Spacer(modifier = Modifier.height(10.dp))
                ActionsSection(
                    onEditProfile = {
                        navController.navigate(Screen.EditProfile.route) {
                            launchSingleTop = true
                        }
                    },
                    onChangePassword = {
                        navController.navigate(Screen.ChangePassword.route) {
                            launchSingleTop = true
                        }
                    },
                    onLogout = {
                        cameraViewModel.clearImage()
                        userViewModel.cerrarSesion()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )

                if (usuarioActual!!.esEstudianteDuoc) {
                    StudentBenefitsSection()
                }
            }
        }
    }

    // Manejar el caso cuando los permisos son denegados permanentemente
    if (!cameraPermissionState.status.isGranted &&
        !cameraPermissionState.status.shouldShowRationale &&
        pendingCameraRequest) {
        AlertDialog(
            onDismissRequest = { pendingCameraRequest = false },
            title = { Text("Permiso de Cámara Requerido") },
            text = {
                Text("Para tomar fotos de perfil, necesitas habilitar el permiso de cámara en la configuración de la aplicación.")
            },
            confirmButton = {
                Button(onClick = { pendingCameraRequest = false }) {
                    Text("Entendido")
                }
            }
        )
    }
    if (showImageSourceDialog) {
        ImageSourceDialog(
            onDismiss = { showImageSourceDialog = false },
            onCameraSelected = {
                showImageSourceDialog = false
                // Verificar permisos antes de abrir cámara
                if (cameraPermissionState.status.isGranted) {
                    showCamera = true
                } else {
                    pendingCameraRequest = true
                    cameraPermissionState.launchPermissionRequest()
                }
            },
            onGallerySelected = {
                showImageSourceDialog = false
                galleryLauncher.launch("image/*")
            }
        )
    }
}

@Composable
fun ProfileHeader(
    usuario: cl.pasteleriamilsabores.data.Usuario,
    profileImageUri: Uri? = null,
    onEditPhoto: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar/Iniciales o Foto
            Box(
                modifier = Modifier
                    .size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    // Mostrar foto capturada
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.extraLarge),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Mostrar avatar con iniciales
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = usuario.nombre.split(" ").map { it.first() }.joinToString(""),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                // Botón para editar foto
                FloatingActionButton(
                    onClick = onEditPhoto,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Cambiar foto",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = usuario.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = usuario.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (usuario.esEstudianteDuoc) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🎓 Estudiante Duoc UC",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun PersonalInfoSection(usuario: cl.pasteleriamilsabores.data.Usuario) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Información Personal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            InfoRow(
                icon = Icons.Default.DateRange,
                title = "Fecha de Nacimiento",
                value = usuario.fechaNacimiento
            )

            InfoRow(
                icon = Icons.Default.Phone,
                title = "Teléfono",
                value = usuario.telefono.ifEmpty { "No especificado" }
            )

            InfoRow(
                icon = Icons.Default.Person,
                title = "Tipo de Cuenta",
                value = if (usuario.esEstudianteDuoc) "Estudiante Duoc UC" else "Cliente Regular"
            )
        }
    }
}

@Composable
fun PreferencesSection(usuario: cl.pasteleriamilsabores.data.Usuario,userViewModel: UserViewModel) {
    var actualizando by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Preferencias",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recibir Newsletter",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (actualizando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Switch(
                        checked = usuario.recibirNewsletter,
                        onCheckedChange = { nuevoEstado ->
                            actualizando = true
                            userViewModel.actualizarPreferenciaNewsletter(nuevoEstado)
                            actualizando = false
                        },
                        enabled = !actualizando
                    )
                }
            }

            Text(
                text = if (usuario.recibirNewsletter) "✅ Recibiendo newsletter"
                else "❌ Newsletter desactivado",
                style = MaterialTheme.typography.bodySmall,
                color = if (usuario.recibirNewsletter) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun ActionsSection(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Acciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ActionButton(
                icon = Icons.Default.Edit,
                text = "Editar Perfil",
                onClick = onEditProfile
            )

            ActionButton(
                icon = Icons.Default.Lock,
                text = "Cambiar Contraseña",
                onClick = onChangePassword
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun StudentBenefitsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Beneficios Estudiante",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Beneficios de Estudiante",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("• 🎂 Torta gratis en tu cumpleaños")
                Text("• 📚 Descuento especial en todos los productos")
                Text("• 🎁 Promociones exclusivas para estudiantes")
                Text("• ⭐ Prioridad en pedidos especiales")
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, contentDescription = text)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ArrowForward, contentDescription = "Ir")
        }
    }
}
@Composable
fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar imagen") },
        text = { Text("¿De dónde quieres tomar la foto de perfil?") },
        confirmButton = {
            Column {
                Button(
                    onClick = onCameraSelected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Face, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Usar Cámara")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onGallerySelected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Menu, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Desde Galería")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}