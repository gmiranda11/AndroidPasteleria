package cl.pasteleriamilsabores.utils

import org.junit.Assert.*
import org.junit.Test

class ValidadoresTest {

    @Test
    fun emailValido_RetornaTrue() {
        val email = "alumno@duocuc.cl"
        assertTrue(Validadores.esEmailValido(email))
    }

    @Test
    fun emailInvalido_RetornaFalse() {
        val email = "correo-sin-arroba.com"
        assertFalse(Validadores.esEmailValido(email))
    }

    @Test
    fun passwordCorta_RetornaFalse() {
        val pass = "123"
        assertFalse(Validadores.esPasswordValida(pass))
    }

    @Test
    fun passwordsCoinciden_RetornaTrue() {
        assertTrue(Validadores.passwordsCoinciden("123456", "123456"))
    }
}