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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.pasteleriamilsabores.navigation.Screen
import cl.pasteleriamilsabores.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, userViewModel: UserViewModel) {
    val userViewModel: UserViewModel = viewModel()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var codigoPromocional by remember { mutableStateOf("") }
    var terminos by remember { mutableStateOf(false) }
    var newsletter by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    val esEstudianteDuoc = email.contains("@duoc.cl") || email.contains("@duocuc.cl")
    val usuarioActual by userViewModel.usuarioActual

    LaunchedEffect(usuarioActual) {
        if (usuarioActual != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Crear Cuenta",
                        fontWeight = FontWeight.Bold
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Únete a nuestra comunidad",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Mostrar errores
            if (userViewModel.errorMessage.value.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = userViewModel.errorMessage.value,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Formulario
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        userViewModel.limpiarError()
                    },
                    label = { Text("Nombre completo *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = userViewModel.errorMessage.value.isNotEmpty()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { nuevoValor ->
                        email = nuevoValor
                        emailError = nuevoValor.isNotEmpty() && !validarEmail(nuevoValor)
                        userViewModel.limpiarError()
                    },
                    label = { Text("Email *") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    isError = emailError,
                    supportingText = {
                        if (emailError) {
                            Text("Formato de email inválido", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = { fechaNacimiento = it },
                    label = { Text("Fecha de nacimiento *") },
                    placeholder = { Text("DD/MM/AAAA") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = "Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { nuevoValor ->
                        password = nuevoValor
                        passwordError = nuevoValor.isNotEmpty() && !validarLongitudPassword(nuevoValor)
                        userViewModel.limpiarError()
                    },
                    label = { Text("Contraseña *") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Done else Icons.Default.Clear,
                                contentDescription = "Visibilidad contraseña"
                            )
                        }
                    },
                    isError = passwordError,
                    supportingText = {
                        if (passwordError) {
                            Text("Mínimo 6 caracteres", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { nuevoValor ->
                        confirmPassword = nuevoValor
                        confirmPasswordError = nuevoValor.isNotEmpty() && !validarCoincidenciaPassword(password, nuevoValor)
                        userViewModel.limpiarError()
                    },
                    label = { Text("Confirmar contraseña *") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.Done else Icons.Default.Clear,
                                contentDescription = "Visibilidad contraseña"
                            )
                        }
                    },
                    isError = confirmPasswordError,
                    supportingText = {
                        if (confirmPasswordError) {
                            Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                OutlinedTextField(
                    value = codigoPromocional,
                    onValueChange = { codigoPromocional = it },
                    label = { Text("Código promocional (opcional)") },
                    placeholder = { Text("Ej: FELICES50") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Checkboxes
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = terminos,
                        onCheckedChange = { terminos = it }
                    )
                    Text("Acepto los términos y condiciones *")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = newsletter,
                        onCheckedChange = { newsletter = it }
                    )
                    Text("Deseo recibir newsletter y promociones")
                }
            }

            // Información de estudiante Duoc
            if (esEstudianteDuoc) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = "Estudiante Duoc",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Estudiante Duoc detectado! Recibirás torta gratis en tu cumpleaños",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Botón de registro
            Button(
                onClick = {
                    // Validar antes de registrar
                    var hayErrores = false

                    // Validar email
                    if (!validarEmail(email)) {
                        emailError = true
                        hayErrores = true
                    }

                    // Validar contraseña
                    if (!validarLongitudPassword(password)) {
                        passwordError = true
                        hayErrores = true
                    }

                    // Validar coincidencia
                    if (!validarCoincidenciaPassword(password, confirmPassword)) {
                        confirmPasswordError = true
                        hayErrores = true
                    }

                    if (!hayErrores && terminos) {
                        // NUEVA FORMA: Llamada asíncrona con callback
                        userViewModel.registrarUsuario(
                            nombre = nombre,
                            email = email,
                            password = password,
                            fechaNacimiento = fechaNacimiento,
                            telefono = telefono,
                            esEstudianteDuoc = esEstudianteDuoc,
                            recibirNewsletter = newsletter,
                            onSuccess = {
                                // Esto se ejecuta si el registro funcionó
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    } else if (!terminos) {
                        userViewModel.errorMessage.value = "Debes aceptar los términos y condiciones"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = nombre.isNotEmpty() && email.isNotEmpty() && fechaNacimiento.isNotEmpty() &&
                        password.isNotEmpty() && confirmPassword.isNotEmpty() && terminos &&
                        !userViewModel.isLoading.value
            ) {
                if (userViewModel.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Crear Cuenta", style = MaterialTheme.typography.titleMedium)
                }
            }

            // Beneficios
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Beneficios exclusivos",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("• 50% de descuento si eres mayor de 50 años")
                        Text("• 10% de descuento permanente con código FELICES50")
                        Text("• Torta gratis en tu cumpleaños si eres estudiante Duoc")
                    }
                }
            }
        }
    }
}
private fun validarEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
    return emailRegex.matches(email)
}

private fun validarCoincidenciaPassword(password: String, confirmPassword: String): Boolean {
    return password == confirmPassword
}

private fun validarLongitudPassword(password: String): Boolean {
    return password.length >= 6
}