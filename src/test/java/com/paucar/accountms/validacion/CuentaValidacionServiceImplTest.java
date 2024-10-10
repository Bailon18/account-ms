package com.paucar.accountms.validacion;

import com.paucar.accountms.exception.CuentaNoEncontradaException;
import com.paucar.accountms.exception.SaldoInsuficienteException;
import com.paucar.accountms.model.Cuenta;
import com.paucar.accountms.repository.CuentaRepository;
import com.paucar.accountms.service.validacion.CuentaValidacionServiceImpl;
import com.paucar.accountms.util.EstadoCuenta;
import com.paucar.accountms.util.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaValidacionServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(CuentaValidacionServiceImplTest.class);

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private CuentaValidacionServiceImpl cuentaValidacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validarEstadoActivo_CuandoCuentaNoActiva_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarEstadoActivo_CuandoCuentaNoActiva_DeberiaLanzarExcepcion");

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta("1234567890")
                .estado(EstadoCuenta.INACTIVO)
                .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cuentaValidacionService.validarEstadoActivo(cuenta));

        log.error("Excepción lanzada: {}", exception.getMessage());
        assertEquals("No se pueden realizar transacciones en cuentas que no estén ACTIVAS.", exception.getMessage());
    }

    @Test
    void validarSaldoAhorros_CuandoSaldoInsuficiente_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarSaldoAhorros_CuandoSaldoInsuficiente_DeberiaLanzarExcepcion");

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta("1234567890")
                .saldo(100.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .build();

        SaldoInsuficienteException exception = assertThrows(SaldoInsuficienteException.class, () -> cuentaValidacionService.validarSaldo(cuenta, 200.0));

        log.error("Excepción lanzada: {}", exception.getMessage());
        assertEquals("Saldo insuficiente en la cuenta de ahorros.", exception.getMessage());
    }

    @Test
    void validarSaldoCorriente_CuandoSobrepasaSobregiro_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarSaldoCorriente_CuandoSobrepasaSobregiro_DeberiaLanzarExcepcion");

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta("1234567890")
                .saldo(-400.0)
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .build();

        SaldoInsuficienteException exception = assertThrows(SaldoInsuficienteException.class, () -> cuentaValidacionService.validarSaldo(cuenta, 200.0));

        log.error("Excepción lanzada: {}", exception.getMessage());
        assertEquals("Límite de sobregiro alcanzado en la cuenta corriente.", exception.getMessage());
    }

    @Test
    void validarMonto_CuandoMontoInvalido_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarMonto_CuandoMontoInvalido_DeberiaLanzarExcepcion");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cuentaValidacionService.validarMonto(-1.0));

        log.error("Excepción lanzada: {}", exception.getMessage());
        assertEquals("El monto debe ser mayor que 0.", exception.getMessage());
    }

    @Test
    void validarCuenta_CuandoCuentaNoExiste_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarCuenta_CuandoCuentaNoExiste_DeberiaLanzarExcepcion");

        when(cuentaRepository.findByNumeroCuenta("1234567890")).thenReturn(Optional.empty());

        CuentaNoEncontradaException exception = assertThrows(CuentaNoEncontradaException.class, () -> cuentaValidacionService.validarCuenta("1234567890", 100.0, "origen"));

        log.error("Excepción lanzada: {}", exception.getMessage());
        assertEquals("No se pudo realizar la transferencia: la cuenta de origen con el número [1234567890] no fue encontrada.", exception.getMessage());
    }

    @Test
    void validarCuenta_CuandoCuentaExistePeroInactiva_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarCuenta_CuandoCuentaExistePeroInactiva_DeberiaLanzarExcepcion");

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta("1234567890")
                .estado(EstadoCuenta.INACTIVO)
                .build();

        when(cuentaRepository.findByNumeroCuenta("1234567890")).thenReturn(Optional.of(cuenta));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cuentaValidacionService.validarCuenta("1234567890", 100.0, "origen"));

        log.error("Excepción lanzada: {}", exception.getMessage());
        assertEquals("No se pudo realizar la transferencia: la cuenta de origen con el número [1234567890] está INACTIVA.", exception.getMessage());
    }

    @Test
    void validarCuenta_CuandoCuentaOrigenSaldoInsuficiente_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: validarCuenta_CuandoCuentaOrigenSaldoInsuficiente_DeberiaLanzarExcepcion");

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta("1234567890")
                .saldo(50.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaRepository.findByNumeroCuenta("1234567890")).thenReturn(Optional.of(cuenta));

        SaldoInsuficienteException exception = assertThrows(SaldoInsuficienteException.class, () -> cuentaValidacionService.validarCuenta("1234567890", 100.0, "origen"));

        log.error("Excepción lanzada: {}", exception.getMessage());
        assertEquals("No se pudo realizar la transferencia: saldo insuficiente en la cuenta de origen [1234567890].", exception.getMessage());
    }

    @Test
    void validarCuenta_CuandoTodoEsCorrecto_DeberiaRetornarCuenta() {
        log.info("Iniciando prueba: validarCuenta_CuandoTodoEsCorrecto_DeberiaRetornarCuenta");

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta("1234567890")
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaRepository.findByNumeroCuenta("1234567890")).thenReturn(Optional.of(cuenta));

        Cuenta resultado = cuentaValidacionService.validarCuenta("1234567890", 500.0, "origen");

        log.info("Resultado de la validación de cuenta: {}", resultado);

        assertNotNull(resultado);
        assertEquals("1234567890", resultado.getNumeroCuenta());
    }
}
