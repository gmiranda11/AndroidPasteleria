package cl.pasteleriamilsabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cl.pasteleriamilsabores.data.UserRepository
import cl.pasteleriamilsabores.navigation.MainNavGraph
import cl.pasteleriamilsabores.ui.theme.PasteleriaMilSaboresTheme
import cl.pasteleriamilsabores.viewmodels.CarritoViewModel
import cl.pasteleriamilsabores.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserRepository.initialize(this)
        setContent {
            PasteleriaMilSaboresTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val carritoViewModel: CarritoViewModel = viewModel()
                    val userViewModel: UserViewModel = viewModel()

                    AppContent(
                        carritoViewModel = carritoViewModel,
                        userViewModel = userViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppContent(
    carritoViewModel: CarritoViewModel,
    userViewModel: UserViewModel
) {
    val navController = rememberNavController()
    val usuarioActual by userViewModel.usuarioActual

    // Observar cambios en el usuario y redirigir automáticamente
    LaunchedEffect(usuarioActual) {
        if (usuarioActual != null) {
            // Si hay usuario logueado, asegurarse de que está en Home
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    MainNavGraph(
        navController = navController,
        carritoViewModel = carritoViewModel,
        userViewModel = userViewModel
    )
}