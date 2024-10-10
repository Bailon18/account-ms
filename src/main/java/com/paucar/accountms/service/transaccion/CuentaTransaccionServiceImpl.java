package com.paucar.accountms.service.transaccion;

import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.exception.CuentaNoEncontradaException;
import com.paucar.accountms.mapper.CuentaMapper;
import com.paucar.accountms.model.Cuenta;
import com.paucar.accountms.repository.CuentaRepository;
import com.paucar.accountms.service.validacion.CuentaValidacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CuentaTransaccionServiceImpl implements CuentaTransaccionService {

    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;
    private final CuentaValidacionService cuentaValidacionService;

    @Override
    public CuentaDTO depositar(String numeroCuenta, Double monto) {
        cuentaValidacionService.validarMonto(monto);

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + numeroCuenta));

        cuentaValidacionService.validarEstadoActivo(cuenta);

        cuenta.setSaldo(cuenta.getSaldo() + monto);
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return cuentaMapper.convertEntidadADto(cuentaGuardada);
    }

    @Override
    public CuentaDTO retirar(String numeroCuenta, Double monto) {
        cuentaValidacionService.validarMonto(monto);

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + numeroCuenta));

        cuentaValidacionService.validarEstadoActivo(cuenta);
        cuentaValidacionService.validarSaldo(cuenta, monto);

        cuenta.setSaldo(cuenta.getSaldo() - monto);
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return cuentaMapper.convertEntidadADto(cuentaGuardada);
    }

    @Transactional
    @Override
    public Boolean transferir(String numeroCuentaOrigen, String numeroCuentaDestino, Double monto) {

        Cuenta cuentaOrigen = cuentaValidacionService.validarCuenta(numeroCuentaOrigen, monto, "origen");
        Cuenta cuentaDestino = cuentaValidacionService.validarCuenta(numeroCuentaDestino, 0.0, "destino");

        this.retirar(cuentaOrigen.getNumeroCuenta(), monto);
        this.depositar(cuentaDestino.getNumeroCuenta(), monto);

        return true;
    }
}
