package com.paucar.accountms.controller;

import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.paucar.accountms.service.consulta.CuentaConsultaService;
import com.paucar.accountms.service.gestion.CuentaGestionService;
import com.paucar.accountms.service.transaccion.CuentaTransaccionService;


import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/cuentas")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaConsultaService cuentaConsultaService;
    private final CuentaGestionService cuentaGestionService;
    private final CuentaTransaccionService cuentaTransaccionService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<CuentaDTO>>> obtenerTodasLasCuentas() {
        List<CuentaDTO> cuentas = cuentaConsultaService.obtenerTodasLasCuentas();
        ApiResponse<List<CuentaDTO>> respuesta = ApiResponse.<List<CuentaDTO>>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Lista de cuentas recuperada exitosamente")
                .datos(cuentas)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> obtenerCuentaPorId(@PathVariable Long id) {
        Optional<CuentaDTO> cuenta = cuentaConsultaService.obtenerCuentaPorId(id);
        return cuenta.map(valor -> {
            ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                    .estado(HttpStatus.OK.value())
                    .mensaje("Cuenta encontrada con éxito")
                    .datos(valor)
                    .build();
            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        }).orElseGet(() -> {
            ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                    .estado(HttpStatus.NOT_FOUND.value())
                    .mensaje("Cuenta no encontrada con el ID: " + id)
                    .build();
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        });
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CuentaDTO>> crearCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO nuevaCuenta = cuentaGestionService.crearCuenta(cuentaDTO);
        ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                .estado(HttpStatus.CREATED.value())
                .mensaje("Cuenta creada exitosamente")
                .datos(nuevaCuenta)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> actualizarCuenta(@PathVariable Long id,
                                                                   @Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO cuentaActualizada = cuentaGestionService.actualizarCuenta(id, cuentaDTO);
        ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Cuenta actualizada exitosamente")
                .datos(cuentaActualizada)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<CuentaDTO>>> obtenerCuentasPorClienteId(@PathVariable Long clienteId) {
        List<CuentaDTO> cuentas = cuentaConsultaService.obtenerCuentasPorClienteId(clienteId);
        ApiResponse<List<CuentaDTO>> respuesta = ApiResponse.<List<CuentaDTO>>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Lista de cuentas del cliente recuperada exitosamente")
                .datos(cuentas)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCuenta(@PathVariable Long id) {
        cuentaGestionService.eliminarCuenta(id);
        ApiResponse<Void> respuesta = ApiResponse.<Void>builder()
                .estado(HttpStatus.NO_CONTENT.value())
                .mensaje("Cuenta eliminada exitosamente")
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/depositar")
    public ResponseEntity<ApiResponse<CuentaDTO>> depositar(@RequestParam String numeroCuenta,
                                                            @RequestParam Double monto) {
        CuentaDTO cuentaActualizada = cuentaTransaccionService.depositar(numeroCuenta, monto);
        ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Depósito realizado con éxito")
                .datos(cuentaActualizada)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @PutMapping("/retirar")
    public ResponseEntity<ApiResponse<CuentaDTO>> retirar(@RequestParam String numeroCuenta,
                                                          @RequestParam Double monto) {
        CuentaDTO cuentaActualizada = cuentaTransaccionService.retirar(numeroCuenta, monto);
        ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Retiro realizado con éxito")
                .datos(cuentaActualizada)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }


    @PutMapping("/realizar-transferencia")
    public ResponseEntity<ApiResponse<Boolean>> transferencia(@RequestParam String cuentaOrigen,
                                                              @RequestParam String cuentaDestino,
                                                              @RequestParam Double monto) {

        boolean estadoTransferencia = cuentaTransaccionService.transferir(cuentaOrigen, cuentaDestino, monto);

        ApiResponse<Boolean> respuesta;
        HttpStatus status;

        if (estadoTransferencia) {
            respuesta = ApiResponse.<Boolean>builder()
                    .estado(HttpStatus.OK.value())
                    .mensaje("Transferencia realizada con éxito")
                    .datos(true)
                    .build();
            status = HttpStatus.OK;
        } else {
            respuesta = ApiResponse.<Boolean>builder()
                    .estado(HttpStatus.BAD_REQUEST.value())
                    .mensaje("No se pudo realizar la transferencia")
                    .datos(false)
                    .build();
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(respuesta, status); // Usamos `status` en lugar de `HttpStatus.OK`
    }

}
