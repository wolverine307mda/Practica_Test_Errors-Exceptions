package error

sealed class DniError(override val message: String) : Exception(message) {
    class DniLetraInvalida(dni: String) : DniError("La letra del $dni no es valida")
    class DniFormatoInvalido(dni: String) : DniError("El formato del DNI '$dni' es incorrecto.")
}