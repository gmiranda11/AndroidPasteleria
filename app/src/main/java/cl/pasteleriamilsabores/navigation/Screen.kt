package cl.pasteleriamilsabores.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Products : Screen("products")
    object ProductDetail : Screen("product_detail")
    object About : Screen("about")
    object Blog : Screen("blog")
    object BlogDetail : Screen("blog_detail")
    object Contact : Screen("contact")
    object Login : Screen("login")
    object Register : Screen("register")
    object Admin : Screen("admin")
    object Profile : Screen("profile")
}