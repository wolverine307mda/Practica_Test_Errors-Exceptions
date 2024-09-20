package org.example.validator

import error.TarjetaError
import org.example.models.Tarjeta
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class TarjetaValidator {

    /**
     * Valida una tarjeta en función de su número y fecha de caducidad.
     * @param tarjeta La tarjeta a validar.
     * @return True si la tarjeta es válida, false en caso contrario.
     */
    fun validarTarjeta(tarjeta: Tarjeta): Boolean {
        if (!validarNumeroTarjeta(tarjeta.numero)) {
            throw TarjetaError.TarjetaNumeroInvalido(tarjeta.numero)
        }
        if (!validarFechaCaducidad(tarjeta.fechaCaducidad)) {
            throw TarjetaError.FechaCaducidadInvalida(tarjeta.numero)
        }
        return true
    }

    /**
     * Valida el número de una tarjeta.
     * @param numero El número de la tarjeta a validar.
     * @return True si el número de tarjeta es válido, false en caso contrario.
     */
    fun validarNumeroTarjeta(numero: String): Boolean {
        val sanitizedNumero = numero.replace("\\s".toRegex(), "")
        if (!sanitizedNumero.matches(Regex("\\d{16}"))) return false

        return ValidarNumero(sanitizedNumero)
    }

    /**
     * Valida la fecha de caducidad de una tarjeta.
     * @param fechaCaducidad La fecha de caducidad de la tarjeta a validar.
     * @return True si la fecha de caducidad es válida, false en caso contrario.
     */
    fun validarFechaCaducidad(fechaCaducidad: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("MM/yy")
        return try {
            val parsedDate = YearMonth.parse(fechaCaducidad, formatter)
            !parsedDate.isBefore(YearMonth.now())
        } catch (e: Exception) {
            false
        }
    }

    private fun ValidarNumero(numero: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in numero.length - 1 downTo 0) {
            var n = Character.getNumericValue(numero[i])
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }
}