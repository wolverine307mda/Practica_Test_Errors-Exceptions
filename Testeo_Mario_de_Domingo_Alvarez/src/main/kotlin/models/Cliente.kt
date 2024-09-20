package org.example.models

import java.util.*

data class Cliente(
    val id: UUID,                         // Identificación única
    val nombre: String,                     // Nombre completo
    val dni: String,                           // DNI o NIF
    val cuentaBancaria: CuentaBancaria,     // Cuenta bancaria (IBAN)
    val tarjeta: Tarjeta                    // cliente.models.Tarjeta de crédito o débito
)