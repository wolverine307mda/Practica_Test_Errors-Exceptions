package exceptions


sealed class ClienteException(override val message: String) : Exception(message) {
    class NombreInvalido(nombre: String) : ClienteException("El nombre '$nombre' no es válido, debe contener solo letras y tener más de dos caracteres.")
    class ClienteNoEncontrado(dni: String) : ClienteException("No se ha encontrado el cliente con DNI '$dni'.")
}

sealed class CuentaBancariaException(override val message: String) : Exception(message) {
    class SaldoIncorrecto(iban: String) : CuentaBancariaException("El saldo de la cuenta '$iban' es incorrecto.")
    class IbanFormatoIncorrecto(iban: String) : CuentaBancariaException("El formato del IBAN '$iban' es incorrecto.")
}

sealed class DniException(override val message: String) : Exception(message) {
    class DniLetraInvalida(dni: String) : DniException("La letra del $dni no es válida.")
    class DniFormatoInvalido(dni: String) : DniException("El formato del DNI '$dni' es incorrecto.")
}

sealed class TarjetaException(override val message: String) : Exception(message) {
    class TarjetaNumeroInvalido(numeroTarjeta: String) : TarjetaException("El número de tarjeta '$numeroTarjeta' no es válido.")
    class FechaCaducidadInvalida(fecha: String) : TarjetaException("La fecha de caducidad '$fecha' no es válida.")
}