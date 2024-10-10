package com.paucar.accountms.client.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cliente {
    private long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
}
