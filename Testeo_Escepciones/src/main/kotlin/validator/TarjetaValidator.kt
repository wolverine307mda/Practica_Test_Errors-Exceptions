package validator

import exceptions.TarjetaException
import org.example.models.Tarjeta
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class TarjetaValidator {
    fun validarTarjeta(tarjeta: Tarjeta): Boolean {
        if (!validarNumeroTarjeta(tarjeta.numero)) {
            throw TarjetaException.TarjetaNumeroInvalido(tarjeta.numero)
        }
        if (!validarFechaCaducidad(tarjeta.fechaCaducidad)) {
            throw TarjetaException.FechaCaducidadInvalida(tarjeta.numero)
        }
        return true
    }

    fun validarNumeroTarjeta(numero: String): Boolean {
        val sanitizedNumero = numero.replace("\\s".toRegex(), "")
        if (!sanitizedNumero.matches(Regex("\\d{16}"))) return false

        return luhnCheck(sanitizedNumero)
    }

    private fun luhnCheck(numero: String): Boolean {
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

    fun validarFechaCaducidad(fechaCaducidad: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("MM/yy")
        return try {
            val parsedDate = YearMonth.parse(fechaCaducidad, formatter)
            !parsedDate.isBefore(YearMonth.now())
        } catch (e: Exception) {
            false
        }
    }
}