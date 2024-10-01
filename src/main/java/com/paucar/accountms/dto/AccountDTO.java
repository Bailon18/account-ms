package com.paucar.accountms.dto;


import com.paucar.accountms.util.EstadoCuenta;
import com.paucar.accountms.util.TipoCuenta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {

    private Long id;
    private String numeroCuenta;
    private Double saldo;
    private TipoCuenta tipoCuenta;
    private Long clienteId;
    private EstadoCuenta estado;

}
