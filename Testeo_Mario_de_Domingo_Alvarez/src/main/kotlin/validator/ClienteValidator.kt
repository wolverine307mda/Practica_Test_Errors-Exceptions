package validator

import error.ClienteError
import org.example.models.Cliente
import org.example.validator.DniValidator
import org.example.validator.TarjetaValidator

/**
 * Clase que valida un cliente en función de su nombre, DNI, cuenta bancaria y tarjeta.
 *
 * @author Mario de Domingo
 * @param cuentaBancariaValidator Validador de cuentas bancarias.
 * @param dniValidator Validador de DNIs.
 * @param tarjetaValidator Validador de tarjetas.
 */
class ClienteValidator(
    private val cuentaBancariaValidator: CuentaBancariaValidator = CuentaBancariaValidator(),
    private val dniValidator: DniValidator = DniValidator(),
    private val tarjetaValidator: TarjetaValidator = TarjetaValidator()
) {

    /**
     * Valida un cliente en función de su nombre, DNI, cuenta bancaria y tarjeta.
     *
     * @author Mario de Domingo
     * @param cliente El cliente a validar.
     * @return True si el cliente es válido, false en caso contrario.
     */
    fun validarCliente(cliente: Cliente): Boolean {
        validarNombre(cliente.nombre)
        dniValidator.validarDni(cliente.dni)
        cuentaBancariaValidator.validarCuenta(cliente.cuentaBancaria)
        tarjetaValidator.validarTarjeta(cliente.tarjeta)
        return true
    }

    /**
     * Valida el nombre del cliente.
     *
     * @author Mario de Domingo
     * @param nombre El nombre del cliente a validar.
     * @return True si el nombre es válido, false en caso contrario.
     */
    fun validarNombre(nombre: String): Boolean {
        val regex = Regex("^[a-zA-ZáéíóúÁÉÍÓÚÑñ\\s]{2,}$")
        if (!regex.matches(nombre.trim())) {
            throw ClienteError.NombreInvalido(nombre)
        }
        return true
    }
}
