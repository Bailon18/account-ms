package com.paucar.accountms.service.gestion;

import com.paucar.accountms.client.ClienteFeign;
import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.exception.CuentaYaExisteException;
import com.paucar.accountms.exception.CuentaNoEncontradaException;
import com.paucar.accountms.exception.ClienteNoEncontradoException;
import com.paucar.accountms.mapper.CuentaMapper;
import com.paucar.accountms.model.Cuenta;
import com.paucar.accountms.client.dto.Cliente;
import com.paucar.accountms.repository.CuentaRepository;
import com.paucar.accountms.util.ApiResponse;
import com.paucar.accountms.util.EstadoCuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuentaGestionServiceImpl implements CuentaGestionService {

    private final CuentaRepository cuentaRepository;
    private final ClienteFeign clienteFeign;
    private final CuentaMapper cuentaMapper;

    @Override
    public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {
        // Validar la existencia del cliente usando el cliente Feign.
        ResponseEntity<ApiResponse<Cliente>> respuesta = clienteFeign.obtenerCliente(cuentaDTO.getClienteId());

        if (respuesta.getBody() == null || respuesta.getBody().getDatos() == null) {
            throw new ClienteNoEncontradoException("El cliente con ID: " + cuentaDTO.getClienteId() + " no existe.");
        }

        // Verificar si la cuenta ya existe en el repositorio.
        if (cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            throw new CuentaYaExisteException("La cuenta con número " + cuentaDTO.getNumeroCuenta() + " ya existe.");
        }

        // Mapear DTO a entidad de Cuenta y establecer estado.
        Cuenta cuenta = cuentaMapper.convertirDtoAEntidad(cuentaDTO);
        cuenta.setEstado(EstadoCuenta.ACTIVO);

        // Si el número de cuenta es nulo o vacío, generar un nuevo número de cuenta.
        if (cuenta.getNumeroCuenta() == null || cuenta.getNumeroCuenta().isEmpty()) {
            cuenta.setNumeroCuenta(generarNumeroCuenta());
        }

        // Guardar la cuenta en el repositorio.
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return cuentaMapper.convertEntidadADto(cuentaGuardada);
    }

    @Override
    public CuentaDTO actualizarCuenta(Long id, CuentaDTO cuentaDTO) {
        // Buscar la cuenta existente por ID.
        Cuenta cuentaExistente = cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada con el ID: " + id));

        // Actualizar los valores de la cuenta.
        cuentaExistente.setSaldo(cuentaDTO.getSaldo());
        cuentaExistente.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuentaExistente.setEstado(cuentaDTO.getEstado());

        // Guardar los cambios en el repositorio.
        Cuenta cuentaGuardada = cuentaRepository.save(cuentaExistente);
        return cuentaMapper.convertEntidadADto(cuentaGuardada);
    }

    @Override
    public void eliminarCuenta(Long id) {
        // Verificar si la cuenta existe por ID.
        if (!cuentaRepository.existsById(id)) {
            throw new CuentaNoEncontradaException("Cuenta no encontrada con el ID: " + id);
        }

        // Eliminar la cuenta del repositorio.
        cuentaRepository.deleteById(id);
    }

    // Generador de número de cuenta.
    private String generarNumeroCuenta() {
        return String.valueOf((long) (Math.random() * 10000000000L));
    }
}
