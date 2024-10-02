package com.paucar.accountms.util;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private int estado;
    private String mensaje;
    private T datos;
}
