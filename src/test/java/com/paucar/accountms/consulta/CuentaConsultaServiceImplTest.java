package com.paucar.accountms.consulta;

import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.mapper.CuentaMapper;
import com.paucar.accountms.model.Cuenta;
import com.paucar.accountms.repository.CuentaRepository;
import com.paucar.accountms.service.consulta.CuentaConsultaServiceImpl;
import com.paucar.accountms.util.EstadoCuenta;
import com.paucar.accountms.util.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaConsultaServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(CuentaConsultaServiceImplTest.class);

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private CuentaConsultaServiceImpl cuentaConsultaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerTodasLasCuentas_DeberiaRetornarListaDeCuentas() {
        log.info("Iniciando prueba: obtenerTodasLasCuentas_DeberiaRetornarListaDeCuentas");

        List<Cuenta> cuentas = Arrays.asList(

                Cuenta.builder()
                        .id(1L)
                        .numeroCuenta("1234567890")
                        .saldo(1000.0)
                        .tipoCuenta(TipoCuenta.AHORROS)
                        .clienteId(1L)
                        .estado(EstadoCuenta.ACTIVO)
                        .build(),

                Cuenta.builder()
                        .id(2L)
                        .numeroCuenta("0987654321")
                        .saldo(5000.0)
                        .tipoCuenta(TipoCuenta.CORRIENTE)
                        .clienteId(1L)
                        .estado(EstadoCuenta.ACTIVO)
                        .build()
        );

        when(cuentaRepository.findAll()).thenReturn(cuentas);
        when(cuentaMapper.convertEntidadADto(cuentas.get(0)))
                .thenReturn(new CuentaDTO(1L, "1234567890", 1000.0, TipoCuenta.AHORROS, 1L, EstadoCuenta.ACTIVO));
        when(cuentaMapper.convertEntidadADto(cuentas.get(1)))
                .thenReturn(new CuentaDTO(2L, "0987654321", 5000.0, TipoCuenta.CORRIENTE, 1L, EstadoCuenta.ACTIVO));

        List<CuentaDTO> resultado = cuentaConsultaService.obtenerTodasLasCuentas();
        log.info("Resultado obtenido: {}", resultado);

        assertEquals(2, resultado.size(), "El tamaño de la lista debería ser 2.");
    }

    @Test
    void obtenerCuentaPorId_CuentaNoExiste_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: obtenerCuentaPorId_CuentaNoExiste_DeberiaLanzarExcepcion");

        when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<CuentaDTO> resultado = cuentaConsultaService.obtenerCuentaPorId(999L);

        log.info("Resultado de búsqueda por ID inexistente: {}", resultado);

        assertFalse(resultado.isPresent(), "El resultado debería ser vacío para un ID inexistente.");
    }

    @Test
    void obtenerCuentasPorClienteId_CuentasVacias_DeberiaRetornarListaVacia() {
        log.info("Iniciando prueba: obtenerCuentasPorClienteId_CuentasVacias_DeberiaRetornarListaVacia");

        when(cuentaRepository.findByClienteId(1L)).thenReturn(Collections.emptyList());
        List<CuentaDTO> resultado = cuentaConsultaService.obtenerCuentasPorClienteId(1L);

        log.info("Resultado de búsqueda de cuentas para cliente sin cuentas: {}", resultado);

        assertTrue(resultado.isEmpty(), "El resultado debería ser una lista vacía si el cliente no tiene cuentas.");
    }

    @Test
    void obtenerCuentaPorId_DeberiaRetornarCuenta() {
        log.info("Iniciando prueba: obtenerCuentaPorId_DeberiaRetornarCuenta");

        // Definir la cuenta simulada usando el patrón Builder.
        Cuenta cuenta = Cuenta.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        // Definir el DTO correspondiente usando el patrón Builder.
        CuentaDTO cuentaDTO = CuentaDTO.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaMapper.convertEntidadADto(cuenta)).thenReturn(cuentaDTO);

        // Llamar al método del servicio.
        Optional<CuentaDTO> resultado = cuentaConsultaService.obtenerCuentaPorId(1L);

        log.info("Resultado de búsqueda de cuenta por ID: {}", resultado);

        assertEquals(true, resultado.isPresent(), "La cuenta debería estar presente.");
        assertEquals("1234567890", resultado.get().getNumeroCuenta(), "El número de cuenta debería ser '1234567890'.");
    }

    @Test
    void obtenerCuentasPorClienteId_DeberiaRetornarListaDeCuentas() {
        log.info("Iniciando prueba: obtenerCuentasPorClienteId_DeberiaRetornarListaDeCuentas");

        // Definir cuentas simuladas usando el patrón Builder.
        List<Cuenta> cuentas = Arrays.asList(
                Cuenta.builder()
                        .id(1L)
                        .numeroCuenta("1234567890")
                        .saldo(1000.0)
                        .tipoCuenta(TipoCuenta.AHORROS)
                        .clienteId(1L)
                        .estado(EstadoCuenta.ACTIVO)
                        .build(),
                Cuenta.builder()
                        .id(2L)
                        .numeroCuenta("0987654321")
                        .saldo(5000.0)
                        .tipoCuenta(TipoCuenta.CORRIENTE)
                        .clienteId(1L)
                        .estado(EstadoCuenta.ACTIVO)
                        .build()
        );

        when(cuentaRepository.findByClienteId(1L)).thenReturn(cuentas);

        // Definir los DTOs correspondientes usando el patrón Builder.
        when(cuentaMapper.convertEntidadADto(cuentas.get(0)))
                .thenReturn(CuentaDTO.builder()
                        .id(1L)
                        .numeroCuenta("1234567890")
                        .saldo(1000.0)
                        .tipoCuenta(TipoCuenta.AHORROS)
                        .clienteId(1L)
                        .estado(EstadoCuenta.ACTIVO)
                        .build());
        when(cuentaMapper.convertEntidadADto(cuentas.get(1)))
                .thenReturn(CuentaDTO.builder()
                        .id(2L)
                        .numeroCuenta("0987654321")
                        .saldo(5000.0)
                        .tipoCuenta(TipoCuenta.CORRIENTE)
                        .clienteId(1L)
                        .estado(EstadoCuenta.ACTIVO)
                        .build());

        // Llamar al método a probar.
        List<CuentaDTO> resultado = cuentaConsultaService.obtenerCuentasPorClienteId(1L);

        log.info("Resultado de búsqueda de cuentas por cliente ID: {}", resultado);

        // Validar que el tamaño de la lista devuelta es el esperado.
        assertEquals(2, resultado.size(), "El tamaño de la lista debería ser 2.");
    }
}
