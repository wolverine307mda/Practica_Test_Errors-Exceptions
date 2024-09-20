package error

sealed class CuentaBancariaError(override val message: String) : Exception(message) {
    class SaldoIncorrecto(iban: String) : CuentaBancariaError("El saldo de la cuenta '$iban' es incorrecto.")
    class IbanFormatoIncorrecto(iban: String) : CuentaBancariaError("El formato del IBAN '$iban' es incorrecto.")
}