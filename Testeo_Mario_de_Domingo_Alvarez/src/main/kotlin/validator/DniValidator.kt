package org.example.validator

import error.ClienteError
import error.DniError

/**
 * Clase que valida un DNI.
 *
 * @author Mario de Domingo
 * @param dni El DNI a validar.
 * @return True si el DNI es válido, false en caso contrario.
 */
class DniValidator {

    /**
     * Valida un DNI en función de su formato y letra.
     *
     * @author Mario de Domingo
     * @param dni El DNI a validar.
     * @return True si el DNI es válido, false en caso contrario.
     */
    fun validarDni(dni: String): Boolean {
        val regex = "[0-9]{8}[A-Z]".toRegex() // Asegúrate de que el regex sea correcto.
        if (!dni.matches(regex)) {
            throw DniError.DniFormatoInvalido(dni)
        }

        val letras = listOf("T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X",
            "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E")

        val numero = dni.substring(0, 8).toIntOrNull() ?: throw DniError.DniFormatoInvalido(dni)
        val letra = dni.last()

        if (letras[numero % 23] != letra.toString()) {
            throw DniError.DniLetraInvalida(dni)
        }

        return true
    }
}