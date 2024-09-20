import exceptions.CuentaBancariaException
import org.example.models.CuentaBancaria
import org.junit.jupiter.api.Test
import validator.CuentaBancariaValidator
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class CuentaBancariaValidatorTest {

    private val validator = CuentaBancariaValidator()

    @Test
    fun ibanValido() {
        val iban = "ES9121000418450200051332"
        assertTrue(validator.validarIban(iban))
    }

    @Test
    fun saldoValido() {
        val saldo = 1000.0
        assertTrue(validator.validarSaldo(saldo, "ES9121000418450200051332"))
    }

    @Test
    fun cuentaValida() {
        val cuenta = CuentaBancaria("ES9121000418450200051332", 1000.0)
        assertTrue(validator.validarCuenta(cuenta))
    }

    @Test
    fun ibanInvalido() {
        val cuenta = CuentaBancaria("ES9121000418450200051333", 1000.0)
        assertFailsWith<CuentaBancariaException.IbanFormatoIncorrecto> {
            validator.validarCuenta(cuenta)
        }
    }

    @Test
    fun ibanConCaracteresInvalidos() {
        val ibanInvalido = "es12-3456-abcd-efgh-ijklmn"
        assertFalse(validator.validarIban(ibanInvalido))
    }

    @Test
    fun saldoNegativo() {
        val cuenta = CuentaBancaria("ES9121000418450200051332", -1000.0)
        assertFailsWith<CuentaBancariaException.SaldoIncorrecto> {
            validator.validarCuenta(cuenta)
        }
    }

    @Test
    fun saldoCero() {
        val cuenta = CuentaBancaria("ES9121000418450200051332", 0.0)
        assertFailsWith<CuentaBancariaException.SaldoIncorrecto> {
            validator.validarSaldo(cuenta.saldo, cuenta.iban)
        }
    }

    @Test
    fun cuentaNula() {
        val cuenta = CuentaBancaria("", 0.0)
        assertFailsWith<CuentaBancariaException.IbanFormatoIncorrecto> {
            validator.validarCuenta(cuenta)
        }
    }

    @Test
    fun ibanNulo() {
        val cuenta = CuentaBancaria("", 1000.0)
        assertFailsWith<CuentaBancariaException.IbanFormatoIncorrecto> {
            validator.validarCuenta(cuenta)
        }
    }

    @Test
    fun ibanConLongitudIncorrecta() {
        val cuenta = CuentaBancaria("ES91210004184502000513", 1000.0)
        assertFalse(validator.validarIban(cuenta.iban))
    }

    @Test
    fun ibanConLongitudExcesiva() {
        val cuenta = CuentaBancaria("ES912100041845020005133212345678", 1000.0)
        assertFalse(validator.validarIban(cuenta.iban))
    }

    @Test
    fun ibanSinPrefijo() {
        val cuenta = CuentaBancaria("9121000418450200051332", 1000.0)
        assertFalse(validator.validarIban(cuenta.iban))
    }

    // Tests adicionales
    @Test
    fun ibanConEspacios() {
        val cuenta = CuentaBancaria("ES 91 2100 0418 4502 0005 1332", 1000.0)
        assertFalse(validator.validarIban(cuenta.iban))
    }

    @Test
    fun saldoValidoEnCero() {
        val cuenta = CuentaBancaria("ES9121000418450200051332", 0.01)
        assertTrue(validator.validarSaldo(cuenta.saldo, cuenta.iban))
    }

    @Test
    fun validarCuentaConIbanIncorrectoYSaldoValido() {
        val cuenta = CuentaBancaria("ES9121000418450200051234", 500.0)
        assertFailsWith<CuentaBancariaException.IbanFormatoIncorrecto> {
            validator.validarCuenta(cuenta)
        }
    }

    @Test
    fun validarCuentaConIbanValidoYSaldoNegativo() {
        val cuenta = CuentaBancaria("ES9121000418450200051332", -5.0)
        assertFailsWith<CuentaBancariaException.SaldoIncorrecto> {
            validator.validarCuenta(cuenta)
        }
    }

    @Test
    fun validarCuentaConIbanValidoYSaldoCero() {
        val cuenta = CuentaBancaria("ES9121000418450200051332", 0.0)
        assertFailsWith<CuentaBancariaException.SaldoIncorrecto> {
            validator.validarCuenta(cuenta)
        }
    }
}
