package cl.pasteleriamilsabores.utils

object Validadores {
    fun esEmailValido(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
        return email.isNotEmpty() && emailRegex.matches(email)
    }

    fun esPasswordValida(password: String): Boolean {
        return password.length >= 6
    }

    fun passwordsCoinciden(pass1: String, pass2: String): Boolean {
        return pass1 == pass2
    }
}