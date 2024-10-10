package com.paucar.accountms.service.consulta;

import com.paucar.accountms.dto.CuentaDTO;

import java.util.List;
import java.util.Optional;

public interface CuentaConsultaService {

    List<CuentaDTO> obtenerTodasLasCuentas();
    Optional<CuentaDTO> obtenerCuentaPorId(Long id);
    List<CuentaDTO> obtenerCuentasPorClienteId(Long clienteId);

}
