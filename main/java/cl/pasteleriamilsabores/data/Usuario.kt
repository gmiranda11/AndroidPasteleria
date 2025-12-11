package cl.pasteleriamilsabores.data

data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val password: String,
    val fechaNacimiento: String,
    val telefono: String,
    val esEstudianteDuoc: Boolean = false,
    val recibirNewsletter: Boolean = true
)