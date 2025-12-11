package cl.pasteleriamilsabores.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import cl.pasteleriamilsabores.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Info, contentDescription = "Nosotros") },
            label = { Text("Nosotros") },
            selected = currentRoute == Screen.About.route,
            onClick = {
                if (currentRoute != Screen.About.route) {
                    navController.navigate(Screen.About.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Productos") },
            label = { Text("Productos") },
            selected = currentRoute == Screen.Products.route,
            onClick = {
                if (currentRoute != Screen.Products.route) {
                    navController.navigate(Screen.Products.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Edit, contentDescription = "Blog") },
            label = { Text("Blog") },
            selected = currentRoute == Screen.Blog.route,
            onClick = {
                if (currentRoute != Screen.Blog.route) {
                    navController.navigate(Screen.Blog.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Email, contentDescription = "Contacto") },
            label = { Text("Contacto") },
            selected = currentRoute == Screen.Contact.route,
            onClick = {
                if (currentRoute != Screen.Contact.route) {
                    navController.navigate(Screen.Contact.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            }
        )
    }
}