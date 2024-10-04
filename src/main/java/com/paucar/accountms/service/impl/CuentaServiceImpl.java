package com.paucar.accountms.service.impl;

import com.paucar.accountms.client.ClienteFeign;
import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.exception.CuentaYaExisteException;
import com.paucar.accountms.exception.CuentaNoEncontradaException;
import com.paucar.accountms.exception.ClienteNoEncontradoException;
import com.paucar.accountms.exception.SaldoInsuficienteException;
import com.paucar.accountms.mapper.CuentaMapper;
import com.paucar.accountms.model.Cuenta;
import com.paucar.accountms.model.Cliente;
import com.paucar.accountms.repository.CuentaRepository;
import com.paucar.accountms.service.CuentaService;
import com.paucar.accountms.util.ApiResponse;
import com.paucar.accountms.util.EstadoCuenta;
import com.paucar.accountms.util.TipoCuenta;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteFeign clienteFeign;
    private final CuentaMapper cuentaMapper;

    public CuentaServiceImpl(CuentaRepository cuentaRepository,
                             ClienteFeign clienteFeign,
                             CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.clienteFeign = clienteFeign;
        this.cuentaMapper = cuentaMapper;
    }

    @Override
    public List<CuentaDTO> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll().stream()
                .map(cuentaMapper::convertEntidadADto)
                .sorted(Comparator.comparing(CuentaDTO::getId).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CuentaDTO> obtenerCuentaPorId(Long id) {
        return cuentaRepository.findById(id)
                .map(cuentaMapper::convertEntidadADto);
    }

    @Override
    public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {

        // Validar la existencia del cliente usando el cliente Feign.
        ResponseEntity<ApiResponse<Cliente>> respuesta = clienteFeign.obtenerCliente(cuentaDTO.getClienteId());

        if (respuesta.getBody() == null || respuesta.getBody().getDatos() == null) {
            throw new ClienteNoEncontradoException("El cliente con ID: " + cuentaDTO.getClienteId() + " no existe.");
        }

        Cliente cliente = respuesta.getBody().getDatos();

        if (cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            throw new CuentaYaExisteException("La cuenta con número " + cuentaDTO.getNumeroCuenta() + " ya existe.");
        }

        Cuenta cuenta = cuentaMapper.convertirDtoAEntidad(cuentaDTO);
        cuenta.setEstado(EstadoCuenta.ACTIVO);


        if (cuenta.getNumeroCuenta() == null || cuenta.getNumeroCuenta().isEmpty()) {
            cuenta.setNumeroCuenta(generarNumeroCuenta());
        }

        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return cuentaMapper.convertEntidadADto(cuentaGuardada);
    }


    @Override
    public CuentaDTO actualizarCuenta(Long id, CuentaDTO cuentaDTO) {

        Cuenta cuentaExistente = cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada con el ID: " + id));

        cuentaExistente.setSaldo(cuentaDTO.getSaldo());
        cuentaExistente.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuentaExistente.setEstado(cuentaDTO.getEstado());

        Cuenta cuentaGuardada = cuentaRepository.save(cuentaExistente);
        return cuentaMapper.convertEntidadADto(cuentaGuardada);
    }

    @Override
    public void eliminarCuenta(Long id) {
        if (!cuentaRepository.existsById(id)) {
            throw new CuentaNoEncontradaException("Cuenta no encontrada con el ID: " + id);
        }
        cuentaRepository.deleteById(id);
    }

    @Override
    public CuentaDTO depositar(Long id, Double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor que 0.");
        }

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada con el ID: " + id));

        if (cuenta.getEstado() != EstadoCuenta.ACTIVO) {
            throw new IllegalStateException("No se pueden realizar transacciones en cuentas que no estén ACTIVAS.");
        }

        cuenta.setSaldo(cuenta.getSaldo() + monto);
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return cuentaMapper.convertEntidadADto(cuentaGuardada);
    }

    @Override
    public List<CuentaDTO> obtenerCuentasPorClienteId(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream()
                .map(cuentaMapper::convertEntidadADto)
                .collect(Collectors.toList());
    }

    @Override
    public CuentaDTO retirar(Long id, Double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor que 0.");
        }

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada con el ID: " + id));

        if (cuenta.getEstado() != EstadoCuenta.ACTIVO) {
            throw new IllegalStateException("No se pueden realizar transacciones en cuentas que no estén ACTIVAS.");
        }

        if (cuenta.getTipoCuenta() == TipoCuenta.AHORROS && cuenta.getSaldo() < monto) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la cuenta de ahorros.");
        }
        if (cuenta.getTipoCuenta() == TipoCuenta.CORRIENTE && cuenta.getSaldo() - monto < -500) {
            throw new SaldoInsuficienteException("Límite de sobregiro alcanzado en la cuenta corriente.");
        }

        cuenta.setSaldo(cuenta.getSaldo() - monto);
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return cuentaMapper.convertEntidadADto(cuentaGuardada);
    }

    private String generarNumeroCuenta() {
        return String.valueOf((long) (Math.random() * 10000000000L));
    }
}
