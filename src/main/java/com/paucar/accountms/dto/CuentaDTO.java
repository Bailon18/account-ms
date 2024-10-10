package com.paucar.accountms.dto;


import com.paucar.accountms.util.EstadoCuenta;
import com.paucar.accountms.util.TipoCuenta;
import lombok.*;

@Builder
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {

    private Long id;
    private String numeroCuenta;
    private Double saldo;
    private TipoCuenta tipoCuenta;
    private Long clienteId;
    private EstadoCuenta estado;

}
