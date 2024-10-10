package com.paucar.accountms.service.validacion;


import com.paucar.accountms.model.Cuenta;

public interface CuentaValidacionService {
    void validarEstadoActivo(Cuenta cuenta);
    void validarSaldo(Cuenta cuenta, Double monto);
    void validarMonto(Double monto);
    Cuenta validarCuenta(String numeroCuenta, Double monto, String tipoCuenta);
}
