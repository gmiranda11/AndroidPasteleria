package cl.pasteleriamilsabores.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cl.pasteleriamilsabores.ui.screens.*
import cl.pasteleriamilsabores.viewmodels.CarritoViewModel
import cl.pasteleriamilsabores.viewmodels.UserViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
    carritoViewModel: CarritoViewModel,
    userViewModel: UserViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Products.route) {
            ProductsScreen(navController = navController)
        }
        composable(Screen.ProductDetail.route) {
            ProductDetailScreen(navController = navController)
        }
        composable(Screen.About.route) {
            AboutScreen(navController = navController)
        }
        composable(Screen.Blog.route) {
            BlogScreen(navController = navController)
        }
        composable(Screen.BlogDetail.route) {
            BlogDetailScreen(navController = navController)
        }
        composable(Screen.Contact.route) {
            ContactScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            // Proteger pantalla de login - redirigir si ya está logueado
            LoginScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }
        composable(Screen.Register.route) {
            // Proteger pantalla de registro - redirigir si ya está logueado
            RegisterScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }
        composable(Screen.Admin.route) {
            AdminScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}