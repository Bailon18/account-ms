package com.paucar.accountms.gestion;

import com.paucar.accountms.client.ClienteFeign;
import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.exception.ClienteNoEncontradoException;
import com.paucar.accountms.mapper.CuentaMapper;
import com.paucar.accountms.client.dto.Cliente;
import com.paucar.accountms.model.Cuenta;
import com.paucar.accountms.repository.CuentaRepository;
import com.paucar.accountms.service.gestion.CuentaGestionServiceImpl;
import com.paucar.accountms.util.ApiResponse;
import com.paucar.accountms.util.EstadoCuenta;
import com.paucar.accountms.util.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaGestionServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(CuentaGestionServiceImplTest.class);

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @Mock
    private ClienteFeign clienteFeign;

    @InjectMocks
    private CuentaGestionServiceImpl cuentaGestionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearCuenta_CuandoClienteExiste_DeberiaCrearCuentaExitosamente() {
        log.info("Iniciando prueba: crearCuenta_CuandoClienteExiste_DeberiaCrearCuentaExitosamente");

        CuentaDTO cuentaDTO = CuentaDTO.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        Cliente clienteSimulado = new Cliente();
        ApiResponse<Cliente> apiResponseCliente = ApiResponse.<Cliente>builder()
                .estado(200)
                .mensaje("Cliente encontrado")
                .datos(clienteSimulado)
                .build();

        when(clienteFeign.obtenerCliente(1L)).thenReturn(ResponseEntity.ok(apiResponseCliente));
        when(cuentaRepository.existsByNumeroCuenta("1234567890")).thenReturn(false);

        Cuenta cuentaEntidad = Cuenta.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaMapper.convertirDtoAEntidad(cuentaDTO)).thenReturn(cuentaEntidad);
        when(cuentaRepository.save(cuentaEntidad)).thenReturn(cuentaEntidad);
        when(cuentaMapper.convertEntidadADto(cuentaEntidad)).thenReturn(cuentaDTO);

        CuentaDTO resultado = cuentaGestionService.crearCuenta(cuentaDTO);

        log.info("Resultado obtenido para crear cuenta: {}", resultado);

        assertNotNull(resultado, "El resultado no debería ser nulo.");
        assertEquals(cuentaDTO.getNumeroCuenta(), resultado.getNumeroCuenta(), "El número de cuenta debería coincidir.");
        assertEquals(cuentaDTO.getSaldo(), resultado.getSaldo(), "El saldo debería coincidir.");
        assertEquals(cuentaDTO.getClienteId(), resultado.getClienteId(), "El ID del cliente debería coincidir.");

        verify(cuentaRepository, times(1)).save(cuentaEntidad);
    }

    @Test
    void crearCuenta_CuandoClienteNoExiste_DeberiaLanzarExcepcion() {
        log.info("Iniciando prueba: crearCuenta_CuandoClienteNoExiste_DeberiaLanzarExcepcion");

        CuentaDTO cuentaDTO = CuentaDTO.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(clienteFeign.obtenerCliente(1L)).thenReturn(ResponseEntity.ok(ApiResponse.<Cliente>builder().datos(null).build()));

        ClienteNoEncontradoException exception = assertThrows(ClienteNoEncontradoException.class, () -> cuentaGestionService.crearCuenta(cuentaDTO));

        log.error("Excepción lanzada: {}", exception.getMessage());
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void actualizarCuenta_CuandoCuentaExiste_DeberiaActualizarCorrectamente() {
        log.info("Iniciando prueba: actualizarCuenta_CuandoCuentaExiste_DeberiaActualizarCorrectamente");

        Cuenta cuentaExistente = Cuenta.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        CuentaDTO cuentaDTO = CuentaDTO.builder()
                .id(1L)
                .numeroCuenta("1234567890")
                .saldo(2000.0)
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .clienteId(1L)
                .estado(EstadoCuenta.ACTIVO)
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaExistente));
        when(cuentaRepository.save(cuentaExistente)).thenReturn(cuentaExistente);
        when(cuentaMapper.convertEntidadADto(cuentaExistente)).thenReturn(cuentaDTO);

        CuentaDTO resultado = cuentaGestionService.actualizarCuenta(1L, cuentaDTO);

        log.info("Resultado obtenido para actualizar cuenta: {}", resultado);

        assertNotNull(resultado);
        assertEquals(2000.0, resultado.getSaldo(), "El saldo debería ser 2000.0.");
        verify(cuentaRepository, times(1)).save(cuentaExistente);
    }
}
