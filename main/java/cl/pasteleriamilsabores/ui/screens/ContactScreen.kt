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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.pasteleriamilsabores.navigation.Screen
import cl.pasteleriamilsabores.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var asunto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Contáctanos",
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
                currentRoute = Screen.Contact.route
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // Header
            Text(
                text = "Estamos aquí para ayudarte",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Escríbenos y te responderemos a la brevedad",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // Información de contacto
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ContactInfoCard(
                    icon = Icons.Default.LocationOn,
                    title = "Dirección",
                    content = "Av. Dulce 123\nSantiago, Chile",
                    modifier = Modifier.weight(1f)
                )
                ContactInfoCard(
                    icon = Icons.Default.Phone,
                    title = "Teléfono",
                    content = "+56 2 2345 6789\nLun-Sáb: 9:00 - 20:00",
                    modifier = Modifier.weight(1f)
                )
                ContactInfoCard(
                    icon = Icons.Default.Email,
                    title = "Email",
                    content = "info@pasteleriamilsabores.cl\npedidos@pasteleriamilsabores.cl",
                    modifier = Modifier.weight(1f)
                )
            }

            // Formulario de contacto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Envíanos un Mensaje",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )

                    var expanded by remember { mutableStateOf(false) }
                    val asuntos = listOf(
                        "Consulta general",
                        "Pedido especial",
                        "Reclamo o queja",
                        "Sugerencia",
                        "Otros"
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = asunto,
                            onValueChange = { asunto = it },
                            label = { Text("Asunto *") },
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
                            asuntos.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        asunto = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = mensaje,
                        onValueChange = { mensaje = it },
                        label = { Text("Mensaje *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        singleLine = false,
                        maxLines = 5
                    )

                    Button(
                        onClick = {
                            // Enviar mensaje
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = nombre.isNotEmpty() && email.isNotEmpty() &&
                                asunto.isNotEmpty() && mensaje.isNotEmpty()
                    ) {
                        Text("Enviar Mensaje")
                    }
                }
            }

            // Redes sociales
            Text(
                text = "También puedes encontrarnos en:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* Facebook */ }) {
                    Text("Facebook")
                }
                TextButton(onClick = { /* Instagram */ }) {
                    Text("Instagram")
                }
                TextButton(onClick = { /* Twitter */ }) {
                    Text("Twitter")
                }
            }
        }
    }
}

@Composable
fun ContactInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}