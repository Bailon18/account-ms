package com.paucar.accountms.service.gestion;


import com.paucar.accountms.dto.CuentaDTO;

public interface CuentaGestionService {

    CuentaDTO crearCuenta(CuentaDTO cuentaDTO);
    CuentaDTO actualizarCuenta(Long id, CuentaDTO cuentaDTO);
    void eliminarCuenta(Long id);

}
