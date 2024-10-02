package com.paucar.accountms.service;

import com.paucar.accountms.dto.CuentaDTO;

import java.util.List;
import java.util.Optional;

public interface CuentaService {

    List<CuentaDTO> obtenerTodasLasCuentas();
    Optional<CuentaDTO> obtenerCuentaPorId(Long id);
    CuentaDTO crearCuenta(CuentaDTO cuentaDTO);
    CuentaDTO actualizarCuenta(Long id, CuentaDTO cuentaDTO);
    void eliminarCuenta(Long id);
    CuentaDTO depositar(Long id, Double monto);
    CuentaDTO retirar(Long id, Double monto);
    List<CuentaDTO> obtenerCuentasPorClienteId(Long clienteId);
}
