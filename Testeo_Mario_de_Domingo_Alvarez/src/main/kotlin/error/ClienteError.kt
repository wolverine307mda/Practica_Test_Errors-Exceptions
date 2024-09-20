package error


sealed class ClienteError(override val message: String) : Exception(message) {
    class NombreInvalido(nombre: String) : ClienteError("El nombre '$nombre' no es válido, debe contener solo letras y tener más de dos caracteres.")
    class ClienteNoEncontrado(dni: String) : ClienteError("No se ha encontrado el cliente con DNI '$dni'.")
}