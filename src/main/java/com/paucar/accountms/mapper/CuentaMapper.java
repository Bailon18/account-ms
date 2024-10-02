package com.paucar.accountms.mapper;

import com.paucar.accountms.dto.CuentaDTO;
import com.paucar.accountms.model.Cuenta;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    CuentaDTO convertEntidadADto(Cuenta cuenta);
    Cuenta convertirDtoAEntidad(CuentaDTO cuentaDTO);

}
