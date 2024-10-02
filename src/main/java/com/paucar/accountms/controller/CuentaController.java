package com.paucar.accountms.controller;

import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.service.CuentaService;
import com.paucar.accountms.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cuentas")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CuentaDTO>>> obtenerTodasLasCuentas() {
        List<CuentaDTO> cuentas = cuentaService.obtenerTodasLasCuentas();
        ApiResponse<List<CuentaDTO>> respuesta = ApiResponse.<List<CuentaDTO>>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Lista de cuentas recuperada exitosamente")
                .datos(cuentas)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> obtenerCuentaPorId(@PathVariable Long id) {
        Optional<CuentaDTO> cuenta = cuentaService.obtenerCuentaPorId(id);
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
        CuentaDTO nuevaCuenta = cuentaService.crearCuenta(cuentaDTO);
        ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                .estado(HttpStatus.CREATED.value())
                .mensaje("Cuenta creada exitosamente")
                .datos(nuevaCuenta)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> actualizarCuenta(@PathVariable Long id, @Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO cuentaActualizada = cuentaService.actualizarCuenta(id, cuentaDTO);
        ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Cuenta actualizada exitosamente")
                .datos(cuentaActualizada)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<CuentaDTO>>> obtenerCuentasPorClienteId(@PathVariable Long clienteId) {
        List<CuentaDTO> cuentas = cuentaService.obtenerCuentasPorClienteId(clienteId);
        ApiResponse<List<CuentaDTO>> respuesta = ApiResponse.<List<CuentaDTO>>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Lista de cuentas del cliente recuperada exitosamente")
                .datos(cuentas)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCuenta(@PathVariable Long id) {
        cuentaService.eliminarCuenta(id);
        ApiResponse<Void> respuesta = ApiResponse.<Void>builder()
                .estado(HttpStatus.NO_CONTENT.value())
                .mensaje("Cuenta eliminada exitosamente")
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/depositar/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> depositar(@PathVariable Long id, @RequestParam Double monto) {
        CuentaDTO cuentaActualizada = cuentaService.depositar(id, monto);
        ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Depósito realizado con éxito")
                .datos(cuentaActualizada)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @PutMapping("/retirar/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> retirar(@PathVariable Long id, @RequestParam Double monto) {
        CuentaDTO cuentaActualizada = cuentaService.retirar(id, monto);
        ApiResponse<CuentaDTO> respuesta = ApiResponse.<CuentaDTO>builder()
                .estado(HttpStatus.OK.value())
                .mensaje("Retiro realizado con éxito")
                .datos(cuentaActualizada)
                .build();
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}
