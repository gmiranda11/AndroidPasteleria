import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.pasteleriamilsabores.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var cambiando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cambiar Contraseña", fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }

                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary)
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
                        Icon(Icons.Default.Warning, contentDescription = "Error")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(userViewModel.errorMessage.value)
                    }
                }
            }

            Text(
                "Seguridad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Contraseña actual
            OutlinedTextField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    userViewModel.limpiarError()
                },
                label = { Text("Contraseña actual *") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(
                            if (currentPasswordVisible) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = "Mostrar contraseña"
                        )
                    }
                }
            )

            // Nueva contraseña
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    userViewModel.limpiarError()
                },
                label = { Text("Nueva contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(
                            if (newPasswordVisible) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = "Mostrar contraseña"
                        )
                    }
                }
            )

            // Confirmar nueva contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    userViewModel.limpiarError()
                },
                label = { Text("Confirmar nueva contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = "Mostrar contraseña"
                        )
                    }
                }
            )

            // Requisitos de contraseña
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "La contraseña debe contener:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("• Mínimo 6 caracteres")
                }
            }

            Button(
                onClick = {
                    cambiando = true
                    val exito = userViewModel.cambiarPassword(
                        passwordActual = currentPassword,
                        nuevaPassword = newPassword,
                        confirmarPassword = confirmPassword
                    )
                    if (exito) {
                        navController.popBackStack()
                    }
                    cambiando = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = currentPassword.isNotEmpty() &&
                        newPassword.isNotEmpty() &&
                        confirmPassword.isNotEmpty() &&
                        !cambiando
            ) {
                if (cambiando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Cambiar Contraseña")
                }
            }
        }
    }
}