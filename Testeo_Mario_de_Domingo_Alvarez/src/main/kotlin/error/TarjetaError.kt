package error

sealed class TarjetaError(override val message: String) : Exception(message) {
    class TarjetaNumeroInvalido(numeroTarjeta: String) : TarjetaError("El número de tarjeta '$numeroTarjeta' no es válido.")
    class FechaCaducidadInvalida(fecha: String) : TarjetaError("La fecha de caducidad '$fecha' no es válida.")
}