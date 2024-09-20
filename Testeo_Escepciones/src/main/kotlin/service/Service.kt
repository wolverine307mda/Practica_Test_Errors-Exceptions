package service

import exceptions.ClienteException
import org.example.models.Cliente

interface Service {
    @Throws(ClienteException::class)
    fun getClienteById(dni: String): Cliente

    @Throws(ClienteException::class)
    fun saveCliente(item: Cliente): Cliente

    @Throws(ClienteException::class)
    fun updateCliente(dni: String, item: Cliente): Cliente

    @Throws(ClienteException::class)
    fun deleteCliente(dni: String)
}
