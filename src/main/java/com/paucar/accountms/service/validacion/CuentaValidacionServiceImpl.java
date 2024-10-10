package com.paucar.accountms.service.validacion;

import com.paucar.accountms.exception.CuentaNoEncontradaException;
import com.paucar.accountms.exception.SaldoInsuficienteException;
import com.paucar.accountms.model.Cuenta;
import com.paucar.accountms.repository.CuentaRepository;
import com.paucar.accountms.util.EstadoCuenta;
import com.paucar.accountms.util.TipoCuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuentaValidacionServiceImpl implements CuentaValidacionService {

    private final CuentaRepository cuentaRepository;

    @Override
    public void validarEstadoActivo(Cuenta cuenta) {
        if (cuenta.getEstado() != EstadoCuenta.ACTIVO) {
            throw new IllegalStateException("No se pueden realizar transacciones en cuentas que no estén ACTIVAS.");
        }
    }

    @Override
    public void validarSaldo(Cuenta cuenta, Double monto) {
        // Utilizando un switch para eliminar duplicaciones de condiciones
        switch (cuenta.getTipoCuenta()) {
            case AHORROS -> validarSaldoAhorros(cuenta, monto);
            case CORRIENTE -> validarSaldoCorriente(cuenta, monto);
            default -> throw new IllegalArgumentException("Tipo de cuenta no soportado: " + cuenta.getTipoCuenta());
        }
    }

    private void validarSaldoAhorros(Cuenta cuenta, Double monto) {
        if (cuenta.getSaldo() < monto) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la cuenta de ahorros.");
        }
    }

    private void validarSaldoCorriente(Cuenta cuenta, Double monto) {
        if (cuenta.getSaldo() - monto < -500) {
            throw new SaldoInsuficienteException("Límite de sobregiro alcanzado en la cuenta corriente.");
        }
    }

    @Override
    public void validarMonto(Double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que 0.");
        }
    }

    @Override
    public Cuenta validarCuenta(String numeroCuenta, Double monto, String tipoCuenta) {

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException(
                        "No se pudo realizar la transferencia: la cuenta de " + tipoCuenta +
                                " con el número [" + numeroCuenta + "] no fue encontrada."));

        // Validar si la cuenta está activa
        if (cuenta.getEstado() != EstadoCuenta.ACTIVO) {
            throw new IllegalStateException(
                    "No se pudo realizar la transferencia: la cuenta de " + tipoCuenta +
                            " con el número [" + numeroCuenta + "] está INACTIVA.");
        }

        // Validar el saldo si es cuenta de origen
        if (tipoCuenta.equals("origen") && cuenta.getTipoCuenta() == TipoCuenta.AHORROS && cuenta.getSaldo() < monto) {
            throw new SaldoInsuficienteException(
                    "No se pudo realizar la transferencia: saldo insuficiente en la cuenta de origen [" + numeroCuenta + "].");
        }

        return cuenta;
    }
}
