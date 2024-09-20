package org.example.service

import com.github.michaelbull.result.Result
import error.ClienteError
import org.example.models.Cliente

interface Service {
    fun getClienteById(dni: String): Result<Cliente, ClienteError>
    fun saveCliente(item: Cliente): Result<Cliente, ClienteError>
    fun updateCliente(dni: String, item: Cliente): Result<Cliente, ClienteError>
    fun deleteCliente(dni: String): Result<Cliente, ClienteError>
}