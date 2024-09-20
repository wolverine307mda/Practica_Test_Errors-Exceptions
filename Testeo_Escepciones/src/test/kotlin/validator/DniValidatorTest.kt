import exceptions.DniException
import org.example.validator.DniValidator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DniValidatorTest {

    private val validator = DniValidator()

    @Test
    fun dniValido() {
        val dni = "03177397Q"
        assertTrue(validator.validarDni(dni))
    }

    @Test
    fun validarDniConLetraIncorrecta() {
        val dniInvalido = "12345678A" // La letra correcta para 12345678 es "Z", no "A"

        val exception = assertThrows(DniException.DniLetraInvalida::class.java) {
            validator.validarDni(dniInvalido)
        }

        assertEquals("La letra del 12345678A no es válida.", exception.message) // Agregada el punto al final
    }

    @Test
    fun numValidoLetraInvalida() {
        val dni = "03177397R"  // Un DNI inválido (letra inválida)
        val exception = assertThrows(DniException.DniLetraInvalida::class.java) {
            validator.validarDni(dni)
        }

        assertEquals("La letra del 03177397R no es válida.", exception.message) // Agregada el punto al final
    }
/*
    @Test
    fun dniInvalido() {
        val dni = "03177397Q9"  // Un DNI inválido (demasiados caracteres)
        val exception = assertThrows(DniException.DniFormatoInvalido::class.java) {
            validator.validarDni(dni)
        }

        assertEquals("El formato del DNI 03177397Q9 es inválido.", exception.message) // Agregada el punto al final
    }

    @Test
    fun dniMuyLargo() {
        val dniInvalido = "12345678Z3"  // Un DNI con más caracteres de los permitidos
        val exception = assertThrows(DniException.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }

        assertEquals("El formato del DNI 12345678Z3 es inválido.", exception.message) // Agregada el punto al final
    }

    @Test
    fun inputQueNoSigueLaReglaRegex() {
        val dniInvalido = "1234567RZ"  // Formato incorrecto, menos números de los permitidos
        val exception = assertThrows(DniException.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }

        assertEquals("El formato del DNI 1234567RZ es inválido.", exception.message) // Agregada el punto al final
    }

    @Test
    fun dniNoSigueLaRegexYEsMuyLargo() {
        val dniInvalido = "1234567RZ3"  // Menos números y con más caracteres de los permitidos
        val exception = assertThrows(DniException.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }

        assertEquals("El formato del DNI 1234567RZ3 es inválido.", exception.message) // Agregada el punto al final
    }

    @Test
    fun dniNoEsNumerico() {
        val dniInvalido = "ABCDEF97Q"  // El formato no contiene números válidos
        val exception = assertThrows(DniException.DniFormatoInvalido::class.java) {
            validator.validarDni(dniInvalido)
        }

        assertEquals("El formato del DNI ABCDEF97Q es inválido.", exception.message) // Agregada el punto al final
    }*/
}
