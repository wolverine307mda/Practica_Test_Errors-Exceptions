package validator

import error.CuentaBancariaError
import org.example.models.CuentaBancaria
import java.math.BigInteger

class CuentaBancariaValidator {

    /**
     * Valida la cuenta bancaria en función de su IBAN y saldo.
     * 
     * @author Mario de Domingo
     * @param cuenta La cuenta bancaria a validar.
     * @return True si la cuenta es válida, false en caso contrario.
     */
    fun validarCuenta(cuenta: CuentaBancaria): Boolean {
        if (!validarIban(cuenta.iban)) {
            throw CuentaBancariaError.IbanFormatoIncorrecto(cuenta.iban)
        }
        validarSaldo(cuenta.saldo, cuenta.iban)
        return true
    }

    /**
     * Valida el formato del IBAN.
     *
     * @author Mario de Domingo
     * @param iban El IBAN a validar.
     * @return True si el IBAN es válido, false en caso contrario.
     */
fun validarIban(iban: String): Boolean {
    // Comprueba si el IBAN cumple con el patrón correcto (sólo contiene dígitos y letras mayúsculas).
    if (!"^[0-9A-Z]*\$".toRegex().matches(iban)) {
        return false
    }

    // Obtiene los símbolos del IBAN, elimina los espacios en blanco y comprueba si tiene 24 caracteres y empieza con "ES".
    val symbols = iban.trim()
    if (symbols.length != 24 || !symbols.startsWith("ES")) {
        return false
    }

    // Construye una cadena con los dígitos del IBAN (excluyendo los dos primeros caracteres) y "142800".
    val cuentaSoloNumeros = symbols.substring(4) + "142800"

    // Calcula el checksum del IBAN utilizando el algoritmo de verificación ISO 13616.
    val modo = BigInteger("97")
    val cuentaNumero = BigInteger(cuentaSoloNumeros)
    val resto = cuentaNumero.mod(modo).toInt()
    val dc = 98 - resto

    // Convierte el checksum en una cadena de dos dígitos y comprueba si coincide con los dos últimos caracteres del IBAN.
    val cadenaDc = dc.toString().padStart(2, '0')
    return symbols.substring(2, 4) == cadenaDc
}

    /**
     * Valida el saldo de la cuenta bancaria.
     *
     * @author Mario de Domingo
     * @param saldo El saldo a validar.
     * @param iban El IBAN de la cuenta.
     * @return True si el saldo es válido, false en caso contrario.
     */
    fun validarSaldo(saldo: Double, iban: String): Boolean {
        if (saldo <= 0.00) {
            throw CuentaBancariaError.SaldoIncorrecto(iban)
        }
        return true  // Asegúrate de devolver `true` si el saldo es válido.
    }
}
