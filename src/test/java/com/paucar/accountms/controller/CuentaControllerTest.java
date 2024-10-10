package com.paucar.accountms.controller;

import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.service.consulta.CuentaConsultaService;
import com.paucar.accountms.service.gestion.CuentaGestionService;
import com.paucar.accountms.service.transaccion.CuentaTransaccionService;
import com.paucar.accountms.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CuentaControllerTest {

    private static final Logger log = LoggerFactory.getLogger(CuentaControllerTest.class);

    @Mock
    private CuentaConsultaService cuentaConsultaService;

    @Mock
    private CuentaGestionService cuentaGestionService;

    @Mock
    private CuentaTransaccionService cuentaTransaccionService;

    @InjectMocks
    private CuentaController cuentaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        log.info("Inicializando pruebas para CuentaController...");
    }

    @Test
    void obtenerTodasLasCuentas() {
        log.info("Prueba: obtenerTodasLasCuentas");
        List<CuentaDTO> listaCuentas = Arrays.asList(new CuentaDTO(), new CuentaDTO());
        when(cuentaConsultaService.obtenerTodasLasCuentas()).thenReturn(listaCuentas);

        ResponseEntity<ApiResponse<List<CuentaDTO>>> response = cuentaController.obtenerTodasLasCuentas();

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerCuentaPorId_CuandoExiste() {
        log.info("Prueba: obtenerCuentaPorId_CuandoExiste");
        CuentaDTO cuentaDTO = new CuentaDTO();
        Long id = 1L;
        when(cuentaConsultaService.obtenerCuentaPorId(id)).thenReturn(Optional.of(cuentaDTO));

        ResponseEntity<ApiResponse<CuentaDTO>> response = cuentaController.obtenerCuentaPorId(id);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerCuentaPorId_CuandoNoExiste() {
        log.info("Prueba: obtenerCuentaPorId_CuandoNoExiste");
        Long id = 1L;
        when(cuentaConsultaService.obtenerCuentaPorId(id)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<CuentaDTO>> response = cuentaController.obtenerCuentaPorId(id);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void crearCuenta() {
        log.info("Prueba: crearCuenta");
        CuentaDTO cuentaDTO = new CuentaDTO();
        cuentaDTO.setNumeroCuenta("1234565567");
        when(cuentaGestionService.crearCuenta(cuentaDTO)).thenReturn(cuentaDTO);

        ResponseEntity<ApiResponse<CuentaDTO>> response = cuentaController.crearCuenta(cuentaDTO);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void actualizarCuenta() {
        log.info("Prueba: actualizarCuenta");
        Long id = 1L;
        CuentaDTO cuentaDTO = new CuentaDTO();
        when(cuentaGestionService.actualizarCuenta(id, cuentaDTO)).thenReturn(cuentaDTO);

        ResponseEntity<ApiResponse<CuentaDTO>> response = cuentaController.actualizarCuenta(id, cuentaDTO);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void obtenerCuentasPorClienteId() {
        log.info("Prueba: obtenerCuentasPorClienteId");
        Long clienteId = 1L;
        List<CuentaDTO> listaCuentas = Arrays.asList(new CuentaDTO(), new CuentaDTO());
        when(cuentaConsultaService.obtenerCuentasPorClienteId(clienteId)).thenReturn(listaCuentas);

        ResponseEntity<ApiResponse<List<CuentaDTO>>> response = cuentaController.obtenerCuentasPorClienteId(clienteId);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eliminarCuenta() {
        log.info("Prueba: eliminarCuenta");
        Long id = 1L;
        doNothing().when(cuentaGestionService).eliminarCuenta(id);

        ResponseEntity<ApiResponse<Void>> response = cuentaController.eliminarCuenta(id);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void depositar() {
        log.info("Prueba: depositar");
        String numeroCuenta = "123456556";
        Double monto = 100.0;
        CuentaDTO cuentaDTO = new CuentaDTO();
        cuentaDTO.setNumeroCuenta(numeroCuenta);
        when(cuentaTransaccionService.depositar(numeroCuenta, monto)).thenReturn(cuentaDTO);

        ResponseEntity<ApiResponse<CuentaDTO>> response = cuentaController.depositar(numeroCuenta, monto);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void retirar() {
        log.info("Prueba: retirar");
        String numeroCuenta = "123456897";
        Double monto = 50.0;
        CuentaDTO cuentaDTO = new CuentaDTO();
        when(cuentaTransaccionService.retirar(numeroCuenta, monto)).thenReturn(cuentaDTO);

        ResponseEntity<ApiResponse<CuentaDTO>> response = cuentaController.retirar(numeroCuenta, monto);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void transferencia() {
        log.info("Prueba: transferencia");
        String cuentaOrigen = "1234567865";
        String cuentaDestino = "4565678907";
        Double monto = 100.0;
        when(cuentaTransaccionService.transferir(cuentaOrigen, cuentaDestino, monto)).thenReturn(true);

        ResponseEntity<ApiResponse<Boolean>> response = cuentaController.transferencia(cuentaOrigen, cuentaDestino, monto);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void transferencia_Falla() {
        log.info("Prueba: transferencia_Falla");
        String cuentaOrigen = "1233456745";
        String cuentaDestino = "4563458865";
        Double monto = 100.0;
        when(cuentaTransaccionService.transferir(cuentaOrigen, cuentaDestino, monto)).thenReturn(false);

        ResponseEntity<ApiResponse<Boolean>> response = cuentaController.transferencia(cuentaOrigen, cuentaDestino, monto);

        log.info("Resultado: HttpStatus = {}, Mensaje = {}", response.getStatusCode(), response.getBody().getMensaje());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
