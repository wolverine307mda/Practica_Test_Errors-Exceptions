package org.example.repository

import org.example.models.Cliente
import repository.Repository
import java.util.concurrent.ConcurrentHashMap

class RepositoryImpl : Repository {
    private val clientes = ConcurrentHashMap<String, Cliente>()

    override fun findById(dni: String): Cliente? {
        return clientes[dni]
    }

    override fun save(item: Cliente): Cliente {
        clientes[item.dni] = item
        return item
    }

    override fun update(dni: String, item: Cliente): Cliente? {
        if (clientes.containsKey(dni)) {
            clientes[dni] = item
            return item
        }
        return null
    }

    override fun delete(dni: String): Cliente? {
        return clientes.remove(dni)
    }
}
