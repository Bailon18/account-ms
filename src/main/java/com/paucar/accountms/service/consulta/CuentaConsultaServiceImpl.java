package com.paucar.accountms.service.consulta;

import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.mapper.CuentaMapper;
import com.paucar.accountms.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaConsultaServiceImpl implements CuentaConsultaService {

    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;
    
    @Override
    public List<CuentaDTO> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll().stream()
                .map(cuentaMapper::convertEntidadADto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CuentaDTO> obtenerCuentaPorId(Long id) {
        return cuentaRepository.findById(id)
                .map(cuentaMapper::convertEntidadADto);
    }

    @Override
    public List<CuentaDTO> obtenerCuentasPorClienteId(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId).stream()
                .map(cuentaMapper::convertEntidadADto)
                .collect(Collectors.toList());
    }
}
