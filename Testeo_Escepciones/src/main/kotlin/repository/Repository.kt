package repository

import org.example.models.Cliente

interface Repository {
    fun findById(dni: String): Cliente?
    fun save(item: Cliente): Cliente
    fun update(dni: String, item: Cliente): Cliente?
    fun delete(dni: String): Cliente?
}