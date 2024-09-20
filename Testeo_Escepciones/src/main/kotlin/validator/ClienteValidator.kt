package validator

import exceptions.ClienteException
import org.example.models.Cliente
import org.example.validator.DniValidator

class ClienteValidator(
    private val cuentaBancariaValidator: CuentaBancariaValidator = CuentaBancariaValidator(),
    private val dniValidator: DniValidator = DniValidator(),
    private val tarjetaValidator: TarjetaValidator = TarjetaValidator()
) {

    fun validarCliente(cliente: Cliente): Boolean {
        validarNombre(cliente.nombre)
        dniValidator.validarDni(cliente.dni)
        cuentaBancariaValidator.validarCuenta(cliente.cuentaBancaria)
        tarjetaValidator.validarTarjeta(cliente.tarjeta)
        return true
    }

    fun validarNombre(nombre: String): Boolean {
        val regex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚÑñ\\s]{2,}$")
        if (!regex.matches(nombre.trim())) {
            throw ClienteException.NombreInvalido(nombre)
        }
        return true
    }
}