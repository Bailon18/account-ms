package com.paucar.accountms.transaccion;

import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.exception.CuentaNoEncontradaException;
import com.paucar.accountms.exception.SaldoInsuficienteException;
import com.paucar.accountms.mapper.CuentaMapper;
import com.paucar.accountms.model.Cuenta;
import com.paucar.accountms.repository.CuentaRepository;
import com.paucar.accountms.service.transaccion.CuentaTransaccionServiceImpl;
import com.paucar.accountms.service.validacion.CuentaValidacionService;
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

class CuentaTransaccionServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(CuentaTransaccionServiceImplTest.class);

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @Mock
    private CuentaValidacionService cuentaValidacionService;

    @InjectMocks
    private CuentaTransaccionServiceImpl cuentaTransaccionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void depositar_CuandoCuentaExiste_DeberiaDepositarCorrectamente() {
        log.info("Iniciando prueba: depositar_CuandoCuentaExiste_DeberiaDepositarCorrectamente");

        String numeroCuenta = "1234567890";
        Double monto = 900.0;

        Cuenta cuentaExistente = Cuenta.builder()
                .id(1L)
                .numeroCuenta(numeroCuenta)
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(cuentaExistente));

        Cuenta cuentaConSaldoActualizado = Cuenta.builder()
                .id(1L)
                .numeroCuenta(numeroCuenta)
                .saldo(1500.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaConSaldoActualizado);

        CuentaDTO cuentaDTOEsperado = CuentaDTO.builder()
                .numeroCuenta(numeroCuenta)
                .saldo(1500.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .estado(EstadoCuenta.ACTIVO)
                .build();
        when(cuentaMapper.convertEntidadADto(cuentaConSaldoActualizado)).thenReturn(cuentaDTOEsperado);

        CuentaDTO resultado = cuentaTransaccionService.depositar(numeroCuenta, monto);

        log.info("Resultado obtenido para depósito: {}", resultado);

        assertNotNull(resultado);
        assertEquals(1500.0, resultado.getSaldo());
        assertEquals(numeroCuenta, resultado.getNumeroCuenta());
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }

    @Test
    void depositar_CuandoCuentaNoExiste_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: depositar_CuandoCuentaNoExiste_DeberiaLanzarExcepcion");

        String numeroCuenta = "1234567890";
        Double monto = 500.0;

        when(cuentaRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.empty());

        CuentaNoEncontradaException exception = assertThrows(CuentaNoEncontradaException.class, () -> cuentaTransaccionService.depositar(numeroCuenta, monto));

        log.error("Excepción lanzada: {}", exception.getMessage());
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void retirar_CuandoSaldoSuficiente_DeberiaRetirarCorrectamente() {
        log.info("Iniciando prueba: retirar_CuandoSaldoSuficiente_DeberiaRetirarCorrectamente");

        String numeroCuenta = "1234567890";
        Double monto = 500.0;

        Cuenta cuentaExistente = Cuenta.builder()
                .id(1L)
                .numeroCuenta(numeroCuenta)
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        Cuenta cuentaActualizada = Cuenta.builder()
                .id(1L)
                .numeroCuenta(numeroCuenta)
                .saldo(500.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(cuentaExistente));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaActualizada);
        when(cuentaMapper.convertEntidadADto(cuentaActualizada)).thenReturn(CuentaDTO.builder()
                .numeroCuenta(numeroCuenta)
                .saldo(500.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .estado(EstadoCuenta.ACTIVO)
                .build());

        CuentaDTO resultado = cuentaTransaccionService.retirar(numeroCuenta, monto);

        log.info("Resultado obtenido para retiro: {}", resultado);

        assertNotNull(resultado, "El resultado no debería ser nulo.");
        assertEquals(500.0, resultado.getSaldo());
        verify(cuentaRepository, times(1)).save(cuentaExistente);
    }

    @Test
    void retirar_CuandoSaldoInsuficiente_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: retirar_CuandoSaldoInsuficiente_DeberiaLanzarExcepcion");

        String numeroCuenta = "1234567890";
        Double monto = 1500.0;

        Cuenta cuentaExistente = Cuenta.builder()
                .id(1L)
                .numeroCuenta(numeroCuenta)
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaRepository.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(cuentaExistente));
        doThrow(new SaldoInsuficienteException("Saldo insuficiente")).when(cuentaValidacionService).validarSaldo(cuentaExistente, monto);

        SaldoInsuficienteException exception = assertThrows(SaldoInsuficienteException.class, () -> cuentaTransaccionService.retirar(numeroCuenta, monto));

        log.error("Excepción lanzada: {}", exception.getMessage());
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }
}
