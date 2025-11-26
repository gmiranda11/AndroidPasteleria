package cl.pasteleriamilsabores.navigation

import ChangePasswordScreen
import EditProfileScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
            HomeScreen(navController = navController, carritoViewModel = carritoViewModel)
        }
        composable(Screen.Products.route) {
            ProductsScreen(navController = navController, carritoViewModel = carritoViewModel)
        }
        composable(Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            ProductDetailScreen(navController = navController, carritoViewModel = carritoViewModel)
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
        composable(Screen.EditProfile.route) {
            val usuarioActual = userViewModel.usuarioActual.value
            if (usuarioActual != null) {
                EditProfileScreen(
                    navController = navController,
                    userViewModel = userViewModel,
                    usuarioActual = usuarioActual
                )
            }
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }
    }
}