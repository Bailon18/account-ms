package com.paucar.accountms.service.transaccion;

import com.paucar.accountms.dto.CuentaDTO;

public interface CuentaTransaccionService {

    CuentaDTO depositar(String numeroCuenta, Double monto);
    CuentaDTO retirar(String numeroCuenta, Double monto);
    Boolean transferir(String numeroCuentaOrigen, String numeroCuentaDestino, Double monto);
}
