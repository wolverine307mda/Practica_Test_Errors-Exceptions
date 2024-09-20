package validator

import exceptions.CuentaBancariaException
import org.example.models.CuentaBancaria
import java.math.BigInteger

class CuentaBancariaValidator {

    fun validarCuenta(cuenta: CuentaBancaria): Boolean {
        if (!validarIban(cuenta.iban)) {
            throw CuentaBancariaException.IbanFormatoIncorrecto(cuenta.iban)
        }
        validarSaldo(cuenta.saldo, cuenta.iban)
        return true
    }

    fun validarIban(iban: String): Boolean {
        if (!"^[0-9A-Z]*\$".toRegex().matches(iban)) {
            return false
        }

        val symbols = iban.trim()
        if (symbols.length != 24 || !symbols.startsWith("ES")) {
            return false
        }

        val cuentaSoloNumeros = symbols.substring(4) + "142800"
        val modo = BigInteger("97")
        val cuentaNumero = BigInteger(cuentaSoloNumeros)
        val resto = cuentaNumero.mod(modo).toInt()
        val dc = 98 - resto

        val cadenaDc = dc.toString().padStart(2, '0')

        return symbols.substring(2, 4) == cadenaDc
    }

    fun validarSaldo(saldo: Double, iban: String): Boolean {
        if (saldo <= 0.00) {
            throw CuentaBancariaException.SaldoIncorrecto(iban)
        }
        return true
    }
}