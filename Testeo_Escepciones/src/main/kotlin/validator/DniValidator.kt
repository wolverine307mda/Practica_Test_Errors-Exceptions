package org.example.validator

import exceptions.DniException

class DniValidator {

    fun validarDni(dni: String): Boolean {
        val regex = "[0-9]{8}[A-Z]".toRegex()
        if (!dni.matches(regex)) {
            throw DniException.DniFormatoInvalido(dni)
        }

        val letras = listOf("T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X",
            "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E")

        val numero = dni.substring(0, 8).toIntOrNull() ?: throw DniException.DniFormatoInvalido(dni)
        val letra = dni.last()

        if (letras[numero % 23] != letra.toString()) {
            throw DniException.DniLetraInvalida(dni)
        }

        return true
    }
}