package validator

import error.ClienteError
import error.DniError
import org.example.validator.DniValidator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DniValidatorTest {

    private val validator = DniValidator()

    @Test
    fun dniValido() {
        val dni = "03177397Q"
        assertTrue(validator.validarDni(dni))
    }

    @Test
    fun validarDniConLetraIncorrecta() {
        // Un DNI con número válido pero letra incorrecta
        val dniInvalido = "12345678A" // La letra correcta para 12345678 es "Z", no "A"

        val exception = assertThrows(DniError.DniLetraInvalida::class.java) {
            validator.validarDni(dniInvalido)
        }

        assertEquals("La letra del 12345678A no es valida", exception.message)
    }

    @Test
    fun numValidoLetraInvalida() {
        val dni = "03177397R"  // Un DNI inválido (letra inválida)
        assertThrows(DniError.DniLetraInvalida::class.java) {
            validator.validarDni(dni)
        }
    }

    @Test
    fun dniInvalido() {
        val dni = "03177397Q9"  // Un DNI inválido (demasiados caracteres)
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dni)
        }
    }

    @Test
    fun dniMuyLargo() {
        val dniInvalido = "12345678Z3"
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }    }

    @Test
    fun inputQueNoSigueLaReglaRegex() {
        val dniInvalido = "1234567RZ"
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }    }

    @Test
    fun dniNoSigueLaRegexYEsMuyLargo() {
        val dniInvalido = "1234567RZ3"
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }    }

    @Test
    fun dniNoEsNumerico() {
        val dniInvalido = "03177397Q9"
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }
    }

    @Test
    fun dniValidoConLetrasMinúsculas() {
        val dni = "03177397q"  // DNI válido con letra minúscula
        assertTrue(validator.validarDni(dni.toUpperCase())) // Asegura que se trate correctamente
    }

    @Test
    fun dniConCaracteresEspeciales() {
        val dniInvalido = "03177397@Q"  // DNI con un carácter especial
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }
    }

    @Test
    fun dniVacio() {
        val dniInvalido = ""  // DNI vacío
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }
    }

    @Test
    fun dniNulo() {
        val dniInvalido: String? = null  // DNI nulo
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido ?: "")
        }
    }

    @Test
    fun dniIncorrectoPorFormato() {
        val dniInvalido = "1234A678"  // DNI con formato incorrecto
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }
    }

    @Test
    fun dniConSoloLetras() {
        val dniInvalido = "ABCDEFGHI"  // DNI compuesto solo por letras
        assertThrows(DniError.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }
    }

    @Test
    fun dniConLargoCorrectoPeroLetraIncorrecta() {
        val dniInvalido = "03177397X"  // DNI válido en longitud pero letra incorrecta
        assertThrows(DniError.DniLetraInvalida::class.java) {
            validator.validarDni(dniInvalido)
        }
    }

}